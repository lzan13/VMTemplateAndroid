package com.vmloft.develop.app.template.request.api

import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResponse

import retrofit2.http.*

/**
 * Create by lzan13 on 2020-02-13 17:35
 * 描述：喜欢相关 API 接口
 */
interface LikeAPI {

    /**
     * 喜欢
     * @param type 喜欢类型 0-用户 1-帖子 2-评论
     * @param id 喜欢的数据 id
     */
    @FormUrlEncoded
    @POST("v1/like")
    suspend fun like(
        @Field("type") type: Int,
        @Field("id") id: String,
    ): RResponse<Any>

    /**
     * 取消关注
     */
    @FormUrlEncoded
    @POST("v1/like/cancel")
    suspend fun cancelLike(
        @Field("type") type: Int,
        @Field("id") id: String,
    ): RResponse<Any>

    /**
     * 获取喜欢列表
     * @param type 喜欢类型 0-用户，1-帖子，2-评论
     * @param id 喜欢的数据 id
     * @param owner 发起喜欢的人，查询指定用户喜欢的数据是用到
     */
    @GET("v1/like")
    suspend fun getLikeUserList(
        @Query("type") type: Int = 0,
        @Query("id") id: String,
        @Query("owner") owner: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): RResponse<RPaging<User>>

    /**
     * 获取喜欢列表
     * @param type 喜欢类型 0-用户，1-帖子，2-评论
     * @param id 喜欢的数据 id
     * @param owner 发起喜欢的人，查询指定用户喜欢的数据是用到
     */
    @GET("v1/like")
    suspend fun likePostList(
        @Query("type") type: Int = 1,
        @Query("id") id: String,
        @Query("owner") owner: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): RResponse<RPaging<Post>>

}