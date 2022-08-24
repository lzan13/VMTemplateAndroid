package com.vmloft.develop.library.data.bean

import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

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
    val createdAt: Long = 0,

    var isLike: Boolean = false, // 记录是否喜欢
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        (other as? Comment)?.let {
            return it.id == this.id
        }
        return false
    }
}