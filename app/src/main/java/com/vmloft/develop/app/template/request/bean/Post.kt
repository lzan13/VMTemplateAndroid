package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:08
 * 描述：帖子对象数据 Bean
 */
@Parcelize
data class Post(
    @SerializedName("_id")
    var id: String,
    var owner: User,
    var title: String? = null,
    var content: String? = null,
    var stick: Int = 0,
    var category: Category,
    var attachments: MutableList<Attachment> = mutableListOf(),
    var commentCount: Int = 0,
    var likeCount: Int = 0,
    var createdAt: String = "",
    var updatedAt: String = "",
    var isLike: Boolean = false,
) : Parcelable {
}