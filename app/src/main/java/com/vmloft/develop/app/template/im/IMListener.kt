package com.vmloft.develop.app.template.im

import com.hyphenate.chat.EMMessage

import com.vmloft.develop.app.template.common.CacheManager
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.repository.InfoRepository
import com.vmloft.develop.app.template.request.repository.RoomRepository
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.library.im.IIMListener
import com.vmloft.develop.library.im.bean.IMRoom
import com.vmloft.develop.library.im.bean.IMUser

import kotlinx.coroutines.*

/**
 * Create by lzan13 on 2019/5/23 09:57
 *
 * 描述：实现 IM 全局回调接口
 */
class IMListener : IIMListener {
    /**
     * 同步获取联系人信息
     *
     * @param id 联系人 id
     */
    override fun getUser(id: String): IMUser? {
        var user = SignManager.getCurrUser()
        user?.let {
            if (it.id == id) {
                return IMUser(id, it.username, it.nickname, it.avatar, it.gender)
            }
        }
        user = CacheManager.getUser(id)
        user?.let {
            return IMUser(id, it.username, it.nickname, it.avatar, it.gender)
        }
        return null
    }

    /**
     * 同步获取联系人信息
     *
     * @param id 联系人 id
     */
    override fun getUser(id: String, callback: (IMUser) -> Unit) {
        var user = CacheManager.getUser(id)
        user?.let {
            return callback.invoke(IMUser(id, it.username, it.nickname, it.avatar, it.gender))
        }
        // 这里使用协程获取用户信息
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val result = InfoRepository().other(id)
            if (result is RResult.Success && result.data != null) {
                val user = result.data!!
                // 将用户信息加入到缓存
                CacheManager.putUser(user)
                callback.invoke(IMUser(id, user.username, user.nickname, user.avatar, user.gender))
            } else if (result is RResult.Error) {
                callback.invoke(IMUser(id))
            }
        }
    }

    /**
     * 获取指定 Id 集合的用户信息
     */
    override fun getUserList(ids: List<String>, callback: () -> Unit) {
        // 这里使用协程获取用户信息
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val result = InfoRepository().getUserList(ids)
            if (result is RResult.Success && result.data != null) {
                CacheManager.resetUsers(result.data!!)
            }
            callback.invoke()
        }
    }

    /**
     * 获取房间信息
     */
    override fun getRoom(id: String): IMRoom {
        val room = CacheManager.getRoom(id)
        room?.let {
            val owner = IMUser(room.owner.id, room.owner.username, room.owner.nickname, room.owner.avatar, room.owner.gender)
            return IMRoom(id, owner, room.title, room.desc, room.count)
        }
        return IMRoom(id, IMUser(""))
    }

    /**
     * 获取房间信息，这里异步获取
     */
//    override fun getRoom(id: String, callback: (IMRoom?) -> Unit) {
//        // 这里使用协程获取用户信息
//        val scope = CoroutineScope(Job() + Dispatchers.Main)
//        scope.launch {
//            val result = RoomRepository().getRoomInfo(id)
//            if (result is RResult.Success && result.data != null) {
//                val room = result.data!!
//                // 将用户信息加入到缓存
//                CacheManager.putRoom(room)
//                // 包装回调
//                val owner = IMUser(room.owner.id, room.owner.username, room.owner.nickname, room.owner.avatar, room.owner.gender)
//                callback.invoke(IMRoom(id, owner, room.title, room.desc, room.count))
//            } else if (result is RResult.Error) {
//                callback.invoke(null)
//            }
//        }
//    }

    /**
     * 离开房间
     */
    override fun exitRoom(id: String) {
        val room = CacheManager.getLastRoom() ?: return
        val user = SignManager.getCurrUser() ?: return
        // 检查下是不是自己创建的房间
        if (room.owner.id == user.id) {
            // 这里使用协程调用销毁聊天室
            val scope = CoroutineScope(Job() + Dispatchers.Main)
            scope.launch {
                val result = RoomRepository().destroyRoom(id)
                if (result is RResult.Success) {
                    // 1.销毁的话把缓存里的房间信息也要删掉
                    CacheManager.setLastRoom(null)
                }
            }
        } else {
            // 2.只是退出，置空下最后加入的房间就好
            CacheManager.setLastRoom(null)
        }
    }

    /**
     * 联系人头像点击
     *
     * @param id 用户 id
     */
    override fun onHeadClick(id: String) {
        val user = CacheManager.getUser(id) ?: User(id)
        AppRouter.goUserInfo(user)
    }

    override fun getMsgType(msg: EMMessage): Int {
        return 0
    }

    override fun getMsgSummary(msg: EMMessage): String {
        return ""
    }
}