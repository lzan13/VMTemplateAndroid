package com.vmloft.develop.app.template.request.viewmodel

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.request.repository.LikeRepository

import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.app.template.request.repository.PostRepository
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.tools.utils.VMStr

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：注册登录 ViewModel
 */
class ExploreViewModel(private val postRepo: PostRepository, private val likeRepo: LikeRepository) : BViewModel() {

    /**
     * 加载内容
     */
    fun getPostList(page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit, isService: Boolean = true) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = postRepo.postList(page, limit, isService)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "postList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 屏蔽内容
     */
    fun shieldPost(post: Post) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = postRepo.shieldPost(post)

            if (result is RResult.Success) {
                emitUIState(data = result.data, toast = VMStr.byRes(R.string.feedback_hint), type = "shieldPost")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
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
            val result = likeRepo.like(1, id)

            if (result is RResult.Success) {
                emitUIState(type = "like")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 取消喜欢
     */
    fun cancelLike(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = likeRepo.cancelLike(1, id)

            if (result is RResult.Success) {
                emitUIState(type = "cancelLike")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

}