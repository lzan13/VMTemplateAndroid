package com.vmloft.develop.library.data.common

import com.vmloft.develop.library.data.bean.Room
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.repository.InfoRepository
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.request.RResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * Create by lzan13 on 2021/5/19 14:08
 * 描述：缓存管理
 */
object CacheManager {

    // 缓存用户信息
    private val userMap = mutableMapOf<String, User>()

    // 缓存房间信息
    private val roomMap = mutableMapOf<String, Room>()

    private var lastRoom: Room? = null

    /**
     * ----------------------------------------------------------------------
     * 缓存用户信息
     */
    fun resetUsers(users: List<User>) {
        userMap.clear()
        users.forEach {
            userMap[it.id] = it
        }
    }

    fun putUsers(users: List<User>) {
        users.forEach {
            userMap[it.id] = it
        }
    }

    fun putUser(user: User) {
        userMap[user.id] = user
    }

    fun getUser(id: String): User {
        return userMap[id] ?: User()
    }

    fun getUser(id: String, callback: (User) -> Unit = {}) {
        // 这里使用协程获取用户信息
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            val result = InfoRepository().other(id)
            if (result is RResult.Success && result.data != null) {
                val user = result.data!!
                // 将用户信息加入到缓存
                putUser(user)
                callback.invoke(user)
            } else if (result is RResult.Error) {
                callback.invoke(User(id))
            }
        }
    }

    /**
     * ----------------------------------------------------------------------
     * 缓存房间信息
     */
    fun resetRoom(rooms: List<Room>) {
        roomMap.clear()
        rooms.forEach {
            roomMap[it.id] = it
        }
    }

    fun putRooms(rooms: List<Room>) {
        rooms.forEach {
            putRoom(it)
        }
    }

    fun putRoom(room: Room) {
        roomMap[room.id] = room
        // 把房主页缓存起来
        putUser(room.owner)
    }

    fun getRoom(id: String): Room {
        return roomMap[id] ?: Room(id)
    }


    /**
     * ----------------------------------------------------------------------
     * 设置最后进入的房间记录
     * @param room 最后一次加入的房间
     * @param remove 是否移除缓存信息
     */
    fun setLastRoom(room: Room? = null, remove: Boolean = false) {
        if (remove) {
            userMap.remove(lastRoom?.owner?.id)
            roomMap.remove(lastRoom?.id)
        }
        lastRoom = room
        val json: String = JsonUtils.toJson(room)
        DSPManager.putLastRoom(json)
    }

    fun getLastRoom(): Room? {
        if (lastRoom == null) {
            val json: String = DSPManager.getLastRoom()
            lastRoom = JsonUtils.fromJson(json, Room::class.java)
        }
        return lastRoom
    }
}