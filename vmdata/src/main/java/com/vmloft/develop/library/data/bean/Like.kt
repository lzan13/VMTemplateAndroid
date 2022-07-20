package com.vmloft.develop.library.data.bean

import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:15
 * 描述：喜欢数据 Bean
 */
@Parcelize
data class Like(
    @SerializedName("_id")
    var id: String = "",
    var owner: String = "",
    var user: User = User(),
    var post: Post = Post(),
    var comment: Comment = Comment(),
    var type: Int = 0,
    var createdAt: String = "",
) : Parcelable {
}