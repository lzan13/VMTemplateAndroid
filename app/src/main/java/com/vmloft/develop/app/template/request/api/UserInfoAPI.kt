package com.vmloft.develop.app.template.request.api

import com.vmloft.develop.app.template.request.bean.*
import com.vmloft.develop.library.common.request.RResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Create by lzan13 on 2020-02-13 17:35
 * 描述：用户信息 API 网络接口
 */
interface UserInfoAPI {

    /**
     * ------------------------------------ 用户信息接口  ------------------------------------
     */

    /**
     * 更新用户信息
     */
    @PUT("v1/info")
    suspend fun updateInfo(@Body body: RequestBody): RResponse<User>

    /**
     * 更新用户名
     */
    @FormUrlEncoded
    @PUT("v1/info/username")
    suspend fun updateUsername(@Field("username") username: String): RResponse<User>

    /**
     * 更新当前账户头像
     */
    @Multipart
    @PUT("v1/info/avatar")
    suspend fun updateAvatar(@Part avatar: MultipartBody.Part): RResponse<User>

    /**
     * 更新当前账户封面
     */
    @Multipart
    @PUT("v1/info/cover")
    suspend fun updateCover(@Part cover: MultipartBody.Part): RResponse<User>

    /**
     * 更新当前用户邮箱绑定
     */
    @FormUrlEncoded
    @PUT("v1/info/bindEmail")
    suspend fun bindEmail(
        @Field("email") email: String,
        @Field("code") code: String,
    ): RResponse<User>

    /**
     * 更新密码
     */
    @FormUrlEncoded
    @PUT("v1/info/password")
    suspend fun updatePassword(
        @Field("password") password: String,
        @Field("oldPassword") oldPassword: String,
    ): RResponse<Any>

    /**
     * 个人认证
     */
    @FormUrlEncoded
    @PUT("v1/info/personalAuth")
    suspend fun personalAuth(
        @Field("realName") realName: String,
        @Field("idCardNumber") idCardNumber: String,
    ): RResponse<Any>

    /**
     * 获取当前用户信息
     */
    @GET("v1/info/current")
    suspend fun current(): RResponse<User>

    /**
     * 获取其他用户信息
     */
    @GET("v1/info/other/{id}")
    suspend fun other(@Path("id") id: String): RResponse<User>

    /**
     * 根据指定用户 id 集合的用户信息
     */
    @FormUrlEncoded
    @POST("v1/info/ids")
    suspend fun getUserList(@Field("ids") ids: List<String>): RResponse<List<User>>

    /**
     * 签到操作
     */
    @GET("v1/info/clock")
    suspend fun clock(): RResponse<Any>

    /**
     * 检查版本
     */
    @GET("v1/info/checkVersion")
    suspend fun checkVersion(@Query("platform") platform: String = "android"): RResponse<Version>
}