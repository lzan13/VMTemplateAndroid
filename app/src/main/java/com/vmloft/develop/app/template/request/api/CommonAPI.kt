package com.vmloft.develop.app.template.request.api

import com.vmloft.develop.app.template.request.bean.*
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.request.RResponse

import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Create by lzan13 on 2020-02-13 17:35
 * 描述：一些通用相关 API 网络接口
 */
interface CommonAPI {

    /**
     * --------------------------------- 上传文件接口 ---------------------------------
     */
    /**
     * 上传单附件
     */
    @Multipart
    @POST("v1/attachment")
    suspend fun upload(@Part file: MultipartBody.Part): RResponse<Attachment>

    /**
     * 通过网络地址上传附件
     */
    @FormUrlEncoded
    @POST("v1/attachment/url")
    suspend fun uploadByUrl(@Field("url") url: String): RResponse<Attachment>

    /**
     * 多附件上传
     */
    @POST("v1/attachments")
    suspend fun uploadMultipart(@Body body: MultipartBody): RResponse<List<Attachment>>

    /**
     * 删除附件
     */
    @POST("v1/attachment/{id}")
    suspend fun deletePicture(@Path("id") id: String): RResponse<Any>

    /**
     * ------------------------------------ 职业分类等接口  ------------------------------------
     */
    /**
     * 获取分类列表
     */
    @GET("v1/info/category")
    suspend fun getCategoryList(
        @Query("page") page: Int = CConstants.defaultPage,
        @Query("limit") limit: Int = CConstants.defaultLimit,
    ): RResponse<RPaging<Category>>

    /**
     * 获取职业列表
     */
    @GET("v1/info/profession")
    suspend fun getProfessionList(
        @Query("page") page: Int = CConstants.defaultPage,
        @Query("limit") limit: Int = CConstants.defaultLimit,
    ): RResponse<RPaging<Profession>>

    /**
     * ------------------------------------ 获取配置相关  ------------------------------------
     */
    /**
     * 提交反馈
     */
    @POST("v1/checkVersion")
    suspend fun checkVersion(@Query("platform") platform: String): RResponse<Config>


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