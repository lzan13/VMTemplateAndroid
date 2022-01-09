package com.vmloft.develop.app.template.request.api

import com.vmloft.develop.app.template.request.bean.*
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.request.RResponse

import retrofit2.http.*

/**
 * Create by lzan13 on 2020-02-13 17:35
 * 描述：关注相关 API 接口
 */
interface FollowAPI {

    /**
     * 关注
     */
    @FormUrlEncoded
    @POST("v1/follow")
    suspend fun follow(@Field("id") id: String): RResponse<Any>

    /**
     * 取消关注
     */
    @FormUrlEncoded
    @POST("v1/follow/cancel")
    suspend fun cancelFollow(@Field("id") id: String): RResponse<Any>

    /**
     * 获取关注列表
     */
    @GET("v1/follow")
    suspend fun getFollowList(
        @Query("userId") userId: String,
        @Query("type") type: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): RResponse<RPaging<User>>

}