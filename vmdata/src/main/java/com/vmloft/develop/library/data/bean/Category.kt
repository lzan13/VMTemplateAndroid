package com.vmloft.develop.library.data.bean

import android.os.Parcelable

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize


/**
 * Create by lzan13 on 2020/7/30 16:12
 * 描述：分类数据 Bean
 */
@Entity
@Parcelize
data class Category(
    @PrimaryKey
    @SerializedName("_id")
    var id: String = "",
    var title: String = "",
    var desc: String = "",
) : Parcelable {
    @Ignore
    constructor() : this("")
}