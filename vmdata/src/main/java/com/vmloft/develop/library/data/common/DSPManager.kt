package com.vmloft.develop.library.data.common

import com.vmloft.develop.library.base.common.CSPManager

/**
 * Create by lzan13 on 2020/4/25 19:48
 * 描述：SharedPreference 管理
 */
object DSPManager {

    /**
     * 记录登录信息
     */
    private val signEntry = "sign"
    private val tokenKey = "tokenKey"
    private val signIdKey = "signIdKey" // 当前登录 id
    private val signUserKey = "signUserKey"
    private val historyUserKey = "historyUserKey"
    private val signMatchKey = "signMatchKey"

    private val lastRoomKey = "lastRoomKey" // 记录最后加入的房间信息

    /**
     * 记录时间
     */
    private val timeEntry = "time"
    private val appConfigTimeKey = "appConfigTimeKey" // 上次请求客户端配置时间
    private val checkVersionTimeKey = "checkVersionTimeKey" // 上次版本检查时间
    private val categoryTimeKey = "categoryTimeKey" // 上次获取分类时间
    private val giftTimeKey = "giftTimeKey" // 上次获取职业时间
    private val privatePolicyTimeKey = "privatePolicyTimeKey" // 上次请求隐私政策时间
    private val professionTimeKey = "professionTimeKey" // 上次获取职业时间
    private val userAgreementTimeKey = "userAgreementTimeKey" // 上次请求用户协议时间
    private val userNormTimeKey = "userNormTimeKey" // 上次请求用户行为规范时间

    private val publishPostTimeKey = "userNormTimeKey" // 上次发布内容时间

    /**
     * -------------------------------------------------------------------------------
     * 记录登录信息
     */

    /**
     * token 需要实时更新
     */
    fun getToken(): String = CSPManager.get(signEntry, tokenKey, "") as String
    fun putToken(token: String) {
        CSPManager.put(signEntry, tokenKey, token)
    }

    /**
     * 当前登录账户 Id
     */
    fun getSignId(): String = CSPManager.get(signEntry, signIdKey, "") as String
    fun putSignId(userId: String) {
        CSPManager.put(signEntry, signIdKey, userId)
    }

    /**
     * 当前账户登录记录
     *
     * @return 如果为空，说明没有登录记录
     */
    fun getSignUser(): String = CSPManager.get(signEntry, signUserKey, "") as String
    fun putSignUser(userJson: String) {
        CSPManager.put(signEntry, signUserKey, userJson)
    }

    /**
     * 上一个账户登录记录
     *
     * @return 如果为空，说明没有登录记录
     */
    fun getHistoryUser(): String = CSPManager.get(signEntry, historyUserKey, "") as String
    fun putHistoryUser(userJson: String) {
        CSPManager.putAsync(signEntry, historyUserKey, userJson)
    }

    /**
     * 当前账户匹配信息
     *
     * @return 如果为空，说明没有登录记录
     */
    fun getSelfMatch(): String = CSPManager.get(signEntry, signMatchKey, "") as String
    fun putSelfMatch(json: String) {
        CSPManager.put(signEntry, signMatchKey, json)
    }

    /**
     * 最后加入的房间记录
     */
    fun getLastRoom(): String = CSPManager.get(signEntry, lastRoomKey, "") as String
    fun putLastRoom(json: String) {
        CSPManager.put(signEntry, lastRoomKey, json)
    }

    /**
     * -------------------------------------------------------------------------------
     * 记录时间信息
     */

    /**
     * 最近一次请求隐私政策时间
     */
    fun getAppConfigTime(): Long = CSPManager.get(timeEntry, appConfigTimeKey, 0L) as Long
    fun setAppConfigTime(time: Long) {
        CSPManager.putAsync(timeEntry, appConfigTimeKey, time)
    }

    /**
     * 最近一次版本检查时间
     */
    fun getCheckVersionTime(): Long = CSPManager.get(timeEntry, checkVersionTimeKey, 0L) as Long
    fun setCheckVersionTime(time: Long) {
        CSPManager.putAsync(timeEntry, checkVersionTimeKey, time)
    }

    /**
     * 最近一次分类获取缓存时间
     */
    fun getCategoryTime(): Long = CSPManager.get(timeEntry, categoryTimeKey, 0L) as Long
    fun setCategoryTime(time: Long) {
        CSPManager.putAsync(timeEntry, categoryTimeKey, time)
    }

    /**
     * 最近一次礼物获取缓存时间
     */
    fun getGiftTime(): Long = CSPManager.get(timeEntry, giftTimeKey, 0L) as Long
    fun setGiftTime(time: Long) {
        CSPManager.putAsync(timeEntry, giftTimeKey, time)
    }

    /**
     * 最近一次请求隐私政策时间
     */
    fun getPrivatePolicyTime(): Long = CSPManager.get(timeEntry, privatePolicyTimeKey, 0L) as Long
    fun setPrivatePolicyTime(time: Long) {
        CSPManager.putAsync(timeEntry, privatePolicyTimeKey, time)
    }

    /**
     * 最近一次职业获取缓存时间
     */
    fun getProfessionTime(): Long = CSPManager.get(timeEntry, professionTimeKey, 0L) as Long
    fun setProfessionTime(time: Long) {
        CSPManager.putAsync(timeEntry, professionTimeKey, time)
    }

    /**
     * 最近一次请求用户协议时间
     */
    fun getUserAgreementTime(): Long = CSPManager.get(timeEntry, userAgreementTimeKey, 0L) as Long
    fun setUserAgreementTime(time: Long) {
        CSPManager.putAsync(timeEntry, userAgreementTimeKey, time)
    }

    /**
     * 最近一次请求用户行为规范时间
     */
    fun getUserNormTime(): Long = CSPManager.get(timeEntry, userNormTimeKey, 0L) as Long
    fun setUserNormTime(time: Long) {
        CSPManager.putAsync(timeEntry, userNormTimeKey, time)
    }

    /**
     * 最近一次发布内容时间
     */
    fun getPublishPostTime(): Long = CSPManager.get(timeEntry, publishPostTimeKey, 0L) as Long
    fun setPublishPostTime(time: Long) {
        CSPManager.putAsync(timeEntry, publishPostTimeKey, time)
    }
}