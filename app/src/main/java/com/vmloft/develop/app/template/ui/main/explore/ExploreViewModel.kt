package com.vmloft.develop.app.template.ui.main.explore

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.request.repository.LikeRepository

import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.app.template.request.repository.PostRepository
import com.vmloft.develop.library.common.common.CConstants

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：注册登录 ViewModel
 */
class ExploreViewModel(
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
) : BViewModel() {

    /**
     * 加载内容
     */
    fun getPostList(page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = postRepository.getPostList(page, limit)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "postList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * -------------------------------------------------------
     */
    /**
     * 喜欢
     */
    fun like(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = likeRepository.like(1, id)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, type = "like")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 取消喜欢
     */
    fun cancelLike(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = likeRepository.cancelLike(1, id)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, type = "cancelLike")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

}