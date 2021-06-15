package com.vmloft.develop.app.template.ui.post

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.request.repository.CommonRepository
import com.vmloft.develop.app.template.request.repository.LikeRepository

import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.app.template.request.repository.PostRepository
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：内容 ViewModel
 */
class PostViewModel(
    val repository: PostRepository,
    val commonRepository: CommonRepository,
    val likeRepository: LikeRepository,
) : BViewModel() {

    /**
     * -------------------------------------------------------
     * 加载分类
     */
    fun getCategoryList() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                commonRepository.getCategoryList()
            }

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "categoryList")
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
     * 上传图片
     */
    fun uploadPicture(picture: Any) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            // 临时压缩下图片，这里压缩到默认的分辨率
            val tempPath = VMBitmap.compressTempImage(picture, 2048, 256)
            val file = File(tempPath)
            val body: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("picture", file.name, body)
            val result = commonRepository.upload(part)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "uploadPicture")
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
     * 创建
     */
    fun createPost(title: String, content: String, category: String, attachments: List<String> = arrayListOf()) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.createPost(title, content, category, attachments)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, type = "createPost")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 加载内容列表
     */
    fun getPostList(owner: String, page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.getPostList(page, limit, owner)

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
     * 评论
     */
    fun createComment(content: String, post: String, user: String = "") {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.createComment(content, post, user)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, type = "createComment")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 加载内容评论列表
     */
    fun getCommentList(post: String, page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.getCommentList(post, page, limit)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "commentList")
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

    /**
     * 获取指定用户喜欢的帖子
     */
    fun getLikePostList(owner: String, page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = likeRepository.getLikePostList(owner, page, limit, 1, "")

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "likeList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

}