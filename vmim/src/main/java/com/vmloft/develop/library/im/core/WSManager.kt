package com.vmloft.develop.library.im.core

import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.bean.IMSignal
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.common.IMConversationManager

import io.socket.client.IO
import io.socket.client.Socket

import java.net.URI

/**
 * Create by lzan13 on 2022/8/11
 * 描述：WebSocket 管理类
 */
object WSManager {

    private var isConnect = false // 记录是否建立链接成功

    private var socket: Socket? = null
    private lateinit var uri: URI
    private lateinit var options: IO.Options

    /**
     * 初始化
     */
    fun init() {
        uri = URI.create(IMConstants.imHost())
        options = IO.Options()
        options.path = "/im"
        options.transports = arrayOf("websocket")
    }

    /**
     * 建立链接
     */
    fun connect(user: User) {
        options.query = "token=${user.token}&userId=${user.id}"
        socket = IO.socket(uri, options)
        socket?.let {
            WSListener.init(it)
            IMConversationManager.loadAllConversationFromDB()
            it.connect()
        }
    }

    /**
     * 断开链接
     */
    fun disconnect() {
        // 退出前检查一下是否已经链接
        if (!isConnect()) {
            return
        }
        socket?.disconnect()
    }

    /**
     * 是否成功建立链接
     */
    fun isConnect(): Boolean {
        return isConnect
    }

    /**
     * 链接状态回调
     */
    fun onConnect(connect: Boolean) {
        isConnect = connect

        // 通知其他地方链接断开
        LDEventBus.post(IMConstants.Common.connectStatusEvent, isConnect)
    }

    /**
     * 发送消息
     */
    fun sendMsg(message: IMMessage, callback: (Int, Any) -> Unit = { _: Int, _: Any -> }) {
        socket?.emit(IMConstants.Common.wsMessageEvent, message.toString(), object : AckTimer() {
            override fun callback(code: Int, obj: Any) {
                if (code == 0) {
                    cancelTimer()
                }
                callback.invoke(code, obj)
            }
        })
    }

    /**
     * 发送信令
     */
    fun sendSignal(signal: IMSignal, callback: (Int, Any) -> Unit = { _: Int, _: Any -> }) {
        socket?.emit(IMConstants.Common.wsSignalEvent, signal.toString(), object : AckTimer() {
            override fun callback(code: Int, obj: Any) {
                if (code == 0) {
                    cancelTimer()
                }
                callback.invoke(code, obj)
            }
        })
    }
}