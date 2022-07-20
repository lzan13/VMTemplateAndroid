package com.vmloft.develop.library.data.api

import com.vmloft.develop.library.data.bean.*
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResponse

import retrofit2.http.*

/**
 * Create by lzan13 on 2020-02-13 17:35
 * 描述：一些通用相关 API 网络接口
 */
interface GiftAPI {

    /**
     * 赠送礼物
     */
    @FormUrlEncoded
    @POST("v1/gift/give")
    suspend fun giftGive(@Field("userId") userId: String, @Field("giftId") giftId: String, @Field("count") count: Int): RResponse<Any>

    /**
     * 获取礼物列表
     */
    @GET("v1/gift")
    suspend fun giftList(@Query("status") status: Int = 1): RResponse<RPaging<Gift>>

    /**
     * ------------------ 礼物记录相关 ----------------------
     */
    /**
     * 获取收到的礼物列表
     */
    @GET("v1/giftRelation")
    suspend fun giftRelationList(@Query("user") userId: String): RResponse<RPaging<Gift>>

}