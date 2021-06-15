package com.vmloft.develop.app.template.request.repository

import com.vmloft.develop.app.template.request.api.APIRequest
import com.vmloft.develop.library.common.request.BaseRepository
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.request.bean.Room

/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：房间相关请求
 */
class RoomRepository : BaseRepository() {

    /**
     * 创建
     */
    suspend fun createRoom(title: String, desc: String, owner: String = ""): RResult<Room> {
        return safeRequest(call = { requestCreateRoom(title, desc, owner) })
    }

    private suspend fun requestCreateRoom(title: String, desc: String, owner: String): RResult<Room> =
        executeResponse(APIRequest.roomAPI.createRoom(title, desc, owner))

    /**
     * 销毁
     */
    suspend fun destroyRoom(roomId: String): RResult<Any> {
        return safeRequest(call = { requestDestroyRoom(roomId) })
    }

    private suspend fun requestDestroyRoom(roomId: String): RResult<Any> =
        executeResponse(APIRequest.roomAPI.destroyRoom(roomId))

    /**
     * 修改
     */
    suspend fun updateRoom(id: String, title: String, desc: String): RResult<Room> {
        return safeRequest(call = { requestUpdateRoom(id, title, desc) })
    }

    private suspend fun requestUpdateRoom(id: String, title: String, desc: String): RResult<Room> =
        executeResponse(APIRequest.roomAPI.updateRoom(id, title, desc))

    /**
     * 获取房间列表
     */
    suspend fun getRoomList(page: Int, limit: Int, owner: String = ""): RResult<RPaging<Room>> {
        return safeRequest(call = { requestRoomList(page, limit, owner) })
    }

    private suspend fun requestRoomList(page: Int, limit: Int, owner: String): RResult<RPaging<Room>> =
        executeResponse(APIRequest.roomAPI.getRoomList(page, limit, owner))

    /**
     * 获取房间列表
     */
    suspend fun getRoomInfo(id: String): RResult<Room> {
        return safeRequest(call = { requestRoomInfo(id) })
    }

    private suspend fun requestRoomInfo(id: String): RResult<Room> =
        executeResponse(APIRequest.roomAPI.getRoomInfo(id))

}