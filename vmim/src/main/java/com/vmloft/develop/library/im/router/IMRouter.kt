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
    const val imGiftAnim = "/IM/GiftAnim"

    /**
     * 去聊天
     * @param chatId 聊天Id
     * @param extend 扩展信息
     */
    fun goChat(chatId: String, extend: String = "") {
        CRouter.go(imChat, IMConstants.ChatType.imSingle, str0 = chatId, str1 = extend, flags = Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 跳转快速聊天
     * @param chatId 聊天Id
     * @param isApply 是否是呼入
     */
    fun goChatFast(chatId: String, isApply: Boolean = false) {
        CRouter.go(imChatFast, what = if (isApply) 0 else 1, str0 = chatId, flags = Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 去聊天房间
     * @param chatId 聊天Id
     */
    fun goChatRoom(chatId: String) {
        CRouter.go(imChatRoom, IMConstants.ChatType.imRoom, str0 = chatId, flags = Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 去1V1通话
     * @param callId 呼叫对方
     * @param isInComing 是否是呼入
     */
    fun goSingleCall(callId: String, isInComing: Boolean = false) {
        CRouter.go(imSingleCall, what = if (isInComing) 0 else 1, str0 = callId, flags = Intent.FLAG_ACTIVITY_NEW_TASK)
    }

}