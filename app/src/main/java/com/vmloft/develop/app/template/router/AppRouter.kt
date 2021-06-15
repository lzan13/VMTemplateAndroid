package com.vmloft.develop.app.template.router

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

    const val appMain = "/App/Main"
    const val appGuide = "/App/Guide"

    // 注册登录
    const val appSignGuide = "/App/Sign"
    const val appSignIn = "/App/SignIn"
    const val appSignUp = "/App/SignUp"

    // 设置
    const val appSettings = "/App/Settings"
    const val appSettingsDark = "/App/SettingsDark"
    const val appSettingsMedia = "/App/SettingsMedia"
    const val appSettingsNotify = "/App/SettingsNotify"

    const val appSettingsAbout = "/App/SettingsAbout"
    const val appFeedback = "/App/Feedback"

    // 二维码
    const val appQRScan = "/App/QRScan"
    const val appQRMine = "/App/QRMine"

    // 个人资料编辑
    const val appPersonalInfo = "/App/PersonalInfo"
    const val appPersonalAuth = "/App/PersonalAuth"
    const val appEditUsername = "/App/EditUsername"
    const val appEditNickname = "/App/EditNickname"
    const val appEditSignature = "/App/EditSignature"


    // 内容
    const val appPublish = "/App/PublishPost"
    const val appPostDetails = "/App/PostDetail"

    // 匹配
    const val appMatch = "/App/Match"
    const val appMatchFast = "/App/MatchFast"

    // 用户信息
    const val appUserInfo = "/App/UserInfo"

    // 房间信息
    const val appRoomList = "/App/RoomList"
    const val appRoomCreate = "/App/RoomCreate"


    /**
     * 用户信息
     */
    fun goUserInfo(user: User) {
        ARouter.getInstance().build(appUserInfo).withParcelable("user", user).navigation()
    }

    /**
     * 帖子详情
     */
    fun goPostDetail(post: Post) {
        ARouter.getInstance().build(appPostDetails).withParcelable("post", post).navigation()
    }

    /**
     * 个人信息昵称设置[EditNicknameActivity]
     */
    fun goEditNickname(nickname: String?) {
        ARouter.getInstance().build(appEditNickname).withString("nickname", nickname).navigation()
    }

    /**
     * 个人信息签名设置[EditSignatureActivity]
     */
    fun goEditSignature(signature: String?) {
        ARouter.getInstance().build(appEditSignature).withString("signature", signature).navigation()
    }

}