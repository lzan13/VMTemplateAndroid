package com.vmloft.develop.library.im

import android.content.Context

import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.im.call.IMCallManager
import com.vmloft.develop.library.im.common.IMConversationManager
import com.vmloft.develop.library.im.common.IMSPManager
import com.vmloft.develop.library.im.core.WSManager
import com.vmloft.develop.library.im.db.IMDatabase


/**
 * Create by lzan13 on 2019/5/20 22:22
 * 描述：IM 入口类
 */
object IM {
    private var isInit = false // 记录已经初始化

    /**
     * 初始化
     */
    fun init(context: Context) {
        if (isInit) {
            return
        }

        WSManager.init()

        // 初始化聊天管理类
//        IMChatManager.init()

        // 初始化通话管理类
        IMCallManager.init(context)

        // 初始化完成
        isInit = true
    }

    /**
     * 建立链接
     */
    fun signIn(user: User) {
        WSManager.connect(user)
    }

    /**
     * 断开链接
     */
    fun signOut() {
        IMConversationManager.reset()
        IMDatabase.close()

        WSManager.disconnect()
    }

    /**
     * 头像点击
     */
    fun onHeadClick(userId: String) {
        val user = CacheManager.getUser(userId)
        CRouter.go("/App/UserInfo", obj0 = user)
    }

    /**
     * ---------------------------------------------------------------------
     */
    /**
     * 通知开关
     */
    var isNotify: Boolean
        get() = CSPManager.isNotifyMsgSwitch()
        set(open) {
            CSPManager.setNotifyMsgSwitch(open)
        }

    /**
     * 通知详情
     */
    var isNotifyDetail: Boolean
        get() = CSPManager.isNotifyMsgDetailSwitch()
        set(open) {
            CSPManager.setNotifyMsgDetailSwitch(open)
        }
    /**
     * ---------------------------------------------------------------------
     */
    /**
     * 圆形头像
     */
    var isCircleAvatar: Boolean
        get() = IMSPManager.isCircleAvatar()
        set(open) {
            IMSPManager.putCircleAvatar(open)
        }

    /**
     * 扬声器播放语音
     */
    var isSpeakerVoice: Boolean
        get() = IMSPManager.isSpeakerVoice()
        set(speaker) {
            IMSPManager.putSpeakerVoice(speaker)
        }
}
