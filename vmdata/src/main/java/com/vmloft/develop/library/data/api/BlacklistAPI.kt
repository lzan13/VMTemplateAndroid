package com.vmloft.develop.library.data.api

import com.vmloft.develop.library.data.bean.*
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResponse

import retrofit2.http.*

/**
 * Create by lzan13 on 2020/04/07 17:35
 * 描述：黑名单相关 API 接口
 */
interface BlacklistAPI {

    /**
     * 拉黑
     */
    @FormUrlEncoded
    @POST("v1/blacklist")
    suspend fun blacklist(@Field("id") id: String): RResponse<Any>

    /**
     * 取消拉黑
     */
    @FormUrlEncoded
    @POST("v1/blacklist/cancel")
    suspend fun cancel(@Field("id") id: String): RResponse<Any>

    /**
     * 获取拉黑列表
     * @param page
     * @param limit
     * @param type 关系类型 0-我拉黑的 1-拉黑我的 2-互相拉黑
     */
    @GET("v1/blacklist")
    suspend fun getBlacklist(
        @Query("type") type: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): RResponse<RPaging<User>>

}