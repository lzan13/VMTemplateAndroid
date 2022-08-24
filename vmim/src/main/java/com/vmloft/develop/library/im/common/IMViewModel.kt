package com.vmloft.develop.library.im.common

import androidx.lifecycle.viewModelScope

import com.vmloft.develop.library.data.repository.CommonRepository
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.im.bean.IMConversation
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.request.RResult

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Create by lzan13 on 2022/8/18 22:28
 * 描述：即时通讯相关 ViewModel
 */
class IMViewModel() : BViewModel() {


    /**
     * 加载更多消息
     */
    fun loadMoreMessage(conversation: IMConversation) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val list = withContext(Dispatchers.IO) { IMConversationManager.loadMoreMessage(conversation) }

            val result = RResult.Success(data = list)
            emitUIState(data = result.data, type = "loadMoreMessage")
        }
    }
}