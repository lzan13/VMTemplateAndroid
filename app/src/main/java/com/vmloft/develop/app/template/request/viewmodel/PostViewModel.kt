package com.vmloft.develop.app.template.request.viewmodel

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.request.repository.CommonRepository
import com.vmloft.develop.app.template.request.repository.LikeRepository

import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.app.template.request.repository.PostRepository
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：内容 ViewModel
 */
class PostViewModel(
    private val repo: PostRepository,
    private val commonRepo: CommonRepository,
    private val likeRepo: LikeRepository,
) : BViewModel() {

    /**
     * -------------------------------------------------------
     * 加载分类
     */
    fun categoryList() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                commonRepo.categoryList()
            }

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "categoryList")
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
     * 上传图片
     */
    fun uploadPicture(picture: Any) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            // 临时压缩下图片
            val tempPath = VMBitmap.compressTempImage(picture, 2048, 256)
            val file = File(tempPath)
            val sizes = VMBitmap.getImageSize(tempPath)
            val body: RequestBody = file.asRequestBody("image/*".toMediaType())
//            val part: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, body)
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("desc", "帖子携带附件")
                .addFormDataPart("width", sizes[0].toString())
                .addFormDataPart("height", sizes[1].toString())
                .addFormDataPart("file", file.name, body)
            // 调用上传图片
            var result = commonRepo.upload(builder.build())

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "uploadPicture")
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
     * 创建
     */
    fun createPost(content: String, category: String, attachments: List<String> = arrayListOf()) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.createPost(content, category, attachments)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "createPost")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 创建
     */
    fun deletePost(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.deletePost(id)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "deletePost")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 创建
     */
    fun postInfo(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.postInfo(id)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "postInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 帖子列表
     */
    fun postList(owner: String, page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit, isService: Boolean = true) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.postList(page, limit, isService, owner)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "postList")
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
     * 评论
     */
    fun createComment(content: String, post: String, user: String = "") {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.createComment(content, post, user)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "createComment")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 加载内容评论列表
     */
    fun commentList(post: String, page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.getCommentList(post, page, limit)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "commentList")
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
            val result = repo.shieldPost(post)

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

    /**
     * 获取指定用户喜欢的帖子
     */
    fun getLikePostList(owner: String, page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = likeRepo.getLikePostList(owner, page, limit, 1, "")

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "likeList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

}