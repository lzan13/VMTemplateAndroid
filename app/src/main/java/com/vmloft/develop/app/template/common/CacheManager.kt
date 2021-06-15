package com.vmloft.develop.app.template.common

import com.vmloft.develop.app.template.request.bean.Room
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.utils.JsonUtils


/**
 * Create by lzan13 on 2021/5/19 14:08
 * 描述：缓存管理
 */
class CacheManager {

    // 缓存用户信息
    private val userMap = mutableMapOf<String, User>()

    // 缓存房间信息
    private val roomMap = mutableMapOf<String, Room>()

    private var lastRoom: Room? = null


    companion object {
        val instance: CacheManager by lazy {
            CacheManager()
        }
    }

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

    fun getUser(id: String): User? {
        return userMap[id]
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

    fun getRoom(id: String): Room? {
        return roomMap[id]
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
        val json: String = JsonUtils.toJson(room, Room::class.java)
        SPManager.instance.putLastRoom(json)
    }

    fun getLastRoom(): Room? {
        if (lastRoom == null) {
            val json: String = SPManager.instance.getLastRoom()
            lastRoom = JsonUtils.formJson(json, Room::class.java)
        }
        return lastRoom
    }


}