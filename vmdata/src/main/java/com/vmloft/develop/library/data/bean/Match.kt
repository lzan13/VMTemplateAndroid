package com.vmloft.develop.library.data.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:15
 * 描述：喜欢数据 Bean
 */
@Entity
@Parcelize
data class Match(
    @PrimaryKey
    @SerializedName("_id")
    var id: String = "",
    var user: User = User(),
    var content: String = "", // 匹配内容
    var emotion: Int = 0, // 心情 0-开心 1-平淡 2-难过 3-愤怒
    var gender: Int = 2, // 性别 0-女 1-男 2-神秘
    var type: Int = 0, // 类型 0-心情匹配 1-急速聊天 2-心情树洞

    var filterGender: Int = -1, // 过滤方式，这个只是自己本地过滤配置
) : Parcelable {
    @Ignore
    constructor() : this("")

    override fun equals(other: Any?): Boolean {
        (other as? Match)?.let {
            return it.id == this.id || it.user == this.user
        }
        return false
    }
}