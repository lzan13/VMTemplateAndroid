package com.vmloft.develop.app.template.request.repository

import com.vmloft.develop.app.template.request.api.APIRequest
import com.vmloft.develop.library.common.request.BaseRepository
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.app.template.request.bean.User
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
        return safeRequest(call = { requestUpdateUsername(username) })
    }

    private suspend fun requestUpdateUsername(username: String): RResult<User> =
        executeResponse(APIRequest.userInfoAPI.updateUsername(username))

    /**
     * 更新用户信息
     */
    suspend fun updateInfo(body: RequestBody): RResult<User> {
        return safeRequest(call = { requestUpdateInfo(body) })
    }

    private suspend fun requestUpdateInfo(body: RequestBody): RResult<User> =
        executeResponse(APIRequest.userInfoAPI.updateInfo(body))

    /**
     * 更新头像
     */
    suspend fun updateAvatar(part: MultipartBody.Part): RResult<User> {
        return safeRequest(call = { requestUpdateAvatar(part) })
    }

    private suspend fun requestUpdateAvatar(part: MultipartBody.Part): RResult<User> =
        executeResponse(APIRequest.userInfoAPI.updateAvatar(part))

    /**
     * 更新封面
     */
    suspend fun updateCover(part: MultipartBody.Part): RResult<User> {
        return safeRequest(call = { requestUpdateCover(part) })
    }

    private suspend fun requestUpdateCover(part: MultipartBody.Part): RResult<User> =
        executeResponse(APIRequest.userInfoAPI.updateCover(part))

    /**
     * 更新密码
     */
    suspend fun updatePassword(password: String, oldPassword: String): RResult<Any> {
        return safeRequest(call = { requestUpdatePassword(password, oldPassword) })
    }

    private suspend fun requestUpdatePassword(password: String, oldPassword: String): RResult<Any> =
        executeResponse(APIRequest.userInfoAPI.updatePassword(password, oldPassword))

    /**
     * 个人认证
     */
    suspend fun personalAuth(realName: String, idCardNumber: String): RResult<Any> {
        return safeRequest(call = { requestPersonalAuth(realName, idCardNumber) })
    }

    private suspend fun requestPersonalAuth(realName: String, idCardNumber: String): RResult<Any> =
        executeResponse(APIRequest.userInfoAPI.personalAuth(realName, idCardNumber))

    /**
     * 获取当前用户信息
     */
    suspend fun current(): RResult<User> {
        return safeRequest(call = { requestCurrentUser() })
    }

    private suspend fun requestCurrentUser(): RResult<User> =
        executeResponse(APIRequest.userInfoAPI.current())

    /**
     * 获取其他用户信息
     */
    suspend fun other(id: String): RResult<User> {
        return safeRequest(call = { requestOtherUser(id) })
    }

    private suspend fun requestOtherUser(id: String): RResult<User> =
        executeResponse(APIRequest.userInfoAPI.other(id))

    /**
     * 根据指定用户 id 集合的用户信息
     */
    suspend fun getUserList(ids: List<String>): RResult<List<User>> {
        return safeRequest(call = { requestUserList(ids) })
    }

    private suspend fun requestUserList(ids: List<String>): RResult<List<User>> =
        executeResponse(APIRequest.userInfoAPI.getUserList(ids))

    /**
     * 签到
     */
    suspend fun clock(): RResult<Any> {
        return safeRequest(call = { requestClock() })
    }

    private suspend fun requestClock(): RResult<Any> =
        executeResponse(APIRequest.userInfoAPI.clock())
}