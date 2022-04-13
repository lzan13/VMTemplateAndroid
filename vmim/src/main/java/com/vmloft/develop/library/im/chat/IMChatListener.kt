package com.vmloft.develop.library.im.chat

import android.os.Bundle

import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMCmdMessageBody
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.vmloft.develop.library.base.common.CConstants

import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.notify.NotifyManager
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.call.IMCallManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.fast.IMChatFastManager

import com.vmloft.develop.library.tools.utils.logger.VMLog

/**
 * Created by lzan13 on 2021/05/21.
 *
 * 描述：消息监听实现类
 */
class IMChatListener : EMMessageListener {
    /**
     * 收到新消息，离线消息也都是在这里获取
     * 这里在处理消息监听时根据收到的消息修改了会话对象的最后时间，是为了在会话列表中当清空了会话内容时，
     * 不用过滤掉空会话，并且能显示会话时间
     *
     * @param list 收到的新消息集合，离线和在线都是走这个监听
     */
    override fun onMessageReceived(list: List<EMMessage>) {
        VMLog.i("收到新消息 $list")
        // 遍历消息集合
        for (msg in list) {
            // 更新会话时间
            val conversation: EMConversation = IMChatManager.getConversation(msg.conversationId(), msg.chatType.ordinal)
            IMChatManager.setConversationTime(conversation, msg.localTime())
            // 先获取联系人信息再通知消息刷新
            if (IM.imListener.getUser(msg.conversationId()) != null) {
                onNotifyMessage(msg)
            } else {
                IM.imListener.getUser(msg.conversationId()) {
                    onNotifyMessage(msg)
                }
            }
        }
    }

    /**
     * 通知新消息，这里已经获取过用户信息
     */
    private fun onNotifyMessage(msg: EMMessage) {
        // 接收消息数+1
        val conversation = IMChatManager.getConversation(msg.conversationId(), msg.chatType.ordinal)
        IMChatManager.setConversationMsgReceiveCountAdd(conversation)
        // 通知有新消息来了，新消息通知肯定要发送，不在当前聊天界面还要发送通知栏提醒
        LDEventBus.post(IMConstants.Common.newMsgEvent, msg)
        if (!IMChatManager.isCurrChat(msg.conversationId())) {
            val user = IM.imListener.getUser(msg.conversationId())
            val content = IMChatManager.getSummary(msg)
            val title = user?.nickname ?: user?.username ?: ""
            val bundle = Bundle()
            bundle.putString("bname", "im")
            bundle.putString("chatId", msg.conversationId())
            NotifyManager.sendNotify(content, title, bundle)
        }
    }

    /**
     * 收到新的 CMD 消息
     *
     * @param list 收到的透传消息集合
     */
    override fun onCmdMessageReceived(list: List<EMMessage>) {

        for (msg in list) {
            // 先获取联系人信息再通知消息刷新
            if (IM.imListener.getUser(msg.from) != null) {
                onNotifyCMDMessage(msg)
            } else {
                IM.imListener.getUser(msg.from) {
                    onNotifyCMDMessage(msg)
                }
            }
        }
    }

