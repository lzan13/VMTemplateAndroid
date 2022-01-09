package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Create by lzan13 on 2020/11/22
 * 描述：商品数据 Bean
 */
@Parcelize
data class Commodity(
    @SerializedName("_id")
    var id: String = "", // 商品标题
    val desc: String = "", // 商品描述
    val price: String = "", // 商品价格
    val currPrice: String = "", // 优惠价格
    val attachments: List<Attachment> = mutableListOf(), // 附件信息
    val status: Int = 0, // 商品状态 0-待上架 1-上架中 2-已下架 3-售罄
    val stockCount: Int = 0, // 库存数量
    val type: Int = 0, // 商品类型 0-金币充值 1-开通/会员 2-普通商品
    val level: Int = 0, // 开通/续费会员级别，0-月度 1-季度 2-年度 商品 type == 1 需要
    val remarks: String = "", // 商品备注
    var createdAt: String = "",
) : Parcelable {
}