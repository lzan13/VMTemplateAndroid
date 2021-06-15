package com.vmloft.develop.app.template.ui.room

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.request.repository.RoomRepository

import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.request.RResult

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Create by lzan13 on 2021/5/20 17:28
 * 描述：房间接口
 */
class RoomViewModel(val repository: RoomRepository) : BViewModel() {

    /**
     * 创建
     */
    fun createRoom(title: String, desc: String, owner: String = "") {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.createRoom(title, desc, owner)
            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "createRoom")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 销毁
     */
    fun destroyRoom(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.destroyRoom(id)
            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "destroyRoom")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 更新
     */
    fun updateRoom(id: String, title: String, desc: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.updateRoom(id, title, desc)
            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "updateRoom")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取聊天室列表
     */
    fun getRoomList(page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.getRoomList(page, limit)
            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "roomList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取房间信息
     */
    fun getRoomInfo(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.getRoomInfo(id)
            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, toast = result.msg, type = "roomInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }
}