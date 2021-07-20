package com.vmloft.develop.library.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.*

/**
 * Created by lzan13 on 2020/02/15 11:16
 * 描述：ViewModel 基类
 */
open class BViewModel : ViewModel() {
    // 是否第一次请求，主要是展示全屏加载进度提示，防止空等
    protected var isFirst = true

    private val _uiState = MutableLiveData<UIModel>()
    val uiState: LiveData<UIModel> get() = _uiState


    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }

    suspend fun <T> launchOnIO(block: suspend CoroutineScope.() -> T) {
        withContext(Dispatchers.IO) {
            block
        }
    }

    /**
     * 重置 ViewModel
     */
    fun resetVM() {
        isFirst = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    protected fun emitUIState(
        isLoading: Boolean = false,
        isSuccess: Boolean = true,
        data: Any? = null,
        code: Int = 0,
        error: String? = null,
        toast: String? = null,
        type: String = "default"
    ) {
        val model = UIModel(isLoading, isSuccess, data, code, error, toast, type)
        _uiState.value = model
    }

    data class UIModel(
        val isLoading: Boolean,
        val isSuccess: Boolean,
        val data: Any?,
        val code: Int,
        val error: String?,
        val toast: String?,
        val type: String
    )
}