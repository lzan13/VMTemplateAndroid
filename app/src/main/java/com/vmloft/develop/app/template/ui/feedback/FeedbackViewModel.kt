package com.vmloft.develop.app.template.ui.feedback

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.request.repository.CommonRepository
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.request.FileRepository
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.tools.utils.VMFile
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：注册登录 ViewModel
 */
class FeedbackViewModel(
    private val repo: CommonRepository,
    private val fileRepo: FileRepository,
) : BViewModel() {

    /**
     * 提交反馈信息
     */
    fun feedback(contact: String, content: String, attachment: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.feedback(contact, content, attachment)
            if (result is RResult.Success) {
                emitUIState(type = "feedback")
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
            // 临时压缩下图片，这里压缩到默认的分辨率
            val tempPath = VMBitmap.compressTempImage(picture, 2048, 256)
            val file = File(tempPath)

            // 调用上传图片
            var uploadResult = fileRepo.uploadPicture(file)
            // 构建参数
            if (uploadResult is RResult.Error) {
                emitUIState(code = uploadResult.code, error = uploadResult.error)
                return@launch
            }
            // 构建参数
            val params: MutableMap<String, Any> = mutableMapOf()
            if (uploadResult is RResult.Success) {
                params["extname"] = VMFile.parseSuffix(file.path)
                params["filename"] = VMFile.parseFilename(uploadResult.data!!)
                params["path"] = uploadResult.data!!
                params["extra"] = "feedback"
            }
            // 回调上传结果
            val result = repo.ucloudCallbackObj(JsonUtils.map2json(params).toRequestBody("application/json".toMediaType()))

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "uploadPicture")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

}