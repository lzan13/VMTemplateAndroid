package com.vmloft.develop.library.data.bean

import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2021/11/22
 * 描述：反馈数据 bean
 */
@Parcelize
data class Feedback(
    @SerializedName("_id")
    var id: String = "",
    var owner: User = User(), // type: mongoose.Schema.Types.ObjectId, ref: 'User' }, // 反馈用户，可能为空
    var contact: String = "", // type: String }, // 联系方式，可以使手机号，可以是邮箱
    var content: String = "", // type: String, required: true }, // 反馈内容
    var remark: String = "", // type: String }, // 备注信息，可填写处理之后的说明
    var user: User = User(), // type: mongoose.Schema.Types.ObjectId, ref: 'User' }, // 相关用户
    var post: Post = Post(), // type: mongoose.Schema.Types.ObjectId, ref: 'Post' }, // 相关帖子
    var status: Int = 0, // 处理状态 0-待处理 1-处理中 2-处理完成
    var attachments: MutableList<Attachment> = mutableListOf(), // 附件信息
    var type: Int = 0, // 反馈类型 0-意见建议 1-广告 2-政治敏感 3-色情低俗 4-血腥暴力 5-不文明 6-涉嫌诈骗 7-其他
) : Parcelable {
}
