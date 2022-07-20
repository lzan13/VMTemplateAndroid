package com.vmloft.develop.library.data.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2020/7/30 16:08
 * 描述：帖子对象数据 Bean
 */
@Parcelize
@Entity
data class Post(
    @PrimaryKey
    @SerializedName("_id")
    var id: String = "",
    var owner: User = User(), // 发布者
    var title: String = "", // 标题
    var content: String = "", // 内容
    var stick: Int = 0, // 是否置顶 0-不置顶 1-置顶
    var category: Category = Category(), // 分类
    var attachments: MutableList<Attachment> = mutableListOf(), // 附件集合
    var commentCount: Int = 0, // 评论人数
    var likeCount: Int = 0, // 喜欢人数
    var createdAt: String = "",
    var updatedAt: String = "",

    var isLike: Boolean = false, // 记录是否喜欢

    var isShielded: Boolean = false, // 记录是否被屏蔽，这个目前在本地使用
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        (other as? Post)?.let {
            return it.id == this.id
        }
        return false
    }
}