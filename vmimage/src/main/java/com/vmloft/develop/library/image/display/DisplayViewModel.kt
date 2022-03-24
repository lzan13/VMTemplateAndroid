package com.vmloft.develop.library.image.display

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.vmloft.develop.library.base.BViewModel

import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.request.RResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Create by lzan13 on 2020/4/20 17:28
 */
class DisplayViewModel : BViewModel() {

    /**
     * 保存单张图片
     */
    fun savePictureSingle(context: Context, url: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                IMGLoader.savePicture(context, url)
            }
            if (result is RResult.Success) {
                emitUIState(data = result.data, toast = result.msg)
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 保存多张图片
     */
    fun savePictureMulti(context: Context, urlList: List<String>, pathList: List<String>) {
//        viewModelScope.launch(Dispatchers.Main) {
//            emitUIState()
//            var successCount = 0
//            var errorCount = 0
//            val count = urlList.size - 1
//            for (i in 0..count) {
//                val url = urlList[i]
//                val path = pathList[i]
//                val result = IMGLoader.savePicture(context, url, path)
//                if (result is APIResult.Success) {
//                    successCount++
//                } else {
//                    errorCount++
//                }
//            }
//
//            if (successCount > 0) {
//                emitUIState(data = "保存图片完成 成功[$successCount] 失败[$errorCount]")
//                return@launch
//            } else {
//                emitUIState(data = "保存图片失败")
//            }
//        }
    }
}