package com.vmloft.develop.app.template.request.viewmodel

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.request.repository.OrderRepository

import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.request.RResult

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Create by lzan13 on 2021/8/18 17:28
 * 描述：订单接口
 */
class OrderViewModel(val repository: OrderRepository) : BViewModel() {

    /**
     * 创建
     */
    fun createOrder(price: String, title: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.createOrder(price, title)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "createOrder")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 销毁
     */
    fun destroyOrder(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.destroyOrder(id)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "destroyOrder")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取列表
     */
    fun getOrderList(page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.getOrderList(page, limit)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "orderList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取信息
     */
    fun getOrderInfo(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.getOrderInfo(id)
            if (result is RResult.Success) {
                emitUIState(data = result.data, toast = result.msg, type = "orderInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }
}