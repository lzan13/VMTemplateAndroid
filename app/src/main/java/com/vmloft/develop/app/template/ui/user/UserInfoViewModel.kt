package com.vmloft.develop.app.template.ui.user

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.common.CacheManager
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.request.repository.FollowRepository
import com.vmloft.develop.app.template.request.repository.InfoRepository

import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.app.template.request.repository.PostRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：内容 ViewModel
 */
class UserInfoViewModel(val repository: FollowRepository, val infoRepository: InfoRepository) : BViewModel() {

    /**
     * 关注用户
     */
    fun follow(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.follow(id)
            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "follow")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 取消关注用户
     */
    fun cancelFollow(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.cancelFollow(id)
            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "cancelFollow")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }


    /**
     * 获取用户信息
     */
    fun getUserInfo(id: String = "") {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = if (id.isEmpty()) {
                infoRepository.current()
            } else {
                infoRepository.other(id)
            }

            if (result is RResult.Success && result.data != null) {
                CacheManager.putUser(result.data!!)
                emitUIState(isSuccess = true, data = result.data, type = "userInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

}