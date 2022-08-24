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
    var id: String = "", // id
    var title: String = "", // 标题
    var content: String = "", // 内容
    var tips: String = "", // 提示
    var isNeedVIP: Boolean = false, // 需要 VIP 资格
    var type: Int = 0, // 类型 0-H5 1-小应用 2-小游戏
    var appId: String = "", // appId
    var cover: Attachment = Attachment(), // 封面
    var body: Attachment = Attachment(), // 程序体
    var url: String = "", // H5 地址
    var versionCode: Int = 1, // 版本号
    var versionName: String = "0.0.1", // 版本名
) : Parcelable {
}