package com.vmloft.develop.library.data.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2021/01/20 16:12
 * 描述：签到数据 Bean
 */
@Parcelize
data class Clock(
    @SerializedName("_id")
    var id: String = "",
    var userId: String = "",
    var createdAt: Long = 0,
) : Parcelable {
}