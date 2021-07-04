package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

import kotlinx.android.parcel.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:12
 * 描述：配置数据 Bean
 */
@Parcelize
@Entity
data class Config(
    @PrimaryKey
    @SerializedName("_id")
    val id: String,
    val alias: String = "",
    val title: String = "",
    val desc: String = "",
    val content: String = "",
) : Parcelable {
}