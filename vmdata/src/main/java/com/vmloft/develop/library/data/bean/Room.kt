package com.vmloft.develop.library.data.bean

import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2021/5/25 16:08
 * 描述：房间对象数据 Bean
 */
@Parcelize
data class Room(
    @SerializedName("_id")
    var id: String = "", //
    var owner: User = User(), // 房主
    var managers: MutableList<User> = mutableListOf(), // 管理员列表
    var members: MutableList<User> = mutableListOf(), // 成员列表
    var title: String = "", // 标题
    var desc: String = "", // 描述
    var type: Int = 0, // 房间类型 0-普通聊天 1-你画我猜 2-谁是卧底
    var count: Int = 0, // 房间人数
    var maxCount: Int = 500, // 房间最大人数
    var extension: String = "", // 扩展信息
) : Parcelable {
}