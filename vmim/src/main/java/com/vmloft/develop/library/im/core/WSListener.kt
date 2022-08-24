package com.vmloft.develop.library.im.core

import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.bean.IMSignal
import com.vmloft.develop.library.im.call.IMCallManager
import com.vmloft.develop.library.im.common.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.common.IMConversationManager
import com.vmloft.develop.library.im.fast.IMChatFastManager
import com.vmloft.develop.library.tools.utils.logger.VMLog
import io.socket.client.Ack

import io.socket.client.Socket

/**
 * Created by lzan13 on 2021/05/21.
 * 描述：WebSocket 监听
 */
object WSListener {
    /**
     * 初始化监听
     */
    fun init(socket: Socket) {
        socket.on("connect") {
            VMLog.i("-lz-链接建立成功")
            WSManager.onConnect(true)
        }
        socket.on("disconnect") {
            VMLog.i("-lz-链接断开")
            WSManager.onConnect(false)
        }
        socket.on(IMConstants.Common.wsMessageEvent) {
            // 回复 Ack 告诉服务器收到消息
            if (it.size > 1) (it[it.size - 1] as Ack).call(0)
            // 处理消息
            val messages = JsonUtils.fromJson<List<IMMessage>>(it[0].toString())
            VMLog.i("-lz-收到消息 $messages")
            messages.forEach { message -> onReceiveMessage(message) }
        }
        socket.on(IMConstants.Common.wsSignalEvent) {
            // 回复 Ack 告诉服务器收到消息
            if (it.size > 1) (it[it.size - 1] as Ack).call(0)
            // 处理信令
            val signal = JsonUtils.fromJson<IMSignal>(it[0].toString())
            VMLog.i("-lz-收到信令 $signal")
            onSignalReceived(signal)
        }
    }

    /**
     * 收到新消息，离线消息也都是在这里获取
     * 这里在处理消息监听时根据收到的消息修改了会话对象的最后时间，是为了在会话列表中当清空了会话内容时，
     * 不用过滤掉空会话，并且能显示会话时间
     *
     * @param message 收到的新消息
     */
    fun onReceiveMessage(message: IMMessage) {
        // 先获取联系人信息再通知消息刷新
        val user = CacheManager.getUser(message.chatId)
        if (user.id.isEmpty()) {
            IMConversationManager.addMessage(message)
//            onMessageNotify(message)
        } else {
            CacheManager.getUser(message.chatId) {
                IMConversationManager.addMessage(message)
//                onMessageNotify(message)
            }
        }
    }

    /**
     * 通知新消息，这里已经获取过用户信息
     */
    private fun onMessageNotify(message: IMMessage) {
        IMConversationManager.addMessage(message)
    }

    /**
     * 收到新的信令消息
     */
    fun onSignalReceived(signal: IMSignal) {
        // 先获取联系人信息再通知消息刷新
        val user = CacheManager.getUser(signal.chatId)
        if (user.id.isEmpty()) {
            onSignalNotify(signal)
        } else {
            CacheManager.getUser(signal.from) {
                onSignalNotify(signal)
            }
        }
    }

    /**
     * 信令通知
     */
    private fun onSignalNotify(signal: IMSignal) {
        when (signal.action) {
            // 输入状态
            IMConstants.Common.signalInputStatus -> IMChatManager.receiveInputStatus(signal)
            // 快速聊天
            IMConstants.ChatFast.signalFastInput -> IMChatFastManager.receiveFastSignal(signal)
            // 用户信息改变
            IMConstants.Common.signalInfoChange -> LDEventBus.post(IMConstants.Common.signalInfoChange, signal)
            // 撤回消息
//            IMConstants.Common.signalRecallMessage -> IMChatManager.receiveRecallSignal(signal)
            // 匹配信息
            IMConstants.Common.signalMatchInfo -> IMChatManager.receiveMatchInfo(signal)
            IMConstants.Call.signalCall -> { // 通话信令
                // 如果命令消息超过一分钟，则不进行处理
                if (System.currentTimeMillis() - signal.time > CConstants.timeMinute) return
                // 通话信令状态 [IMContacts.Call]
                val status = signal.getIntAttribute(IMConstants.Call.extCallStatus, IMConstants.Call.callStatusEnd)
                IMCallManager.receiveCallSignal(signal.chatId, status)
                LDEventBus.post(IMConstants.Call.callStatusEvent, signal)
            }
            IMConstants.Call.signalRoomApplyMic -> { // 房间上麦
                // 只有发给当前房间的消息才处理
                if (IMChatManager.isCurrChat(signal.chatId)) {
                    LDEventBus.post(IMConstants.Call.signalRoomApplyMic, signal)
                }
            }
        }
    }
}