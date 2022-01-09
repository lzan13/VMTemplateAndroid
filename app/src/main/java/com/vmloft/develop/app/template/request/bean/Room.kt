package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Create by lzan13 on 2021/5/25 16:08
 * 描述：房间对象数据 Bean
 */
@Parcelize
data class Room(
    @SerializedName("_id")
    var id: String="",
    var owner: User=User(),
    var managers: MutableList<User> = mutableListOf(),
    var members: MutableList<User> = mutableListOf(),
    var title: String = "",
    var desc: String = "",
    var extension: String = "",
    var count: Int = 0,
    var maxCount: Int = 500,
) : Parcelable {
}