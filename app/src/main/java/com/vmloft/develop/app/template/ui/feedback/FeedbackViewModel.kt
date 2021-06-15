package com.vmloft.develop.app.template.ui.feedback

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.request.repository.CommonRepository
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：注册登录 ViewModel
 */
class FeedbackViewModel(
    val repository: CommonRepository,
) : BViewModel() {

    /**
     * 提交反馈信息
     */
    fun feedback(contact: String, content: String, attachment: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.feedback(contact, content, attachment)
            if (result is RResult.Success) {
                emitUIState(isSuccess = true, type = "feedback")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

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
            val result = repository.upload(part)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "uploadPicture")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

}