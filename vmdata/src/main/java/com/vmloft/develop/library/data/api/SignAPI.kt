package com.vmloft.develop.library.data.api

import com.vmloft.develop.library.data.bean.*
import com.vmloft.develop.library.request.RResponse
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
    @POST("v1/sign/upByEmail")
    suspend fun signUpByEmail(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("devicesId") devicesId: String = VMSystem.deviceId(),
    ): RResponse<User>

    /**
     * 通过手机注册
     */
    @FormUrlEncoded
    @POST("v1/sign/upByPhone")
    suspend fun signUpByPhone(
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("devicesId") devicesId: String = VMSystem.deviceId(),
    ): RResponse<User>

    /**
     * 通过设备 Id注册
     */
    @FormUrlEncoded
    @POST("v1/sign/upByDevicesId")
    suspend fun signUpByDevicesId(
        @Field("devicesId") devicesId: String = VMSystem.deviceId(),
        @Field("password") password: String,
    ): RResponse<User>

    /**
     * 通用登录，自动识别手机号、邮箱、用户名
     */
    @FormUrlEncoded
    @POST("v1/sign/in")
    suspend fun signIn(
        @Field("account") account: String,
        @Field("password") password: String,
    ): RResponse<User>

    /**
     * 通过验证码登录
     */
    @FormUrlEncoded
    @POST("v1/sign/inByCode")
    suspend fun signInByCode(
        @Field("phone") phone: String,
        @Field("code") code: String,
    ): RResponse<User>

    /**
     * 使用 devicesId 登录
     */
    @FormUrlEncoded
    @POST("v1/sign/inByDevicesId")
    suspend fun signInByDevicesId(
        @Field("devicesId") devicesId: String,
        @Field("password") password: String,
    ): RResponse<User>

    /**
     * 退出登录
     */
    @GET("v1/sign/out")
    suspend fun signOut(): RResponse<Any>

    /**
     * 销毁账户
     */
    @GET("v1/sign/destroy")
    suspend fun signDestroy(): RResponse<Any>

    /**
     * 请求邮箱验证码
     */
    @GET("v1/sign/sendCodeEmail")
    suspend fun sendCodeEmail(@Query("email") email: String): RResponse<Any>

}