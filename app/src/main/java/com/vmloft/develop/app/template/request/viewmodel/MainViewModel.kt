package com.vmloft.develop.app.template.request.viewmodel

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.repository.CommonRepository
import com.vmloft.develop.app.template.request.repository.InfoRepository
import com.vmloft.develop.library.base.BViewModel
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
                SignManager.setCurrUser(result.data as User)

                emitUIState(data = result.data, type = "userInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
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
    fun clientConfig() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                commonRepo.clientConfig()
            }
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "clientConfig")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }
}