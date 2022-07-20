package com.vmloft.develop.library.im.connect

import com.hyphenate.EMConnectionListener
import com.hyphenate.EMError
import com.hyphenate.chat.EMClient
import com.vmloft.develop.library.base.event.LDEventBus

import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.tools.utils.logger.VMLog

/**
 * Create by lzan13 on 2019/5/9 10:58
 * 描述：IM 链接监听管理类
 */
object IMConnectionManager {

    fun init() {
        EMClient.getInstance().addConnectionListener(object : EMConnectionListener {
            override fun onConnected() {
                VMLog.d("与聊天服务器链接成功")
                // 通知其他地方链接成功
                LDEventBus.post(IMConstants.ConnectStatus.connectStatusEvent, IMConstants.ConnectStatus.connected)
            }

            override fun onDisconnected(code: Int) {
                VMLog.d("链接断开 $code")
                if (code == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 其他设备登录，自己被踢
                    IM.signOut(false)
                } else if (code == EMError.USER_REMOVED) {
                    // 账户被强制下线
                    IM.signOut(false)
                } else {
                    // 连接不到服务器
                }
                // 通知其他地方链接断开
                LDEventBus.post(IMConstants.ConnectStatus.connectStatusEvent, IMConstants.ConnectStatus.disconnect)
            }
        })
    }

    /**
     * 判断是否链接到 IM 服务器
     */
    val isConnected: Boolean
        get() = EMClient.getInstance().isConnected

}