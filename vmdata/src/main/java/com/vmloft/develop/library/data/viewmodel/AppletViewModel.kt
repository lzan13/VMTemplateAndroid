package com.vmloft.develop.library.data.viewmodel

import androidx.lifecycle.viewModelScope

import com.vmloft.develop.library.data.bean.Applet
import com.vmloft.develop.library.data.repository.AppletRepository
import com.vmloft.develop.library.data.repository.CommonRepository
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.tools.utils.VMFile
import com.vmloft.develop.library.tools.utils.logger.VMLog

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.File
import java.io.FileOutputStream

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：程序相关 ViewModel
 */
class AppletViewModel(private val repo: AppletRepository, private val commonRepo: CommonRepository) : BViewModel() {

    /**
     * 加载内容
     */
    fun appletList(page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.appletList(page, limit)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "appletList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 下载文件
     */
    fun download(applet: Applet) {
        val filePath = VMFile.filesPath("applet") + applet.body.id + applet.body.extname
        val file = File(filePath)
        if (!VMFile.isDirExists(file.parent)) {
            VMFile.createDirectory(file.parent)
        }
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            if (VMFile.isFileExists(filePath)) {
                VMLog.i("-download- 文件已存在，无需下载")
                emitUIState(data = applet, type = "download")
                return@launch
            }
            VMLog.i("-download- 文件下载开始")
            withContext(Dispatchers.IO) {
                val body = commonRepo.download(applet.body)
                val totalLen = body.contentLength()
                val inputStream = body.byteStream()
                val outputStream = FileOutputStream(filePath)

                var currLen = 0
                val buff = ByteArray(1024 * 8)
                var readLen = 0
                while (inputStream.read(buff).also { readLen = it } != -1) {
                    currLen += readLen
                    outputStream.write(buff, 0, readLen)
                }
                VMLog.i("-download- 文件下载进度 $currLen/$totalLen")
                inputStream.close()
                outputStream.close()
            }
            VMLog.i("-download- 文件下载完成")
            emitUIState(data = applet, type = "download")

//            if (result is RResult.Success) {
//                emitUIState(data = result.data, type = "download")
//                return@launch
//            } else if (result is RResult.Error) {
//                emitUIState(isSuccess = false, code = result.code, error = result.error)
//            }
        }
    }
}