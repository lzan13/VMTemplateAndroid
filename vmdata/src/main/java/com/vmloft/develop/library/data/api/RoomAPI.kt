package com.vmloft.develop.library.data.api

import com.vmloft.develop.library.data.bean.*
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResponse

import retrofit2.http.*

/**
 * Create by lzan13 on 2021/05/25 17:35
 * 描述：房间 API 接口
 */
interface RoomAPI {

    /**
     * 创建房间
     */
    @FormUrlEncoded
    @POST("v1/room")
    suspend fun createRoom(
        @Field("title") title: String,
        @Field("desc") desc: String,
        @Field("owner") owner: String
    ): RResponse<Room>

    /**
     * 销毁房间
     */
    @DELETE("v1/room/{id}")
    suspend fun destroyRoom(@Path("id") id: String): RResponse<Any>

    /**
     * 更新房间
     */
    @FormUrlEncoded
    @PUT("v1/room/{id}")
    suspend fun updateRoom(
        @Field("id") id: String,
        @Field("title") title: String,
        @Field("desc") desc: String
    ): RResponse<Room>

    /**
     * 获取房间列表
     */
    @GET("v1/room")
    suspend fun roomList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("type") type: Int
    ): RResponse<RPaging<Room>>

    /**
     * 获取随机房间
     */
    @GET("v1/room")
    suspend fun randomRoom(@Query("type") type: Int): RResponse<Room>

    /**
     * 获取房间信息
     */
    @GET("v1/room/{id}")
    suspend fun roomInfo(@Path("id") id: String): RResponse<Room>

}