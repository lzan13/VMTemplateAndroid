package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:19
 * 描述：附件数据 Bean
 */
@Parcelize
data class Attachment(
    @SerializedName("_id")
    val id: String = "", // 附件 id
    val desc: String = "", // 附件描述
    val duration: Int = 0, // 附件时长，video/voice 有值
    val extname: String = "", // 附件扩展名
    val path: String = "", // 附件地址
    val width: Int = 0, // 附件宽
    val height: Int = 0, // 附件高
) : Parcelable {
}