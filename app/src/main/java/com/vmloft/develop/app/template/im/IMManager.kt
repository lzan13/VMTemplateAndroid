package com.vmloft.develop.app.template.im

import android.content.Context
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.request.bean.User

import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.router.IMRouter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Create by lzan13 on 2021/5/23 09:39
 * 描述：IM 管理类
 */
class IMManager {

    companion object {
        val instance: IMManager by lazy {
            IMManager()
        }
    }

    /**
     * 初始化 IM
     */
    fun init(context: Context) {
        IM.instance.init(context, IMListener())
    }

    /**
     * 登录 IM
     */
    suspend fun signIn() = withContext(Dispatchers.IO) {
        val user = SignManager.instance.getCurrUser() ?: User()
        IM.instance.signIn(user.id, user.password)
    }

    fun exit() {
        IM.instance.signOut(false)
    }

    /**
     * 跳转到单聊
     */
    fun goChat(chatId: String) {
        IMRouter.goChat(chatId)
    }

    /**
     * 跳转到快速聊天
     */
    fun goChatFast(chatId: String) {
        IMRouter.goChatFast(chatId)
    }

    /**
     * 跳转到聊天室界面
     */
    fun goChatRoom(chatId: String) {
        IMRouter.goChatRoom(chatId)
    }

    /**
     * ---------------------------------------------------------------------
     * 设置通知
     */
    var isNotify: Boolean
        get() = IM.instance.isNotify
        set(open) {
            IM.instance.isNotify = open
        }

    /**
     * 设置通知详情
     */
    var isNotifyDetail: Boolean
        get() = IM.instance.isNotifyDetail
        set(open) {
            IM.instance.isNotifyDetail = open
        }
    /**
     * ---------------------------------------------------------------------
     * 判断是否开启圆形头像
     */
    /**
     * 设置开启圆形头像
     */
    var isCircleAvatar: Boolean
        get() = IM.instance.isCircleAvatar
        set(open) {
            IM.instance.isCircleAvatar = open
        }
    /**
     * 判断是否扬声器播放语音
     */
    /**
     * 设置是否扬声器播放语音
     */
    var isSpeakerVoice: Boolean
        get() = IM.instance.isSpeakerVoice
        set(speaker) {
            IM.instance.isSpeakerVoice = speaker
        }

}