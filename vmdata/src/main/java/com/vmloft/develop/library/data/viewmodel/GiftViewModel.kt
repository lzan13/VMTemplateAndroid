package com.vmloft.develop.library.data.viewmodel

import androidx.lifecycle.viewModelScope

import com.vmloft.develop.library.data.bean.Gift
import com.vmloft.develop.library.data.repository.CommonRepository
import com.vmloft.develop.library.data.repository.GiftRepository
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.tools.utils.VMFile
import com.vmloft.develop.library.tools.utils.logger.VMLog

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.File
import java.io.FileOutputStream

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：礼物 ViewModel
 */
class GiftViewModel(private val repo: GiftRepository, private val commRepo: CommonRepository) : BViewModel() {

    /**
     * 赠送礼物
     */
    fun giftGive(userId: String, giftId: String, count: Int = 1) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true, type = "giftGive")
            val result = repo.giftGive(userId, giftId, count)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "giftGive")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取礼物列表
     */
    fun giftList() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                repo.giftList()
            }
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "giftList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, type = "giftList", code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取收到礼物列表
     */
    fun giftRelationList(userId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.giftRelationList(userId)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "giftRelationList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 下载文件
     */
    fun download(gift: Gift) {
        val filePath = VMFile.filesPath("gift") + gift.animation.id + gift.animation.extname
        val file = File(filePath)
        if (!VMFile.isDirExists(file.parent)) {
            VMFile.createDirectory(file.parent)
        }
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            if (VMFile.isFileExists(filePath)) {
                VMLog.i("-download- 文件已存在，无需下载")
                emitUIState(data = gift, type = "download")
                return@launch
            }
            VMLog.i("-download- 文件下载开始")
            withContext(Dispatchers.IO) {
                val body = commRepo.download(gift.animation)
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
            emitUIState(data = gift, type = "download")

//            if (result is RResult.Success) {
//                emitUIState(data = result.data, type = "download")
//                return@launch
//            } else if (result is RResult.Error) {
//                emitUIState(isSuccess = false, code = result.code, error = result.error)
//            }
        }
    }

}