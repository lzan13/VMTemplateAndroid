package com.vmloft.develop.app.template.request.repository

import com.vmloft.develop.app.template.request.api.APIRequest
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.app.template.request.bean.User


/**
 * Create by lzan13 on 2020/04/09 09:08
 * 描述：注册登录请求
 */
class SignRepository : BaseRepository() {

    /**
     * 通过设备 Id 注册
     */
    suspend fun signUpByDevicesId(devicesId: String, password: String): RResult<User> {
        return safeRequest { executeResponse(APIRequest.signAPI.signUpByDevicesId(devicesId, password)) }
    }


    /**
     * 通过邮箱注册
     */
    suspend fun signUpByEmail(email: String, password: String): RResult<User> {
        return safeRequest { executeResponse(APIRequest.signAPI.signUpByEmail(email, password)) }
    }


    /**
     * 通过手机号注册
     */
    suspend fun signUpByPhone(phone: String, password: String): RResult<User> {
        return safeRequest { executeResponse(APIRequest.signAPI.signUpByPhone(phone, password)) }
    }


    /**
     * 通用登录，自动识别手机号、邮箱、用户名
     */
    suspend fun signIn(account: String, password: String): RResult<User> {
        return safeRequest { executeResponse(APIRequest.signAPI.signIn(account, password)) }
    }


    /**
     * 通过验证码登录
     */
    suspend fun signInByCode(phone: String, code: String): RResult<User> {
        return safeRequest { executeResponse(APIRequest.signAPI.signInByCode(phone, code)) }
    }


    /**
     * 使用 devicesId 登录
     */
    suspend fun signInByDevicesId(devicesId: String, password: String): RResult<User> {
        return safeRequest { executeResponse(APIRequest.signAPI.signInByDevicesId(devicesId, password)) }
    }


    /**
     * 退出登录
     */
    suspend fun signOut(): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.signAPI.signOut()) }
    }


    /**
     * 销毁账户
     */
    suspend fun signDestroy(): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.signAPI.signDestroy()) }
    }


    /**
     * 请求验证码
     */
    suspend fun sendCodeEmail(email: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.signAPI.sendCodeEmail(email)) }
    }


}