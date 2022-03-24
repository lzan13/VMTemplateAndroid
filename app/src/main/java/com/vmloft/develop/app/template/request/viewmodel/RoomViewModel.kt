package com.vmloft.develop.app.template.request.viewmodel

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.request.repository.RoomRepository
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants

import com.vmloft.develop.library.request.RResult

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Create by lzan13 on 2021/5/20 17:28
 * 描述：房间接口
 */
class RoomViewModel(val repo: RoomRepository) : BViewModel() {

    /**
     * 创建
     */
    fun createRoom(title: String, desc: String, owner: String = "") {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.createRoom(title, desc, owner)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "createRoom")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 销毁
     */
    fun destroyRoom(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.destroyRoom(id)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "destroyRoom")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 更新
     */
    fun updateRoom(id: String, title: String, desc: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.updateRoom(id, title, desc)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "updateRoom")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取房间列表
     */
    fun roomList(page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.roomList(page, limit)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "roomList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }
    /**
     * 获取随机房间
     */
    fun randomRoom(type: Int = 0) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.randomRoom(type)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "randomRoom")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取房间信息
     */
    fun roomInfo(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.roomInfo(id)
            if (result is RResult.Success) {
                emitUIState(data = result.data, toast = result.msg, type = "roomInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }
}