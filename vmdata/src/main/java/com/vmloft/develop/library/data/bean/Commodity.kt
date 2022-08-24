package com.vmloft.develop.library.data.bean

import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2020/11/22
 * 描述：商品数据 Bean
 */
@Parcelize
data class Commodity(
    @SerializedName("_id")
    var id: String = "", // 商品Id
    var title: String = "", // 商品标题
    var desc: String = "", // 商品描述
    var price: Int = 0, // 商品价格
    var currPrice: Int = 0, // 优惠价格
    var attachments: List<Attachment> = mutableListOf(), // 附件信息
    var status: Int = 0, // 商品状态 0-待上架 1-上架中 2-已下架 3-售罄
    var stockCount: Int = 0, // 库存数量
    var type: Int = 0, // 商品类型 0-金币充值 1-开通/会员 2-普通商品
    var level: Int = 0, // 开通/续费会员级别，0-月度 1-季度 2-年度 商品 type == 1 需要
    var remarks: String = "", // 商品备注
    var createdAt: Long = 0,

    var isSelected: Boolean = false, // 这个只是自己本地配置
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        (other as? Commodity)?.let {
            return it.id == this.id
        }
        return false
    }
}