package com.vmloft.develop.app.template.request.api

import com.vmloft.develop.app.template.request.bean.*
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.request.RResponse

import retrofit2.http.*

/**
 * Create by lzan13 on 2020-02-13 17:35
 * 描述：匹配相关 API 网络接口
 */
interface MatchAPI {

    /**
     * ------------------------------------ 匹配接口  ------------------------------------
     */
    /**
     * 提交匹配数据
     */
    @FormUrlEncoded
    @POST("v1/match")
    suspend fun submitMatch(
        @Field("content") content: String,
        @Field("emotion") emotion: Int,
        @Field("gender") gender: Int,
    ): RResponse<Match>

    /**
     * 删除一条匹配数据
     */
    @DELETE("v1/match/{id}")
    suspend fun removeMatch(@Path("id") id: String): RResponse<Any>

    /**
     * 获取匹配列表
     */
    @GET("v1/match")
    suspend fun getMatchList(
        @Query("gender") gender: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): RResponse<RPaging<Match>>

    /**
     * 随机获取一条匹配数据
     */
    @GET("v1/match/one")
    suspend fun getMatchOne(@Query("gender") type: Int): RResponse<Match>

}