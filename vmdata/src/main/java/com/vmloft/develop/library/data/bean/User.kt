package com.vmloft.develop.library.data.bean

import android.os.Parcelable

import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2020/7/30 15:56
 * 描述：用户对象数据 bean
 */
@Parcelize
data class User(
    @SerializedName("_id")
    var id: String = "",
    var devicesId: String = "", // 设备Id
    var username: String = "", // 用户名
    var email: String = "", // 邮箱
    var password: String = "", // 密码
    var emailVerify: Boolean = false,
    var phone: String = "", // 手机号
    var phoneVerify: Boolean = false,
    var avatar: String = "", // 头像
    var cover: String = "", // 封面
    var birthday: String = "", // 生日
    var gender: Int = 2, // 性别：0 仙子，1 仙君，2 神秘
    var nickname: String = "", // 昵称
    var signature: String = "", // 签名
    var address: String = "", // 地址
    var hobby: String = "", // 爱好

    var score: Int = 0, // 积分
    var charm: Float = 0f, // 魅力
    var clockContinuousCount: Int = 0, // 连续签到次数
    var clockTotalCount: Int = 0, // 总签到次数
    var clockTime: String = "", // 签到时间
    var fansCount: Int = 0, // 粉丝数
    var followCount: Int = 0, // 关注数
    var likeCount: Int = 0, // 喜欢数
    var matchCount: Int = 30, // 可用匹配次数
    var fastCount: Int = 30, // 可用快速聊天次数
    var postCount: Int = 0, // 帖子数

    var strangerMsg: Boolean = true,//陌生人私信

    var relation: Int = -1, // 当前用户与他人关系
    var blacklist: Int = -1, // 当前用户与他人黑名单关系
    var profession: Profession = Profession(), // 职业
    var role: Role = Role(), // 身份
    var roleDate: String = "", // 身份过期时间，主要是判断会员到期
    var token: String = "",
    var idCardNumber: String = "",
    var realName: String = "",
    var deleted: Int = 0,
    var deletedReason: String = "",
    var deletedAt: String = "",
    var createdAt: String = "",
    var updatedAt: String = "",
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        (other as? User)?.let {
            return it.id == this.id
        }
        return false
    }
}