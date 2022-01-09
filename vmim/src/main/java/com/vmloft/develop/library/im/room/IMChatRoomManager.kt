package com.vmloft.develop.library.im.room

import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMChatRoom
import com.hyphenate.chat.EMClient
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.chat.IMChatManager

/**
 * Create by lzan13 2021/05/26
 * 描述：聊天室管理
 */
object IMChatRoomManager {

    /**
     * 加入聊天室
     */
    fun joinRoom(roomId: String, callback: (code: Int) -> Unit) {
        EMClient.getInstance().chatroomManager().joinChatRoom(roomId, object : EMValueCallBack<EMChatRoom> {
            override fun onSuccess(value: EMChatRoom?) {
                callback.invoke(0)
            }

            override fun onError(code: Int, msg: String) {
                callback.invoke(code)
            }
        })
    }

    /**
     * 退出聊天室
     */
    fun exitRoom(roomId: String) {
        EMClient.getInstance().chatroomManager().leaveChatRoom(roomId)

        // 聊天室会话不保存，退出就删除
        IMChatManager.deleteConversation(roomId)
        // 回调退出房间接口，通知外层销毁或者清除缓存数据
        IM.imListener.exitRoom(roomId)
    }


    /**
     * 聊天室被销毁
     */
    fun roomDestroyed(roomId: String) {
        // 聊天室会话不保存，退出就删除
        IMChatManager.deleteConversation(roomId)
        // 回调退出房间接口，通知外层销毁或者清除缓存数据
        IM.imListener.exitRoom(roomId)
    }
}