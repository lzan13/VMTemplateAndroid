package com.vmloft.develop.library.data.repository

import com.vmloft.develop.library.data.api.APIRequest
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.data.bean.User

import okhttp3.MultipartBody
import okhttp3.RequestBody


/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：用户信息相关请求
 */
class InfoRepository : BaseRepository() {

    /**
     * 更新用户名
     */
    suspend fun updateUsername(username: String): RResult<User> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.updateUsername(username)) }
    }

    /**
     * 更新头像
     */
    suspend fun updateAvatar(body: MultipartBody): RResult<User> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.updateAvatar(body)) }
    }

    /**
     * 更新封面
     */
    suspend fun updateCover(body: MultipartBody): RResult<User> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.updateCover(body)) }
    }

    /**
     * 更新用户信息
     */
    suspend fun updateInfo(body: RequestBody): RResult<User> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.updateInfo(body)) }
    }

    /**
     * 绑定邮箱
     */
    suspend fun bindEmail(email: String, code: String): RResult<User> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.bindEmail(email, code)) }
    }

    /**
     * 更新密码
     */
    suspend fun updatePassword(password: String, email: String, code: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.updatePassword(password, email, code)) }
    }

    /**
     * 个人认证
     */
    suspend fun realAuth(realName: String, idCardNumber: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.realAuth(realName, idCardNumber)) }
    }

    /**
     * 获取当前用户信息
     */
    suspend fun current(): RResult<User> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.current()) }
    }

    /**
     * 获取其他用户信息
     */
    suspend fun other(id: String): RResult<User> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.other(id)) }
    }

    /**
     * 根据指定用户 id 集合的用户信息
     */
    suspend fun getUserList(ids: List<String>): RResult<List<User>> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.getUserList(ids)) }
    }

    /**
     * 签到
     */
    suspend fun clock(): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.clock()) }
    }

    /**
     * 请求验证码
     */
    suspend fun sendCodeEmail(email: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.signAPI.sendCodeEmail(email)) }
    }

}