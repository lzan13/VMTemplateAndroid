package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:04
 * 描述：角色对象数据 Bean
 */
@Parcelize
data class Role(
    @SerializedName("_id")
    val id: String,
    val title: String = "",
    val desc: String = "",
    val identity: Int = 0,
) : Parcelable {
}