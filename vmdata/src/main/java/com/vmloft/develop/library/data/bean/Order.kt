package com.vmloft.develop.library.data.bean

import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:04
 * 描述：订单对象数据 Bean
 */
@Parcelize
data class Order(
    @SerializedName("_id")
    var id: String = "",
    var owner: String = "", // 订单所属用户
    var price: Int = 0, // 订单价格
    var realPrice: Int = 0, // 实际价格
    var commoditys: List<Commodity> = mutableListOf(), // 包含的商品
    var status: Int = 0, // 订单状态 0-待支付 1-支付成功 2-已关闭
    var title: String = "", // 订单标题
    var payType: Int = 0, // 支付类型 0-微信 1-支付宝，支付成功才会有
    var remarks: String = "", // 订单备注
    var extend: String = "", // 其他扩展
    var updatedAt: String = "",
    var createdAt: String = "",
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        (other as? Order)?.let {
            return it.id == this.id
        }
        return false
    }
}