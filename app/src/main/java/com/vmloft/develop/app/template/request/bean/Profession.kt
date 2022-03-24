package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:05
 * 描述：职业对象数据 Bean
 */
@Parcelize
@Entity
data class Profession(
    @PrimaryKey
    @SerializedName("_id")
    val id: String="",
    val title: String = "",
    val desc: String = ""
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        (other as? Profession)?.let {
            return it.id == this.id
        }
        return false
    }
}