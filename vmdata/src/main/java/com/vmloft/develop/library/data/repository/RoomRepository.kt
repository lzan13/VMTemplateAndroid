package com.vmloft.develop.library.data.repository

import com.vmloft.develop.library.data.api.APIRequest
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.data.bean.Room

/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：房间相关请求
 */
class RoomRepository : BaseRepository() {

    /**
     * 创建
     */
    suspend fun createRoom(title: String, desc: String, owner: String = ""): RResult<Room> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.roomAPI.createRoom(title, desc, owner)) }
    }

    /**
     * 销毁
     */
    suspend fun destroyRoom(roomId: String): RResult<Any> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.roomAPI.destroyRoom(roomId)) }
    }


    /**
     * 修改
     */
    suspend fun updateRoom(id: String, title: String, desc: String): RResult<Room> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.roomAPI.updateRoom(id, title, desc)) }
    }


    /**
     * 获取房间列表
     */
    suspend fun roomList(page: Int, limit: Int, type: Int = 0): RResult<RPaging<Room>> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.roomAPI.roomList(page, limit, type)) }
    }

    /**
     * 获取随机房间
     */
    suspend fun randomRoom(type: Int): RResult<Room> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.roomAPI.randomRoom(type)) }
    }


    /**
     * 获取房间信息
     */
    suspend fun roomInfo(id: String): RResult<Room> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.roomAPI.roomInfo(id)) }
    }

}