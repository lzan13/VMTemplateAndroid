package com.vmloft.develop.library.im.router

import android.content.Intent

import com.alibaba.android.arouter.launcher.ARouter
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.im.common.IMConstants

/**
 * Create by lzan13 on 2020-02-24 21:57
 * 描述：针对路由注解统一收口
 */
object IMRouter {

    const val imChat = "/IM/Chat"
    const val imChatFast = "/IM/ChatFast"
    const val imChatRoom = "/IM/ChatRoom"
    const val imSingleCall = "/IM/SingleCall"

    /**
     * 去聊天
     * @param chatType 聊天类型 0-单聊 1-群聊 2-聊天室
     */
    fun goChat(chatId: String, extend: String = "") {
        ARouter.getInstance().build(imChat)
            .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .withString("chatId", chatId)
            .withInt("chatType", IMConstants.ChatType.imChatSingle)
            .withString("chatExtend", extend)
            .navigation()
    }

    /**
     * 跳转快速聊天
     * @param
     */
    fun goChatFast(chatId: String, isApply: Boolean = false) {
        ARouter.getInstance().build(imChatFast)
            .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .withString("chatId", chatId)
            .withBoolean("isApply", isApply)
            .navigation()
    }

    /**
     * 去聊天房间
     * @param type 跳转类型 0-单聊 1-群聊 2-聊天室
     */
    fun goChatRoom(chatId: String, extend: String = "") {
        ARouter.getInstance().build(imChatRoom)
            .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .withString("chatId", chatId)
            .withInt("chatType", IMConstants.ChatType.imChatRoom)
            .withString("chatExtend", extend)
            .navigation()
    }

    /**
     * 去1V1通话
     * @param callId 呼叫对方
     * @param isInComingCall 是否是呼入通话
     */
    fun goSingleCall(callId: String, isInComingCall: Boolean = false) {
        ARouter.getInstance().build(imSingleCall)
            .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .withString("callId", callId)
            .withBoolean("isInComingCall", isInComingCall)
            .navigation()
    }

}