    /**
     * 通知 CMD 消息
     */
    private fun onNotifyCMDMessage(msg: EMMessage) {
        // 通知有新 CMD 消息来了，每条消息都需要单独通知，接收方根据自己需要判断后续操作
        val body: EMCmdMessageBody = msg.body as EMCmdMessageBody

        when {
            // 鼓励
            body.action() == IMConstants.Common.cmdEncourageAction -> {
                // 如果不是发给当前会话的，就不需要处理
                if (IMChatManager.isCurrChat(msg.conversationId())) {
                    LDEventBus.post(IMConstants.Common.cmdEncourageAction, msg)
                }
            }

            // 输入状态
            body.action() == IMConstants.Common.cmdInputStatusAction -> {
                if (System.currentTimeMillis() - msg.msgTime > CConstants.timeMinute) {
                    // 如果命令消息超过一分钟，则不进行处理
                    return
                }
                // 如果不是发给当前会话的，就不需要处理
                if (IMChatManager.isCurrChat(msg.conversationId())) {
                    LDEventBus.post(IMConstants.Common.cmdInputStatusAction, msg)
                }
            }

            // 快速聊天输入内容
            body.action() == IMConstants.ChatFast.cmdFastInputAction -> {
                if (System.currentTimeMillis() - msg.msgTime > CConstants.timeMinute) {
                    // 如果命令消息超过一分钟，则不进行处理
                    return
                }
                // 快速聊天信令[IMConstants.ChatFast]
                val status = msg.getIntAttribute(IMConstants.ChatFast.msgAttrFastInputStatus, IMConstants.ChatFast.fastInputStatusEnd)
                IMChatFastManager.receiveFastSignal(msg.conversationId(), status)
                LDEventBus.post(IMConstants.ChatFast.cmdFastInputAction, msg)
            }

            // 用户信息改变
            body.action() == IMConstants.Common.cmdInfoChangeAction -> {
                LDEventBus.post(IMConstants.Common.cmdInfoChangeAction, msg)
            }

            // 撤回消息
            body.action() == IMConstants.Common.cmdRecallAction -> {
                IMChatManager.receiveRecallMessage(msg)
            }

            // 通话信令
            body.action() == IMConstants.Call.cmdCallAction -> {
                if (System.currentTimeMillis() - msg.msgTime > CConstants.timeMinute) {
                    // 如果命令消息超过一分钟，则不进行处理
                    return
                }
//                if (IMCallManager.isCalling) {
//                    if (!IMChatManager.isCurrChat(msg.conversationId())) {
//                        // 通话中，但是消息信令不是当前通话对象发过来的，则忽略不处理，或者回复拒绝
//                        IMChatManager.sendCallSignal(msg.conversationId(), IMConstants.Call.callStatusBusy)
//                        return
//                    }
//                }
                // 通话信令状态 [IMContacts.Call]
                val status = msg.getIntAttribute(IMConstants.Call.msgAttrCallStatus, IMConstants.Call.callStatusEnd)
                IMCallManager.receiveCallSignal(msg.conversationId(), status)
                LDEventBus.post(IMConstants.Call.cmdCallStatusEvent, msg)
            }

            // 房间上麦
            body.action() == IMConstants.Call.cmdRoomApplyMic -> {
                // 只有发给当前房间的消息才处理
                if (IMChatManager.isCurrChat(msg.conversationId())) {
                    LDEventBus.post(IMConstants.Call.cmdRoomApplyMic, msg)
                }
            }
        }
    }

    /**
     * 收到新的已读回执
     *
     * @param list 收到已读回执消息集合
     */
    override fun onMessageRead(list: List<EMMessage>) {
        for (msg in list) {
            // 通知有消息更新了，每条消息都需要单独通知，接收方根据自己需要判断后续操作
            LDEventBus.post(IMConstants.Common.readMsgEvent, msg)
        }
    }

    /**
     * 收到新的发送回执
     *
     * @param list 收到发送回执的消息集合
     */
    override fun onMessageDelivered(list: List<EMMessage>) {
        for (msg in list) {
            // 通知有消息更新了，每条消息都需要单独通知，接收方根据自己需要判断后续操作
            LDEventBus.post(IMConstants.Common.deliveredMsgEvent, msg)
        }
    }

    override fun onMessageRecalled(list: List<EMMessage>) {
        for (msg in list) {
            // 通知有消息更新了，每条消息都需要单独通知，接收方根据自己需要判断后续操作
            LDEventBus.post(IMConstants.Common.recallMsgEvent, msg)
        }
    }

    /**
     * 消息的状态改变
     *
     * @param message 发生改变的消息
     * @param any  包含改变的消息
     */
    override fun onMessageChanged(message: EMMessage, any: Any) {
        VMLog.i("消息改变 $message $any")
        // 通知有消息更新了，接收方根据自己需要判断后续操作
        LDEventBus.post(IMConstants.Common.changeMsgEvent, any)
    }


}