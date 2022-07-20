package com.vmloft.develop.library.data.api

import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResponse

import retrofit2.http.*

/**
 * Create by lzan13 on 2020-02-13 17:35
 * 描述：用户关系相关 API 接口
 */
interface RelationAPI {

    /**
     * 关注
     */
    @FormUrlEncoded
    @POST("v1/relation")
    suspend fun follow(@Field("id") id: String): RResponse<Any>

    /**
     * 取消关注
     */
    @FormUrlEncoded
    @POST("v1/relation/cancelFollow")
    suspend fun cancelFollow(@Field("id") id: String): RResponse<Any>

    /**
     * 获取关注列表
     * @param page
     * @param limit
     * @param type 关系类型 0-我关注的 1-关注我的 2-互相关注
     */
    @GET("v1/relation")
    suspend fun relationList(
        @Query("type") type: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): RResponse<RPaging<User>>

}