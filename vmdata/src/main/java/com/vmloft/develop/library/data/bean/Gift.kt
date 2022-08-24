package com.vmloft.develop.library.data.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2020/11/22
 * 描述：礼物数据 Bean
 */
@Entity
@Parcelize
data class Gift(
    @PrimaryKey
    @SerializedName("_id")
    var id: String = "", // Id
    var title: String = "", // 标题
    var desc: String = "", // 描述
    var cover: Attachment = Attachment(), // 礼物封面
    var animation: Attachment = Attachment(), // 礼物动效
    var price: Int = 0, // 价格
    var status: Int = 0, // 状态 0-待上架 1-上架中 2-已下架
    var type: Int = 0, // 类型 0-普通礼物 1-特效礼物
    var createdAt: Long = 0,

    var count: Int = 0, // 数量
    var isSelected: Boolean = false, // 这个只是自己本地参数
) : Parcelable {
    @Ignore
    constructor() : this("")

    override fun equals(other: Any?): Boolean {
        (other as? Gift)?.let {
            return it.id == this.id
        }
        return false
    }
}