package com.vmloft.develop.library.data.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:04
 * 描述：版本对象数据 Bean
 */
@Entity
@Parcelize
data class Version(
    @PrimaryKey
    @SerializedName("_id")
    var id: String = "",
    var platform: Int = 0,
    var title: String = "版本升级",
    var desc: String = "暂无更新",
    var url: String = "",
    var negativeBtn:String = "暂不升级",
    var positiveBtn:String = "马上升级",
    var versionCode: Int = 1,
    var versionName: String = "0.0.1",
    var force: Boolean = false,
) : Parcelable {
    @Ignore
    constructor() : this("")
}