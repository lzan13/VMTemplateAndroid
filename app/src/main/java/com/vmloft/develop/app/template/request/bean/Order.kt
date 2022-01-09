package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:04
 * 描述：订单对象数据 Bean
 */
@Parcelize
data class Order(
    @SerializedName("_id")
    var id: String = "",
    var owner: String = "", // 订单所属用户
    var orderId: String = "", // 订单 Id
    var price: String = "", // 订单价格
    var realPrice: String = "", // 实际支付价格
    var status: Int = 0, // 订单状态 0-待支付 1-支付成功 2-已关闭
    var title: String = "", // 订单标题
    var type: Int = 0, // 订单类型 0-充值 1-购买会员 2-购买商品
    var payType: Int = 0, // 支付类型 0-微信 1-支付宝，支付成功才会有
    var remarks: String = "", // 订单备注
    var extend: String = "", // 其他扩展
    var updatedAt: String = "",
    var createdAt: String = "",
) : Parcelable {
    /**
     * 扩展信息
     * {
     *  "wxCode":"http://zyphoto.itluntan.cn/20210809161857",
     *  "zfbCode":"http://zyphoto.itluntan.cn/20210809161957",
     *  "zfbScheme":"alipayqr://platformapi/startapp?saId=10000007&qrcode=https%3A%2F%2Fqr.alipay.com%2Ftsx14575pzorxigphwtdj84",
     *  "afbPayUrl":"https://admin.zhanzhangfu.com/common/zfbuserid?zfbuserid=2088602250620913&price=8.00"
     * }
     */
}