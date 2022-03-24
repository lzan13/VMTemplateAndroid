package com.vmloft.develop.app.template.request.viewmodel

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.im.IMManager
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.repository.SignRepository
import com.vmloft.develop.library.base.BViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：注册登录 ViewModel
 */
class SignViewModel(private val repo: SignRepository) : BViewModel() {

    /**
     * 通过设备 Id 注册
     */
    fun signUpByDevicesId(devicesId: String, password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.signUpByDevicesId(devicesId, password)
            if (result is RResult.Success && result.data != null && result.data is User) {
                // 注册成功存储下登录信息
                SignManager.setToken(result.data!!.token)
                SignManager.setCurrUser(result.data)
                emitUIState(data = result.data, type = "signUpByDevicesId")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 通过邮箱注册
     */
    fun signUpByEmail(email: String, password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.signUpByEmail(email, password)

            if (result is RResult.Success && result.data != null && result.data is User) {
                // 注册成功存储下登录信息
                SignManager.setToken(result.data!!.token)
                SignManager.setCurrUser(result.data)
                emitUIState(data = result.data, type = "signUpByEmail")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 通过手机号注册
     */
    fun signUpByPhone(phone: String, password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.signUpByPhone(phone, password)

            if (result is RResult.Success && result.data != null && result.data is User) {
                // 注册成功存储下登录信息
                SignManager.setToken(result.data!!.token)
                SignManager.setCurrUser(result.data)
                emitUIState(data = result.data, type = "signUpByPhone")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 通用登录，自动识别手机号、邮箱、用户名
     */
    fun signIn(account: String, password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.signIn(account, password)
            if (result is RResult.Success && result.data != null && result.data is User) {
                // 登录成功存储下登录信息
                SignManager.setToken(result.data!!.token)
                SignManager.setCurrUser(result.data)
                emitUIState(data = result.data, type = "signIn")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 使用 devicesId 登录
     */
    fun signInByDevicesId(devicesId: String, password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.signInByDevicesId(devicesId, password)
            if (result is RResult.Success && result.data != null && result.data is User) {
                // 登录成功存储下登录信息
                SignManager.setToken(result.data!!.token)
                SignManager.setCurrUser(result.data)
                emitUIState(data = result.data, type = "signInByDevicesId")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error, type = "signInByDevicesId")
            }
        }
    }

    /**
     * 通过验证码登录
     */
    fun signInByCode(phone: String, code: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.signInByCode(phone, code)
            if (result is RResult.Success && result.data != null && result.data is User) {
                // 登录成功存储下登录信息
                SignManager.setToken(result.data!!.token)
                SignManager.setCurrUser(result.data)
                emitUIState(data = result.data, type = "signInByCode")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 请求短信验证码
     */
    fun requestCodeBySMS(phone: String) {
//        viewModelScope.launch(Dispatchers.Main) {
//            emitUIState(true)
//            val result = SMSManager.requestCode("86", phone)
//            if (result is RResult.Success) {
//                emitUIState(data = result.data, type = "requestCodeBySMS")
//                return@launch
//            } else if (result is RResult.Error) {
//                emitUIState(isSuccess = false, code = result.code, error = result.error)
//            }
//        }
    }

    /**
     * 验证本机号码
     */
    fun verifyLocalPhone(phone: String) {
//        viewModelScope.launch(Dispatchers.Main) {
//            emitUIState(true)
//            val result = SMSManager.verifyLocalPhone(phone)
//            if (result is RResult.Success) {
//                emitUIState(data = result.data, type = "verifyLocalPhone")
//                return@launch
//            } else if (result is RResult.Error) {
//                emitUIState(isSuccess = false, code = result.code, error = result.error)
//            }
//        }
    }


    /**
     * 退出登录
     */
    fun signOut() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.signOut()
            if (result is RResult.Success) {
                emitUIState(type = "signOut")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * -------------------------------------------------------
     * 登录 IM 账户，im 的注册在业务服务器调用，客户端只有登录接口
     */
    fun signInIM() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = IMManager.signIn()
            if (result is RResult.Success) {
                emitUIState(type = "signInIM")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }
}