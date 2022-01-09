package com.vmloft.develop.app.template.request.repository

import com.vmloft.develop.app.template.request.api.APIRequest
import com.vmloft.develop.library.common.request.BaseRepository
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.request.bean.Order

/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：订单相关请求
 */
class OrderRepository : BaseRepository() {

    /**
     * 创建
     */
    suspend fun createOrder(price: String, title: String): RResult<Order> {
        return safeRequest { executeResponse(APIRequest.orderAPI.createOrder(price, title)) }
    }

    /**
     * 销毁
     */
    suspend fun destroyOrder(orderId: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.orderAPI.destroyOrder(orderId)) }
    }

    /**
     * 获取列表
     */
    suspend fun getOrderList(page: Int, limit: Int): RResult<RPaging<Order>> {
        return safeRequest { executeResponse(APIRequest.orderAPI.getOrderList(page, limit)) }
    }

    /**
     * 获取单个信息
     */
    suspend fun getOrderInfo(id: String): RResult<Order> {
        return safeRequest { executeResponse(APIRequest.orderAPI.getOrderInfo(id)) }
    }

}