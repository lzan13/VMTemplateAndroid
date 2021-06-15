package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Create by lzan13 on 2021/01/20 16:12
 * 描述：签到数据 Bean
 */
@Parcelize
@Entity
data class Clock(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,
    val userId: String = "",
    val createdAt: String = "",
) : Parcelable {
}