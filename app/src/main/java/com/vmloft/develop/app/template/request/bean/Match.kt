package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:15
 * 描述：喜欢数据 Bean
 */
@Parcelize
@Entity
data class Match(
    @PrimaryKey
    @SerializedName("_id")
    var id: String,
    var user: User,
    var emotion: Int = 0, // 心情 0-开心 1-平淡 2-难过 3-愤怒
    var type: Int = 0,
    var content: String = "",
    var createdAt: String = "",
) : Parcelable {
}