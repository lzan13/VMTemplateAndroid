package com.vmloft.develop.library.data.viewmodel

import androidx.lifecycle.viewModelScope

import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.repository.CommonRepository
import com.vmloft.develop.library.data.repository.InfoRepository
import com.vmloft.develop.library.request.RResult

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：首页 ViewModel
 */
class MainViewModel(private val repo: InfoRepository, private val commonRepo: CommonRepository) : BViewModel() {

    /**
     * 加载当前用户数据
     */
    fun userInfo() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.current()

            if (result is RResult.Success) {
                SignManager.setSignUser(result.data as User)

                emitUIState(data = result.data, type = "userInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error, type = "userInfo")
            }
        }
    }

    /**
     * 检查版本
     */
    fun checkVersion() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                commonRepo.checkVersion()
            }
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "checkVersion")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 检查版本
     */
    fun appConfig() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                commonRepo.appConfig()
            }
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "appConfig")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }
}