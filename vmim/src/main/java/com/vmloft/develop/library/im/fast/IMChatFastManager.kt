package com.vmloft.develop.library.im.fast


import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.im.bean.IMSignal
import com.vmloft.develop.library.im.common.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.router.IMRouter

/**
 * Create by lzan13 2021/05/26
 * 描述：快速聊天管理
 */
object IMChatFastManager {

    private var fastStatus: Int = IMConstants.ChatFast.fastInputStatusEnd

    /**
     * ----------------------------------------------------------------
     * 快速聊天信令处理
     */
    /**
     * 发送通话信号，发送通话信令状态 类型查看[IMConstants.ChatFast]
     */
    fun sendFastSignal(chatId: String, status: Int, content: String = "", len: Int = 0) {
        // 记录下状态
        fastStatus = status
        if (status == IMConstants.ChatFast.fastInputStatusApply) {
        } else if (status == IMConstants.ChatFast.fastInputStatusAgree) {
        } else if (status == IMConstants.ChatFast.fastInputStatusReject) {
        } else if (status == IMConstants.ChatFast.fastInputStatusBusy) {
        } else {
        }

        val signal = IMChatManager.createSignal(IMConstants.ChatFast.signalFastInput, chatId)
        signal.setAttribute(IMConstants.ChatFast.extFastInputStatus, status)
        signal.setAttribute(IMConstants.ChatFast.extFastInputContent, content)
        signal.setAttribute(IMConstants.ChatFast.extFastInputLen, len)
        IMChatManager.sendSignal(signal)
    }

    /**
     * 收到快速聊天信号 类型查看[IMConstants.ChatFast]
     */
    fun receiveFastSignal(signal: IMSignal) {
        // 如果命令消息超过一分钟，则不进行处理
        if (System.currentTimeMillis() - signal.time > CConstants.timeMinute) return

        val chatId = signal.chatId
        val status = signal.getIntAttribute(IMConstants.ChatFast.extFastInputStatus, IMConstants.ChatFast.fastInputStatusEnd)

        // 收到申请时需要判断下是否忙碌
        if (status == IMConstants.ChatFast.fastInputStatusApply) {
            if (fastStatus == IMConstants.ChatFast.fastInputStatusApply || fastStatus == IMConstants.ChatFast.fastInputStatusAgree) {
                sendFastSignal(chatId, IMConstants.ChatFast.fastInputStatusBusy)
                return
            } else {
                IMRouter.goChatFast(chatId, true)
            }
        }
        // 记录下状态
        fastStatus = status
        if (status == IMConstants.ChatFast.fastInputStatusAgree) {
        } else if (status == IMConstants.ChatFast.fastInputStatusReject) {
        } else if (status == IMConstants.ChatFast.fastInputStatusBusy) {
        } else if (status == IMConstants.ChatFast.fastInputStatusEnd) {
        }

        LDEventBus.post(IMConstants.ChatFast.signalFastInput, signal)
    }

}