package com.vmloft.develop.library.im.common

import android.net.Uri

import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.data.bean.Attachment
import com.vmloft.develop.library.data.bean.Gift
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.bean.IMSignal
import com.vmloft.develop.library.im.core.WSManager
import com.vmloft.develop.library.tools.utils.VMDate
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMUtils
import com.vmloft.develop.library.tools.utils.logger.VMLog


/**
 * Create by lzan13 2021/5/20
 * 描述：聊天管理
 */
object IMChatManager {

    // 当前聊天对象 Id
    private var currChatId: String? = null

    /**
     * 判断是否正在和当前用户聊天
     */
    fun isCurrChat(chatId: String): Boolean {
        return chatId == currChatId
    }

    /**
     * 记录当前会话
     */
    fun setCurrChatId(chatId: String) {
        currChatId = chatId
    }

    /**
     * 创建消息，默认支持以下类型
     * TXT, IMAGE, VIDEO, LOCATION, VOICE, FILE, CMD
     */
    fun createMessage(id: String, content: String, isSend: Boolean = true): IMMessage {
        val message = IMMessage(body = content)
        if (isSend) {
            message.from = SignManager.getSignId()
            message.to = id
        } else {
            message.from = id
            message.to = SignManager.getSignId()
            message.status = IMConstants.MsgStatus.imSuccess
        }
        message.time = VMDate.currentMilli()
        message.localId = (message.time * 1000 + VMUtils.random(1000)).toString()
        message.id = message.localId
        return message
    }

    /**
     * 创建一条文本消息
     *
     * @param content 消息内容
     * @param chatId  消息对方
     * @param isSend  是否为发送消息
     */
    fun createTextMessage(chatId: String, content: String, isSend: Boolean = true): IMMessage {
        val message = createMessage(chatId, content, isSend)
        return message
    }

    /**
     * 创建一条图片消息
     *
     * @param chatId 接收者
     * @param uri 图片路径
     * @param isSend 是否为发送消息
     */
    fun createPictureMessage(chatId: String, uri: Uri, isSend: Boolean = true): IMMessage {
        val message = createMessage(chatId, "[${VMStr.byRes(R.string.im_picture)}]", isSend)
        message.type = IMConstants.MsgType.imPicture
        val attachment = Attachment(uri = uri)
        message.attachments = mutableListOf(attachment)
        return message
    }

    /**
     * 创建语音消息
     *
     * @param uri   语音文件的路径
     * @param time   语音持续时间
     * @param chatId 接收者
     * @param isSend 是否为发送消息
     */
    fun createVoiceMessage(chatId: String, uri: Uri, time: Int, isSend: Boolean = true): IMMessage {
        val message = createMessage(chatId, "[${VMStr.byRes(R.string.im_voice)}]", isSend)
        message.type = IMConstants.MsgType.imVoice
        val attachment = Attachment(uri = uri, duration = time)
        message.attachments = mutableListOf(attachment)
        return message
    }

    /**
     * 创建礼物消息
     *
     * @param chatId 接收者
     * @param gift 礼物
     * @param isSend 是否为发送消息
     */
    fun createGiftMessage(chatId: String, gift: Gift, isSend: Boolean = true): IMMessage {
        val message = createMessage(chatId, gift.title, isSend)
        message.type = IMConstants.MsgType.imGift
        message.attachments.add(gift.cover)
        if (gift.type == 1) {
            message.attachments.add(gift.animation)
        }
        return message
    }

    /**
     * 创建信令消息
     *
     * @param action 要发送的指定
     * @param to     接收者
     */
    fun createSignal(action: String, to: String): IMSignal {
        val message = IMSignal()
        message.from = SignManager.getSignId()
        message.to = to
        message.action = action
        message.time = VMDate.currentMilli()
        message.localId = (message.time * 1000 + VMUtils.random(1000)).toString()
        message.id = message.localId
        return message
    }

