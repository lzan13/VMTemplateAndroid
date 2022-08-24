package com.vmloft.develop.app.template.im

import android.content.Context

import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.router.IMRouter

/**
 * Create by lzan13 on 2021/5/23 09:39
 * 描述：IM 管理类
 */
object IMManager {

    /**
     * 初始化 IM
     */
    fun init(context: Context) {
        IM.init(context)
    }

    /**
     * 建立链接，同时会进行认证
     */
    fun signIn(user: User) {
        IM.signIn(user)
    }

    /**
     * 断开链接
     */
    fun signOut() {
        IM.signOut()
    }

    /**
     * 跳转到单聊
     */
    fun goChat(chatId: String, extend: String = "") {
        IMRouter.goChat(chatId, extend)
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
        get() = IM.isNotify
        set(open) {
            IM.isNotify = open
        }

    /**
     * 设置通知详情
     */
    var isNotifyDetail: Boolean
        get() = IM.isNotifyDetail
        set(open) {
            IM.isNotifyDetail = open
        }
    /**
     * ---------------------------------------------------------------------
     * 判断是否开启圆形头像
     */
    /**
     * 设置开启圆形头像
     */
    var isCircleAvatar: Boolean
        get() = IM.isCircleAvatar
        set(open) {
            IM.isCircleAvatar = open
        }
    /**
     * 判断是否扬声器播放语音
     */
    /**
     * 设置是否扬声器播放语音
     */
    var isSpeakerVoice: Boolean
        get() = IM.isSpeakerVoice
        set(speaker) {
            IM.isSpeakerVoice = speaker
        }

}