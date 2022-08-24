package com.vmloft.develop.library.data.api

import com.vmloft.develop.library.data.bean.*
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResponse
import okhttp3.RequestBody

import retrofit2.http.*

/**
 * Create by lzan13 on 2021/05/25 17:35
 * 描述：交易相关 API 接口，包括商品和订单险相关
 */
interface TradeAPI {

    /**
     * ----------------------------- 商品相关 -----------------------------
     */
    /**
     * 查询商品列表
     * @param type 商品类型 0-金币充值 1-开通/会员 2-普通商品
     */
    @GET("v1/commodity")
    suspend fun commodityList(@Query("type") type: Int, @Query("page") page: Int, @Query("limit") limit: Int): RResponse<RPaging<Commodity>>

    /**
     * ----------------------------- 订单相关 -----------------------------
     */
    /**
     * 创建订单
     * @param price 价格
     * @param title 标题
     * @param type 类型 0-充值 1-购买会员 2-购买商品
     * @param remarks 备注
     * @param extend 扩展
     */
    @FormUrlEncoded
    @POST("v1/order")
    suspend fun createOrder(@Field("commoditys") commoditys: List<String>, @Field("remarks") remarks: String = ""): RResponse<Order>

    /**
     * 销毁订单
     */
    @DELETE("v1/order/{id}")
    suspend fun destroyOrder(@Path("id") id: String): RResponse<Any>

    /**
     * 批量销毁订单
     */
    @FormUrlEncoded
    @DELETE("v1/order")
    suspend fun destroyOrderList(@Field("ids") ids: String): RResponse<Any>

    /**
     * 获取订单列表
     */
    @GET("v1/order")
    suspend fun orderList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("owner") owner: String = "",
    ): RResponse<RPaging<Order>>

    /**
     * 获取订单信息
     */
    @GET("v1/order/{id}")
    suspend fun orderInfo(@Path("id") id: String): RResponse<Order>

    /**
     * 获取订单支付信息
     */
    @GET("v1/order/payInfo/{id}")
    suspend fun orderPayInfo(@Path("id") id: String): RResponse<String>

    /**
     * 视频奖励
     */
    @POST("v1/common/ads/videoReward")
    suspend fun videoReward(@Body body: RequestBody): RResponse<Boolean>

}