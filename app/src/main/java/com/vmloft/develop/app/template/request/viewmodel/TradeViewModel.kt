package com.vmloft.develop.app.template.request.viewmodel

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.request.repository.TradeRepository
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants

import com.vmloft.develop.library.pay.ALYPay
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.tools.utils.VMStr

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Create by lzan13 on 2021/8/18 17:28
 * 描述：订单接口
 */
class TradeViewModel(val repo: TradeRepository) : BViewModel() {


    /**
     * ----------------------------- 商品相关 -----------------------------
     */
    /**
     * 获取商品列表
     * @param type 商品类型 0-金币充值 1-开通/会员 2-普通商品
     */
    fun commodityList(type: Int, page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.commodityList(type, page, limit)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "commodityList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }


    /**
     * ----------------------------- 订单相关 -----------------------------
     */
    /**
     * 创建订单
     */
    fun createOrder(commoditys: List<String>, remarks: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.createOrder(commoditys, remarks)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "createOrder")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 销毁订单
     */
    fun destroyOrder(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.destroyOrder(id)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "destroyOrder")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取订单列表
     */
    fun orderList(page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.orderList(page, limit)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "orderList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取订单信息
     */
    fun orderInfo(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.orderInfo(id)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "orderInfo")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 订单支付
     */
    fun orderPay(activity: Activity, id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            // 先获取订单支付信息
            val result = repo.orderPayInfo(id)
            if (result is RResult.Success) {
                // 然后调用支付宝支付
                val payResult = withContext(Dispatchers.IO) {
                    ALYPay.pay(activity, result.data as String)
                }
                if (payResult is RResult.Success) {
                    emitUIState(toast = VMStr.byRes(R.string.order_pay_complete), type = "orderPay")
                } else if (payResult is RResult.Error) {
                    emitUIState(isSuccess = false, code = payResult.code, error = payResult.error)
                }
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }


}