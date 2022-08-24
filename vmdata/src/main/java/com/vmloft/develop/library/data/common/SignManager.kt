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

    private var token: String = ""
    private var signId: String = ""

    // 当前登录账户
    private var signUser: User? = null
    private var historyUser: User? = null

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
        this.token = token
        DSPManager.putToken(this.token)
    }

    fun getToken(): String? {
        if (token.isEmpty()) {
            token = DSPManager.getToken()
        }
        return token
    }

    /**
     * 获取当前登录用户Id
     */
    fun getSignId(): String {
        if (signId.isEmpty()) {
            signId = DSPManager.getSignId()
        }
        return signId
    }

    /**
     * 当前登录用户处理
     * 设置当前登录用户，用于登陆成功后保存用户信息
     */
    fun setSignUser(user: User?) {
        signUser = user
        historyUser = user

        signId = user?.id ?: ""

        val json: String = JsonUtils.toJson(user)
        DSPManager.putSignUser(json)
        DSPManager.putSignId(signId)
        if (user != null) {
            DSPManager.putHistoryUser(json)
        }
        user?.let {
            LDEventBus.post(DConstants.Event.userInfo, it)
        }
    }

    fun getSignUser(): User {
        if (signUser == null) {
            var json: String = DSPManager.getSignUser()
            signUser = JsonUtils.fromJson(json, User::class.java)
        }
        return signUser ?: User()
    }

    /**
     * 获取历史登录用户
     */
    fun getHistoryUser(): User? {
        if (historyUser == null) {
            var json: String = DSPManager.getHistoryUser()
            historyUser = JsonUtils.fromJson(json, User::class.java)
        }
        return historyUser
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
        return selfMatch ?: Match("selfMatch", signUser ?: User(), gender = signUser?.gender ?: 2, filterGender = -1)
    }

    /**
     * 退出登录后晴空下当前用户数据
     */
    fun signOut() {
        setToken("")
        setSignUser(null)
    }
}