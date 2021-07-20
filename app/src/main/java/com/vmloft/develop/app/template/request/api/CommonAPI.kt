package com.vmloft.develop.app.template.request.api

import com.vmloft.develop.app.template.request.bean.*
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.request.RResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Create by lzan13 on 2020-02-13 17:35
 * 描述：一些通用相关 API 网络接口
 */
interface CommonAPI {

    /**
     * --------------------------------- 附件件接口 ---------------------------------
     */
    /**
     * UCloud 上传附件回调接口
     */
    @POST("v1/third/ucloud/callbackObj")
    suspend fun ucloudCallbackObj(@Body body: RequestBody): RResponse<Attachment>

    /**
     * ------------------------------------ 通用接口  ------------------------------------
     */
    /**
     * 获取分类列表
     */
    @GET("v1/common/category")
    suspend fun getCategoryList(
        @Query("page") page: Int = CConstants.defaultPage,
        @Query("limit") limit: Int = CConstants.defaultLimit,
    ): RResponse<RPaging<Category>>

    /**
     * 获取职业列表
     */
    @GET("v1/common/profession")
    suspend fun getProfessionList(
        @Query("page") page: Int = CConstants.defaultPage,
        @Query("limit") limit: Int = CConstants.defaultLimit,
    ): RResponse<RPaging<Profession>>

    /**
     * 检查版本
     */
    @GET("v1/common/checkVersion")
    suspend fun checkVersion(@Query("platform") platform: String = "android"): RResponse<Version>

    /**
     * 获取客户端配置
     */
    @GET("v1/common/clientConfig")
    suspend fun getClientConfig(): RResponse<Config>

    /**
     * 获取隐私zhegn
     */
    @GET("v1/common/privacyPolicy")
    suspend fun getPrivacyPolicy(): RResponse<Config>

    /**
     * 获取用户协议
     */
    @GET("v1/common/userAgreement")
    suspend fun getUserAgreement(): RResponse<Config>


    /**
     * ------------------------------------ 反馈接口  ------------------------------------
     */
    /**
     * 提交反馈
     */
    @FormUrlEncoded
    @POST("v1/feedback")
    suspend fun feedback(
        @Field("contact") contact: String,
        @Field("content") content: String,
        @Field("attachment") attachment: String,
    ): RResponse<Any>

}