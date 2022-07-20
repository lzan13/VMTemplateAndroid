package com.vmloft.develop.library.im.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Create by lzan13 on 2019/5/23 09:50
 *
 * 描述：定义 IM 内部联系人实体类，用来获取头像昵称等简单属性进行展示
 */
@Parcelize
data class IMUser(
    var id: String, // 账户 id
    var username: String = "", // 用户名
    var nickname: String = "", // 昵称
    var avatar: String = "", // 头像
    var gender: Int = 2, // 性别
    var identity: Int = 9, // 用户 身份
    var signature: String = "", // 签名
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        (other as? IMUser)?.let {
            return it.id == this.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}