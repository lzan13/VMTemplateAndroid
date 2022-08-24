package com.vmloft.develop.library.data.viewmodel

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.library.data.repository.CommonRepository
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：注册登录 ViewModel
 */
class FeedbackViewModel(private val repo: CommonRepository) : BViewModel() {

    /**
     * 提交反馈信息
     * @param contact 联系方式
     * @param content 反馈内容
     * @param user 相关用户
     * @param post 相关帖子
     * @param comment 相关评论
     * @param attachments 附件
     * @param type 反馈类型 0-意见建议 1-广告引流 2-政治敏感 3-违法违规 4-色情低俗 5-血腥暴力 6-诱导信息 7-谩骂攻击 8-涉嫌诈骗 9-引人不适 10-其他
     */
    fun feedback(contact: String, content: String, type: Int = 0, attachments: List<String> = arrayListOf(), user: String = "", post: String = "", comment: String = "") {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.feedback(contact, content, type, attachments, user, post, comment)
            if (result is RResult.Success) {
                emitUIState(type = "feedback")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取我提交的反馈信息
     */
    fun feedbackList(page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimitBig) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.feedbackList(page, limit)
            if (result is RResult.Success) {
                emitUIState(type = "feedbackList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

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
                .addFormDataPart("space", "feedback")
                .addFormDataPart("desc", "反馈与建议")
                .addFormDataPart("width", sizes[0].toString())
                .addFormDataPart("height", sizes[1].toString())
                .addFormDataPart("file", file.name, body)

            // 调用上传图片
            var result = repo.upload(builder.build())

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "uploadPicture")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

}