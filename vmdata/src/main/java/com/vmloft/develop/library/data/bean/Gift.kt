package com.vmloft.develop.library.data.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2020/11/22
 * 描述：礼物数据 Bean
 */
@Parcelize
@Entity
data class Gift(
    @PrimaryKey
    @SerializedName("_id")
    var id: String = "", // Id
    var title: String = "", // 标题
    val desc: String = "", // 描述
    val cover: Attachment = Attachment(), // 礼物封面
    val animation: Attachment = Attachment(), // 礼物动效
    val price: Int = 0, // 价格
    val status: Int = 0, // 状态 0-待上架 1-上架中 2-已下架
    val type: Int = 0, // 类型 0-普通礼物 1-特效礼物
    var createdAt: String = "",

    var count: Int = 0, // 数量
    var isSelected: Boolean = false, // 这个只是自己本地参数
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        (other as? Gift)?.let {
            return it.id == this.id
        }
        return false
    }
}