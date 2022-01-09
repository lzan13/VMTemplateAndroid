package com.vmloft.develop.app.template.request.viewmodel

import androidx.lifecycle.viewModelScope

import com.vmloft.develop.app.template.common.CacheManager
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.app.template.request.repository.CommonRepository
import com.vmloft.develop.app.template.request.repository.FollowRepository
import com.vmloft.develop.app.template.request.repository.InfoRepository
import com.vmloft.develop.app.template.request.repository.OrderRepository
import com.vmloft.develop.library.common.utils.json.JsonUtils
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import java.io.File


/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：编辑信息 ViewModel
 */
class UserViewModel(
    private val repo: InfoRepository,
    private val commonRepo: CommonRepository,
    private val followRepo: FollowRepository,
    private val orderRepo: OrderRepository,
) : BViewModel() {

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
                commonRepo.getProfessionList()
            }
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "professionList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
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
            val result = repo.updateUsername(username)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "updateUsername")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 修改用户信息
     */
    fun updateInfo(params: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.updateInfo(JsonUtils.map2json(params).toRequestBody("application/json".toMediaType()))

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "updateInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
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
            val body: RequestBody = file.asRequestBody("image/*".toMediaType())
//            val part: MultipartBody.Part = MultipartBody.Part.createFormData("avatar", file.name, body)
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("desc", "修改头像")
                .addFormDataPart("width", "512")
                .addFormDataPart("height", "512")
                .addFormDataPart("file", file.name, body)
            // 调用上传图片
            val result = repo.updateAvatar(builder.build())

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
            val body: RequestBody = file.asRequestBody("image/*".toMediaType())
//            val part: MultipartBody.Part = MultipartBody.Part.createFormData("cover", file.name, body)
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("desc", "修改封面")
                .addFormDataPart("width", "1080")
                .addFormDataPart("height", "1080")
                .addFormDataPart("file", file.name, body)
            // 调用上传图片
            val result = repo.updateCover(builder.build())

            if (result is RResult.Success) {
                emitUIState(isSuccess = true, data = result.data, type = "updateCover")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(code = result.code, error = result.error)
            }
        }
    }

    /**
     * 绑定邮箱
     */
    fun bindEmail(email: String, code: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.bindEmail(email, code)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "bindEmail")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 更新密码
     */
    fun updatePassword(password: String, oldPassword: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.updatePassword(password, oldPassword)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "updatePassword")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 个人认证
     */
    fun personalAuth(realName: String, idCardNumber: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.personalAuth(realName, idCardNumber)

            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "personalAuth")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取用户信息
     */
    fun userInfo(id: String = "") {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = if (id.isEmpty()) {
                repo.current()
            } else {
                repo.other(id)
            }

            if (result is RResult.Success && result.data != null) {
                // 将用户信息加入到缓存
                CacheManager.putUser(result.data!!)
                emitUIState(data = result.data, type = "userInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 签到
     */
    fun clock() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.clock()
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "clock")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 请求验证码
     */
    fun sendCodeEmail(email: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.sendCodeEmail(email)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "sendCodeEmail")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * --------------------------------------------------------------------
     * 用户交互接口
     */
    /**
     * 关注用户
     */
    fun follow(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = followRepo.follow(id)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "follow")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 取消关注用户
     */
    fun cancelFollow(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = followRepo.cancelFollow(id)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "cancelFollow")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }


    /**
     * 创建订单
     */
    fun createOrder(price: String, title: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = orderRepo.createOrder(price, title)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "createOrder")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

}