    /**
     * 最终调用发送信息方法
     *
     * @param message  需要发送的消息
     */
    fun sendMessage(
        message: IMMessage,
        success: () -> Unit = {},
        error: (Int, Any) -> Unit = { _: Int, _: Any -> },
        progress: (Int, String) -> Unit = { _, _ -> },
    ) {
        if (message.resendCount == 0) {
            // 添加消息
            IMConversationManager.addMessage(message)
        } else {
            message.status = IMConstants.MsgStatus.imLoading
            IMConversationManager.updateMessage(message)
        }

        if (!WSManager.isConnect()) {
            error.invoke(-1, "链接未建立，无法发送消息")
            message.status = IMConstants.MsgStatus.imFailed
            IMConversationManager.updateMessage(message)
            return
        }
        // 发送消息
        WSManager.sendMsg(message) { code, obj ->
            if (code == 0) {
                val msg = JsonUtils.fromJson<IMMessage>(obj.toString())
                VMLog.i("消息发送成功 msgId ${message.id} - $message")

                message.status = IMConstants.MsgStatus.imSuccess
                message.time = msg.time
                message.id = msg.id
                IMConversationManager.updateMessage(message)

                success.invoke()
            } else {
                VMLog.i("消息发送失败 $code, $obj")
                message.status = IMConstants.MsgStatus.imFailed
                IMConversationManager.updateMessage(message)
                error.invoke(code, obj)
            }
        }
    }

    /**
     * 最终调用发送信令方法
     *
     * @param signal  需要发送的消息
     */
    fun sendSignal(
        signal: IMSignal,
        success: () -> Unit = {},
        error: (Int, Any) -> Unit = { _: Int, _: Any -> },
    ) {
        if (!WSManager.isConnect()) {
            error.invoke(-1, "链接未建立，无法发送消息")
            return
        }
        WSManager.sendSignal(signal) { code, obj ->
            if (code == 0) {
                success.invoke()
            } else {
                error.invoke(code, obj)
            }
        }
    }

    /**
     * 发送通话信令消息
     */
    fun sendCallSignal(id: String, status: Int) {
        // 创建CMD 消息的消息体 并设置 action 为通话信令
        val signal = createSignal(IMConstants.Call.signalCall, id)
        signal.setAttribute(IMConstants.Call.extCallStatus, status)
        sendSignal(signal)
    }

    /**
     * 发送输入状态
     */
    fun sendInputStatusSignal(id: String) {
        sendSignal(createSignal(IMConstants.Common.signalInputStatus, id))
    }

    /**
     * 输入状态处理
     */
    fun receiveInputStatus(signal: IMSignal) {
        // 如果命令消息超过一分钟，则不进行处理
        if (System.currentTimeMillis() - signal.time > CConstants.timeMinute) return
        // 如果不是发给当前会话的，就不需要处理
        if (isCurrChat(signal.chatId)) {
            LDEventBus.post(IMConstants.Common.signalInputStatus, signal)
        }
    }

    /**
     * 发送一条撤回消息的指令，这里需要和接收方协商定义
     * @param message  需要撤回的消息
     * @param error 撤回错误回调，这里不需要成功回调，因为成功会通过 updateMessage 通知界面刷新
     */
    fun sendRecallSignal(message: IMMessage, error: (Int, Any) -> Unit) {
        // 获取当前时间，用来判断后边撤回消息的时间点是否合法，这个判断不需要在接收方做，
        // 因为如果接收方之前不在线，很久之后才收到消息，将导致撤回失败
        val currTime = VMDate.currentMilli()
        // 判断当前消息的时间是否已经超过了限制时间，如果超过，则不可撤回消息
        if (currTime < message.time || currTime - message.time > CConstants.timeMinute * 5) {
            error.invoke(-1, VMStr.byRes(R.string.im_error_recall_limit_time))
            return
        }
        // 创建一个CMD 类型的消息，将需要撤回的消息通过这条 CMD 消息发送给对方
        val signal = createSignal(IMConstants.Common.signalRecallMessage, message.to)
        // 撤回信令消息id与被撤回消息id相同，这样就不需要加扩展了
        signal.id = message.id
        // 同步消息类型
        signal.chatType = message.chatType
        // 设置消息的扩展为要撤回的 msgId
//        signal.setAttribute(IMConstants.Common.extMsgId, message.id)
        // 准备工作完毕，发送消息
        sendSignal(signal, {
            message.type = IMConstants.MsgType.imSystem
            // 设置扩展为撤回消息类型，是为了区分消息的显示
            message.setAttribute(IMConstants.Common.extType, IMConstants.MsgType.imSystemRecall)
            // 更新消息，这里直接调用添加
            IMConversationManager.addMessage(message)
        }, { code, obj ->
            error.invoke(code, obj)
        })
    }

