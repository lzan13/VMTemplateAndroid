package com.vmloft.develop.app.template.router

import android.os.Parcelable
import com.alibaba.android.arouter.launcher.ARouter
import com.vmloft.develop.app.template.request.bean.Match
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.ui.main.mine.info.EditNicknameActivity
import com.vmloft.develop.app.template.ui.main.mine.info.EditSignatureActivity

/**
 * Create by lzan13 on 2020-02-24 21:57
 * 描述：针对路由注解统一收口
 */
object AppRouter {

    const val appGuide = "/App/Guide"

    // 注册登录
    const val appSignGuide = "/App/Sign"
    const val appSignIn = "/App/SignIn"
    const val appSignUp = "/App/SignUp"

    // 设置
    const val appSettings = "/App/Settings"
    const val appSettingsAccountSecurity = "/App/SettingsAccountSecurity"
    const val appSettingsDark = "/App/SettingsDark"
    const val appSettingsMedia = "/App/SettingsMedia"
    const val appSettingsNotify = "/App/SettingsNotify"

    const val appSettingsAbout = "/App/SettingsAbout"
    const val appFeedback = "/App/Feedback"

    // 政策协议
    const val appSettingsAgreementPolicy = "/App/SettingsAgreementPolicy"

    // 二维码
    const val appQRScan = "/App/QRScan"
    const val appQRMine = "/App/QRMine"

    // 金币信息
    const val appGold = "/App/Gold"
    const val appGoldDesc = "/App/GoldDesc"

    // 订单
    const val appOrderDetail = "/App/OrderDetail"
    const val appOrderList = "/App/OrderList"

    // 个人资料
    const val appPersonalInfo = "/App/PersonalInfo"
    const val appPersonalInfoGuide = "/App/PersonalInfoGuide"
    const val appPersonalAuth = "/App/PersonalAuth"
    const val appEditUsername = "/App/EditUsername"
    const val appEditNickname = "/App/EditNickname"
    const val appEditSignature = "/App/EditSignature"

    // 绑定邮箱
    const val appBindEmail = "/App/BindEmail"


    // 内容
    const val appPostCreate = "/App/PostCreate"
    const val appPostDetails = "/App/PostDetail"
    const val appPostComment = "/App/appPostComment"

    // 用户信息
    const val appUserInfo = "/App/UserInfo"

    // 匹配
    const val appMatchAnim = "/App/MatchAnim"
    const val appMatchSecret = "/App/MatchSecret"

    // 房间信息
    const val appRoomList = "/App/RoomList"
    const val appRoomCreate = "/App/RoomCreate"


}