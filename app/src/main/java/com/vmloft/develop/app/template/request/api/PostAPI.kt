package com.vmloft.develop.app.template.request.api

import com.vmloft.develop.app.template.request.bean.*
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResponse

import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Create by lzan13 on 2020-02-13 17:35
 * 描述：Post API 接口
 */
interface PostAPI {

    /**
     * ------------------------------------ 帖子接口  ------------------------------------
     */
    /**
     * 创建帖子
     */
    @FormUrlEncoded
    @POST("v1/post")
    suspend fun createPost(
        @Field("content") content: String,
        @Field("category") category: String,
        @Field("attachments") attachments: List<String>,
        @Field("stick") stick: Int = 0,
        @Field("extension") extension: String = "",
    ): RResponse<Post>

    /**
     * 删除
     */
    @DELETE("v1/post/{id}")
    suspend fun deletePost(@Path("id") id: String): RResponse<Any>

    /**
     * 更新
     */
    @PUT("v1/post/{id}")
    suspend fun updatePost(@Field("id") id: String, @Body body: RequestBody): RResponse<Post>

    /**
     * 获取帖子信息
     */
    @GET("v1/post/{id}")
    suspend fun postInfo(@Path("id") id: String): RResponse<Post>

    /**
     * 获取
     */
    @GET("v1/post")
    suspend fun postList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("owner") owner: String,
    ): RResponse<RPaging<Post>>

    /**
     * ------------------------------------ 帖子评论接口  ------------------------------------
     */
    /**
     * 创建
     */
    @FormUrlEncoded
    @POST("v1/comment")
    suspend fun createComment(
        @Field("content") content: String,
        @Field("post") post: String,
        @Field("user") user: String,
    ): RResponse<Comment>

    /**
     * 删除
     */
    @DELETE("v1/comment/{id}")
    suspend fun deleteComment(@Path("id") id: String): RResponse<Any>

    /**
     * 获取
     */
    @GET("v1/comment")
    suspend fun getCommentList(
        @Query("post") post: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): RResponse<RPaging<Comment>>

}