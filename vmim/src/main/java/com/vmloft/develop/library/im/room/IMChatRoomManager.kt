package com.vmloft.develop.library.im.room

import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMChatRoom
import com.hyphenate.chat.EMClient

import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.repository.RoomRepository
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.request.RResult

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
        val room = CacheManager.getLastRoom() ?: return
        val user = SignManager.getCurrUser()

        // 检查下是不是自己创建的房间
        if (room.owner.id == user.id) {
            // 这里使用协程调用销毁聊天室
            val scope = CoroutineScope(Job() + Dispatchers.Main)
            scope.launch {
                val result = RoomRepository().destroyRoom(roomId)
                if (result is RResult.Success) {
                    // 1.销毁的话把缓存里的房间信息也要删掉
                    CacheManager.setLastRoom(null)
                }
            }
        } else {
            // 2.只是退出，置空下最后加入的房间就好
            CacheManager.setLastRoom(null)
            // 离开房间
            EMClient.getInstance().chatroomManager().leaveChatRoom(roomId)
        }

        // 聊天室会话不保存，退出就删除
        IMChatManager.deleteConversation(roomId)
    }


//    /**
//     * 聊天室被销毁
//     */
//    fun roomDestroyed(roomId: String) {
//        // 聊天室会话不保存，退出就删除
//        IMChatManager.deleteConversation(roomId)
//        // 回调退出房间接口，通知外层销毁或者清除缓存数据
//        IM.imListener.exitRoom(roomId)
//    }

}