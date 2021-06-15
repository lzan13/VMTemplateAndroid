package com.vmloft.develop.app.template.ui.main.mine.info

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.common.CacheManager
import com.vmloft.develop.app.template.common.SignManager

import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.app.template.request.repository.CommonRepository
import com.vmloft.develop.app.template.request.repository.InfoRepository
import com.vmloft.develop.library.common.utils.JsonUtils
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
 * 描述：编辑信息 ViewModel
 */
class InfoViewModel(private val repository: InfoRepository, private val commonRepository: CommonRepository) : BViewModel() {

    /**
     * ------------------------ 公共信息相关接口 ----------------------------
     */
    /**
     * 获取职业集合
     */
    fun loadProfessionList() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                commonRepository.getProfessionList()
            }
            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "professionList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * ------------------------ 用户信息相关接口 ----------------------------
     */
    /**
     * 修改用户名
     */
    fun updateUsername(username: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.updateUsername(username)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "updateUsername")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 修改用户信息
     */
    fun updateInfo(params: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val body: RequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), JsonUtils.map2json(params))
            val result = repository.updateInfo(body)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "updateInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 修改头像
     */
    fun updateAvatar(avatar: Any) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            // 组装上传头像
            // 临时压缩下图片，这里压缩到默认的分辨率
            val tempPath = VMBitmap.compressTempImage(avatar, 512, 80)
            val file = File(tempPath)
            val body: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("avatar", file.name, body)
            val result = repository.updateAvatar(part)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "updateAvatar")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 修改封面
     */
    fun updateCover(cover: Any) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            // 组装上传头像
            // 临时压缩下图片，这里压缩到默认的分辨率
            val tempPath = VMBitmap.compressTempImage(cover, 1080, 100)
            val file = File(tempPath)
            val body: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("cover", file.name, body)
            val result = repository.updateCover(part)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "updateCover")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 更新密码
     */
    fun updatePassword(password: String, oldPassword: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.updatePassword(password, oldPassword)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "updatePassword")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 个人认证
     */
    fun personalAuth(realName: String, idCardNumber: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.personalAuth(realName, idCardNumber)

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "personalAuth")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(id: String = "") {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = if (id.isEmpty()) {
                repository.current()
            } else {
                repository.other(id)
            }

            if (result is RResult.Success && result.data != null) {
                // 将用户信息加入到缓存
                CacheManager.instance.putUser(result.data!!)
                emitUIState(isSuccess = true, data = result.data, type = "userInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 签到
     */
    fun clock() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.clock()
            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "clock")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }
}