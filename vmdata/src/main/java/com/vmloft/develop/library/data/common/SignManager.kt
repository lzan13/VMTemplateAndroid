package com.vmloft.develop.library.data.common

import com.vmloft.develop.library.data.bean.Match
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.common.utils.JsonUtils


/**
 * Create by lzan13 on 2020/6/19 14:08
 * 描述：用户管理
 */
object SignManager {

    private var mToken: String = ""

    // 当前登录账户
    private var mCurrUser: User? = null
    private var mPrevUser: User? = null

    // 自己的匹配信息
    private var selfMatch: Match? = null


    /**
     * 判断是否登录
     */
    fun isSingIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    /**
     * token 处理
     */
    fun setToken(token: String) {
        mToken = token
        DSPManager.putToken(mToken)
    }

    fun getToken(): String? {
        if (mToken.isNullOrEmpty()) {
            mToken = DSPManager.getToken()
        }
        return mToken
    }

    /**
     * 当前登录用户处理
     * 设置当前登录用户，用于登陆成功后保存用户信息
     */
    fun setCurrUser(user: User?) {
        mCurrUser = user
        mPrevUser = user
        val json: String = JsonUtils.toJson(user)
        DSPManager.putCurrUser(json)
        if (user != null) {
            DSPManager.putPrevUser(json)
        }
        user?.let {
            LDEventBus.post(DConstants.Event.userInfo, it)
        }
    }

    fun getCurrUser(): User {
        if (mCurrUser == null) {
            var json: String = DSPManager.getCurrUser()
            mCurrUser = JsonUtils.fromJson(json, User::class.java)
        }
        return mCurrUser ?: User()
    }

    /**
     * 获取上一次登录用户
     */
    fun getPrevUser(): User? {
        if (mPrevUser == null) {
            var json: String = DSPManager.getPrevUser()
            mPrevUser = JsonUtils.fromJson(json, User::class.java)
        }
        return mPrevUser
    }

    /**
     * 设置自己的匹配信息
     */
    fun setSelfMatch(match: Match) {
        selfMatch = match
        val json: String = JsonUtils.toJson(match)
        DSPManager.putSelfMatch(json)
        LDEventBus.post(DConstants.Event.matchInfo, match)
    }

    /**
     * 获取自己的匹配信息
     */
    fun getSelfMatch(): Match {
        if (selfMatch == null) {
            var json: String = DSPManager.getSelfMatch()
            selfMatch = JsonUtils.fromJson(json, Match::class.java)
        }
        return selfMatch ?: Match("selfMatch", mCurrUser ?: User(), gender = mCurrUser?.gender ?: 2, filterGender = -1)
    }

    /**
     * 退出登录后晴空下当前用户数据
     */
    fun signOut() {
        setToken("")
        setCurrUser(null)

//        IM.signOut(false)
    }
}