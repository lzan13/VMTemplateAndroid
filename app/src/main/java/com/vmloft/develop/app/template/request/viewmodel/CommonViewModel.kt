package com.vmloft.develop.app.template.request.viewmodel

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.request.repository.CommonRepository
import com.vmloft.develop.library.base.BViewModel

import com.vmloft.develop.library.request.RResult

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：公共 ViewModel
 */
class CommonViewModel(private val repo: CommonRepository) : BViewModel() {


    /**
     * 检查版本
     */
    fun checkVersion(server: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                repo.checkVersion()
            }
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "checkVersion")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

}