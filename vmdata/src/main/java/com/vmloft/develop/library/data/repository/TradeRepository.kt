package com.vmloft.develop.library.data.repository

import com.vmloft.develop.library.data.api.APIRequest
import com.vmloft.develop.library.data.bean.Commodity
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.data.bean.Order

import okhttp3.RequestBody

/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：交易相关请求，包括商品和订单险相关
 */
class TradeRepository : BaseRepository() {


    /**
     * ----------------------------- 商品相关 -----------------------------
     */
    /**
     * 获取商品列表
     * @param type 商品类型 0-金币充值 1-开通/会员 2-普通商品
     */
    suspend fun commodityList(type: Int, page: Int, limit: Int): RResult<RPaging<Commodity>> {
        return safeRequest { executeResponse(APIRequest.tradeAPI.commodityList(type, page, limit)) }
    }

    /**
     * ----------------------------- 订单相关 -----------------------------
     */
    /**
     * 创建
     */
    suspend fun createOrder(commoditys: List<String>, remarks: String): RResult<Order> {
        return safeRequest { executeResponse(APIRequest.tradeAPI.createOrder(commoditys, remarks)) }
    }

    /**
     * 销毁
     */
    suspend fun destroyOrder(orderId: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.tradeAPI.destroyOrder(orderId)) }
    }

    /**
     * 获取列表
     */
    suspend fun orderList(page: Int, limit: Int): RResult<RPaging<Order>> {
        return safeRequest { executeResponse(APIRequest.tradeAPI.orderList(page, limit)) }
    }

    /**
     * 获取单个信息
     */
    suspend fun orderInfo(id: String): RResult<Order> {
        return safeRequest { executeResponse(APIRequest.tradeAPI.orderInfo(id)) }
    }

    /**
     * 获取订单支付信息
     */
    suspend fun orderPayInfo(id: String): RResult<String> {
        return safeRequest { executeResponse(APIRequest.tradeAPI.orderPayInfo(id)) }
    }

    /**
     * 获取订单支付信息
     */
    suspend fun videoReward(body:RequestBody): RResult<Boolean> {
        return safeRequest { executeResponse(APIRequest.tradeAPI.videoReward(body)) }
    }

}