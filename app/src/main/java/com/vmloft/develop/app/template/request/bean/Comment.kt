package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:18
 * 描述：评论数据 Bean
 */
@Parcelize
data class Comment(
    @SerializedName("_id")
    var id: String = "",
    var owner: User = User(),
    var user: User = User(),
    var post: Post = Post(),
    var content: String = "",
    var likeCount: Int = 0,
    var createdAt: String = "",
) : Parcelable {
}