package com.vmloft.develop.library.data.bean

import android.net.Uri
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
    var id: String = "", // 附件 id
    var desc: String = "", // 附件描述
    var duration: Int = 0, // 附件时长，video/voice 有值
    var extname: String = "", // 附件扩展名
    var path: String = "", // 附件地址
    var width: Int = 0, // 附件宽
    var height: Int = 0, // 附件高

    var uri: Uri? = null, // 本地文件，上传前用
) : Parcelable {
}