    /**
     * 收到撤回消息，这里需要和发送方协商定义，通过一个透传，并加上扩展去实现消息的撤回
     *
     * @param cmdMessage 收到的透传消息，包含需要撤回的消息的 msgId
     * @return 返回撤回结果是否成功
     */
    fun receiveRecallSignal(signal: IMSignal) {
        // 根据得到的 msgId 去本地查找这条消息，如果本地已经没有这条消息了，就不用撤回
        val msgList = IMConversationManager.getConversation(signal.chatId, signal.chatType).msgList
        val message = msgList.find { signal.id == it.id }
        message?.let {
            // 设置扩展为撤回消息类型，是为了区分消息的显示
            message.setAttribute(IMConstants.Common.extType, IMConstants.MsgType.imSystemRecall)
        }

        // TODO 如果未读，这里需要更新下未读数-1
//        if (message.isUnread) {
//            getConversation(signal.conversationId(), signal.chatType.ordinal).markMessageAsRead(msgId)
//        // 通知未读更新
//        LDEventBus.post(IMConstants.Common.changeUnreadCount, IMConversationManager.getAllUnread())
//        }
        // TODO 更新消息
//        result = EMClient.getInstance().chatManager().updateMessage(message)
//        IMConversationManager.updateMessage(message)
    }

    /**
     * 收到匹配信息
     */
    fun receiveMatchInfo(signal: IMSignal) {
        LDEventBus.post(IMConstants.Common.signalMatchInfo, signal.extend)
    }

    /**
     * 是否显示时间
     */
    fun isShowTime(position: Int, message: IMMessage): Boolean {
        if (position == 0) return true
        val list = IMConversationManager.getCacheMessages(message.chatId, message.chatType)
        VMLog.i(list.toString())
        val preMessage = list[position - 1]
        return (message.time - preMessage.time) > CConstants.timeMinute * 3
    }

    /**
     * 获取消息类型
     */
    fun getMsgType(message: IMMessage): Int {
        val extType = message.getIntAttribute(IMConstants.Common.extType, -1)

        return when (message.type) {
            IMConstants.MsgType.imText -> {
                if (extType == IMConstants.MsgType.imCall) {
                    extType
                } else {
                    IMConstants.MsgType.imText
                }
            }
            IMConstants.MsgType.imSystem -> {
                if (extType == IMConstants.MsgType.imSystemRecall || extType == IMConstants.MsgType.imSystemWelcome) {
                    extType
                } else {
                    IMConstants.MsgType.imSystemDefault
                }
            }
            IMConstants.MsgType.imCard -> {
                if (extType == IMConstants.MsgType.imCardFile || extType == IMConstants.MsgType.imCardRedPacket) {
                    extType
                } else {
                    IMConstants.MsgType.imCardDefault
                }
            }
            IMConstants.MsgType.imPicture,
            IMConstants.MsgType.imVoice,
            IMConstants.MsgType.imVideo,
            IMConstants.MsgType.imGift,
            IMConstants.MsgType.imEmotion,
            IMConstants.MsgType.imLocation,
            -> message.type
            else -> IMConstants.MsgType.imText
        }
    }

    /**
     * 获取消息摘要信息
     */
    fun getSummary(message: IMMessage?): String {
        if (message == null) return VMStr.byRes(R.string.im_empty)

        val type = getMsgType(message)
        return when (type) {
            IMConstants.MsgType.imText,
            IMConstants.MsgType.imSystemDefault,
            IMConstants.MsgType.imSystemWelcome,
            IMConstants.MsgType.imPicture,
            IMConstants.MsgType.imVoice,
            IMConstants.MsgType.imVideo,
            IMConstants.MsgType.imEmotion,
            IMConstants.MsgType.imLocation,
            IMConstants.MsgType.imCardDefault,
            -> message.body
            IMConstants.MsgType.imSystemRecall -> "[${VMStr.byRes(R.string.im_recall_already)}]"
            IMConstants.MsgType.imCardFile -> "[${VMStr.byRes(R.string.im_file)}]"
            IMConstants.MsgType.imCardRedPacket -> "[${VMStr.byRes(R.string.im_red_packet)}]"
            IMConstants.MsgType.imCall -> "[${VMStr.byRes(R.string.im_call)}-${message.body}]"
            IMConstants.MsgType.imGift -> "[${VMStr.byRes(R.string.im_gift)}-${message.body}]"
            else -> "[${VMStr.byRes(R.string.im_unknown_msg)}]"
        }
    }

}