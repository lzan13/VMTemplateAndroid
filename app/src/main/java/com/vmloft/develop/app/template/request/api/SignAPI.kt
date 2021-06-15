package com.vmloft.develop.app.template.request.api

import com.vmloft.develop.app.template.request.bean.*
import com.vmloft.develop.library.common.request.RResponse
import com.vmloft.develop.library.tools.utils.VMSystem

import retrofit2.http.*

/**
 * Create by lzan13 on 2020-02-13 17:35
 * 描述：注册登录 API 网络接口
 */
interface SignAPI {

    /**
     * ------------------------------------ 注册登录接口  ------------------------------------
     */
    /**
     * 通过邮箱注册
     */
    @FormUrlEncoded
    @POST("/api/sign/upByEmail")
    suspend fun signUpByEmail(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("devicesId") devicesId: String = VMSystem.deviceId()
    ): RResponse<User>

    /**
     * 通过手机注册
     */
    @FormUrlEncoded
    @POST("/api/sign/upByPhone")
    suspend fun signUpByPhone(
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("devicesId") devicesId: String = VMSystem.deviceId()
    ): RResponse<User>

    /**
     * 通过设备 Id注册
     */
    @FormUrlEncoded
    @POST("/api/sign/upByDevicesId")
    suspend fun signUpByDevicesId(
        @Field("devicesId") devicesId: String = VMSystem.deviceId(),
        @Field("password") password: String
    ): RResponse<User>

    /**
     * 通用登录，自动识别手机号、邮箱、用户名
     */
    @FormUrlEncoded
    @POST("/api/sign/in")
    suspend fun signIn(
        @Field("account") account: String,
        @Field("password") password: String,
    ): RResponse<User>

    /**
     * 通过验证码登录
     */
    @FormUrlEncoded
    @POST("/api/sign/inByCode")
    suspend fun signInByCode(
        @Field("phone") phone: String,
        @Field("code") code: String,
    ): RResponse<User>

    /**
     * 通过设备 Id 登录
     */
    @FormUrlEncoded
    @POST("/api/sign/inByDevicesId")
    suspend fun signInByDevicesId(
        @Field("devicesId") devicesId: String,
        @Field("password") password: String,
    ): RResponse<User>

    /**
     * 退出登录
     */
    @GET("/api/sign/out")
    suspend fun signOut(): RResponse<Any>

}