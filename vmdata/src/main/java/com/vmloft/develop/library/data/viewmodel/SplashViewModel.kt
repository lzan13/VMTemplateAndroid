package com.vmloft.develop.library.data.viewmodel

import androidx.lifecycle.viewModelScope

import com.vmloft.develop.library.data.repository.CommonRepository
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.request.RResult

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：首页 ViewModel
 */
class SplashViewModel(private val commonRepo: CommonRepository) : BViewModel() {

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