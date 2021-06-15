package com.vmloft.develop.app.template.request.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Create by lzan13 on 2020/7/30 15:56
 * 描述：用户对象数据 bean
 */
@Parcelize
data class User(
    @SerializedName("_id")
    var id: String = "",
    var devicesId: String = "",
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var emailVerify: Boolean = false,
    var phone: String = "",
    var phoneVerify: Boolean = false,
    var avatar: String = "",
    var cover: String = "",
    var birthday: String = "",
    var gender: Int = 2,
    var nickname: String = "",
    var signature: String = "",
    var address: String = "",
    var hobby: String = "",
    var score: Int = 0,
    var clockContinuousCount: Int = 0,
    var clockTotalCount: Int = 0,
    var clockTime: String = "",
    var fansCount: Int = 0,
    var followCount: Int = 0,
    var likeCount: Int = 0,
    var postCount: Int = 0,
    var relation: Int = -1,
    var profession: Profession? = null,
    var role: Role? = null,
    var token: String = "",
    var idCardNumber: String = "",
    var realName: String = "",
    var deleted: Int = 0,
    var deletedReason: String = "",
    var deletedAt: String = "",
    var createdAt: String = "",
    var updatedAt: String = "",
) : Parcelable {
    /**
     * _id
     * devicesId
     * username
     * email
     * password
     * emailVerify
     * phone
     * phoneVerify
     * password
     * avatar
     * cover
     * birthday
     * gender 性别：0 女，1 男，2 神秘
     * nickname
     * signature
     * hobby
     * address
     * clockContinuousCount
     * clockTotalCount
     * clockTime
     * fansCount
     * followCount
     * likeCount
     * postCount
     * relation
     * profession
     * role
     * token
     * idCardNumber
     * realName
     * deleted
     * deletedReason
     * deletedAt
     * createdAt
     * updatedAt
     */
}