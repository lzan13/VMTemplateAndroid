package com.vmloft.develop.library.data.bean

import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2022/05/23 06:19
 * 描述：程序数据 Bean
 */
@Parcelize
data class Applet(
    @SerializedName("_id")
    val id: String = "", // id
    val title: String = "", // 标题
    val content: String = "", // 内容
    val tips: String = "", // 提示
    val isNeedVIP: Boolean = false, // 需要 VIP 资格
    val type: Int = 0, // 类型 0-H5 1-小应用 2-小游戏
    val appId: String = "", // appId
    val cover: Attachment = Attachment(), // 封面
    val body: Attachment = Attachment(), // 程序体
    val url: String = "", // H5 地址
    val versionCode: Int = 1, // 版本号
    val versionName: String = "0.0.1", // 版本名
) : Parcelable {
}