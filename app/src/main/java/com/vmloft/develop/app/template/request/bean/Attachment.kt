package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:19
 * 描述：附件数据 Bean
 */
@Parcelize
data class Attachment(
    @SerializedName("_id")
    val id: String = "", // 附件 id
    val extname: String = "", // 附件扩展名
    val filename: String = "", // 附件文件名
    val path: String = "", // 附件地址
) : Parcelable {
}