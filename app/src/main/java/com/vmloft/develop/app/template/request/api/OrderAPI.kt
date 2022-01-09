package com.vmloft.develop.app.template.request.api

import com.vmloft.develop.app.template.request.bean.*
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.request.RResponse

import retrofit2.http.*

/**
 * Create by lzan13 on 2021/05/25 17:35
 * 描述：订单 API 接口
 */
interface OrderAPI {

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
    suspend fun createOrder(
        @Field("price") price: String,
        @Field("title") title: String,
        @Field("type") type: Int = 0,
        @Field("remarks") remarks: String = "",
        @Field("extend") extend: String = "",
    ): RResponse<Order>

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
    suspend fun getOrderList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("owner") owner: String = "",
    ): RResponse<RPaging<Order>>

    /**
     * 获取订单信息
     */
    @GET("v1/order/{id}")
    suspend fun getOrderInfo(@Query("id") id: String): RResponse<Order>

}