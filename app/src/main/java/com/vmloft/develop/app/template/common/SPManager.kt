package com.vmloft.develop.app.template.common

import com.vmloft.develop.library.tools.utils.VMSPUtil
import com.vmloft.develop.library.tools.utils.VMSystem

/**
 * Create by lzan13 on 2020/4/25 19:48
 * 描述：SharedPreference 管理
 */
object SPManager {
    /**
     * 记录设置项
     */
    private val settingsEntry = "settings"

    // 本地版本
    private val localVersionKey = "localVersionKey"

    // 隐私协议状态
    private val agreementPolicyKey = "policyStatusKey"

    // 深色模式
    private val darkModeSystemSwitchKey = "darkModeSystemSwitchKey"
    private val darkModeManualKey = "darkModeManualKey"

    /**
     * 记录登录信息
     */
    private val signEntry = "sign"
    private val tokenKey = "tokenKey"
    private val currUserKey = "currUserKey"
    private val prevUserKey = "prevUserKey"

    /**
     * 记录最后加入的房间信息
     */
    private val lastRoomKey = "lastRoomKey"

    /**
     * 记录时间
     */
    private val timeEntry = "time"
    private val categoryTimeKey = "categoryTimeKey" // 上次获取分类时间
    private val professionTimeKey = "categoryTimeKey" // 上次获取职业时间
    private val checkVersionTimeKey = "checkVersionTimeKey" // 上次版本检查时间
    private val clientConfigTimeKey = "clientConfigTimeKey" // 上次请求客户端配置时间
    private val privacyPolicyTimeKey = "privacyPolicyTimeKey" // 上次请求隐私政策时间
    private val userAgreementTimeKey = "userAgreementTimeKey" // 上次请求用户协议时间


    /**
     * -------------------------------------------------------------------------------
     * 通用的几个方法
     */
    /**
     * 通用获取数据
     */
    fun get(entry: String, key: String, default: Any): Any? = VMSPUtil.getEntry(entry).get(key, default)

    /**
     * 通用设置数据
     */
    fun put(entry: String, key: String, value: String) {
        VMSPUtil.getEntry(entry).put(key, value)
    }

    /**
     * 通用设置数据，异步
     */
    fun putAsync(entry: String, key: String, value: Any) {
        VMSPUtil.getEntry(entry).putAsync(key, value)
    }

    /**
     * -------------------------------------------------------------------------------
     * 记录设置项
     */

    /**
     * 获取当前运行的版本号
     */
    fun getLocalVersion(): Long = get(settingsEntry, localVersionKey, 0L) as Long
    fun setLocalVersion(version: Long) {
        putAsync(settingsEntry, localVersionKey, version)
    }


    /**
     * 判断启动时是否需要展示引导界面，这里根据本地记录的 appVersion 以及运行 APP 获取到的 appVersion 对比
     */
    fun isGuideShow(): Boolean {
        // 上次运行保存的版本号
        val localVersion = getLocalVersion()
        // 程序当前版本
        val version = VMSystem.versionCode
        return version > localVersion
    }

    /**
     * 隐藏引导界面
     */
    fun setGuideHide() {
        setLocalVersion(VMSystem.versionCode)
    }

    /**
     * 协议与政策状态
     */
    fun isAgreementPolicy(): Boolean = get(settingsEntry, agreementPolicyKey, false) as Boolean
    fun setAgreementPolicy() {
        putAsync(settingsEntry, agreementPolicyKey, true)
    }


    /**
     * 深色模式
     */
    fun isDarkModeSystemSwitch(): Boolean = get(settingsEntry, darkModeSystemSwitchKey, true) as Boolean
    fun setDarkModeSystemSwitch(status: Boolean) {
        putAsync(settingsEntry, darkModeSystemSwitchKey, status)
    }

    fun getDarkModeManual(): Int = get(settingsEntry, darkModeManualKey, -1) as Int
    fun setDarkModeManual(mode: Int) {
        putAsync(settingsEntry, darkModeManualKey, mode)
    }


    /**
     * -------------------------------------------------------------------------------
     * 记录登录信息
     */

    /**
     * token 需要实时更新
     */
    fun getToken(): String = get(signEntry, tokenKey, "") as String
    fun putToken(token: String) {
        put(signEntry, tokenKey, token)
    }

    /**
     * 当前账户登录记录
     *
     * @return 如果为空，说明没有登录记录
     */
    fun getCurrUser(): String = get(signEntry, currUserKey, "") as String
    fun putCurrUser(userJson: String) {
        put(signEntry, currUserKey, userJson)
    }

    /**
     * 上一个账户登录记录
     *
     * @return 如果为空，说明没有登录记录
     */
    fun getPrevUser(): String = get(signEntry, prevUserKey, "") as String
    fun putPrevUser(userJson: String) {
        putAsync(signEntry, prevUserKey, userJson)
    }

    /**
     * 最后加入的房间记录
     */
    fun getLastRoom(): String = get(signEntry, lastRoomKey, "") as String
    fun putLastRoom(json: String) {
        put(signEntry, lastRoomKey, json)
    }

    /**
     * -------------------------------------------------------------------------------
     * 记录时间信息
     */

    /**
     * 获取最近一次分类获取缓存时间
     */
    fun getCategoryTime(): Long = get(timeEntry, categoryTimeKey, 0L) as Long
    fun setCategoryTime(time: Long) {
        putAsync(timeEntry, categoryTimeKey, time)
    }

    /**
     * 获取最近一次职业获取缓存时间
     */
    fun getProfessionTime(): Long = get(timeEntry, professionTimeKey, 0L) as Long
    fun setProfessionTime(time: Long) {
        putAsync(timeEntry, professionTimeKey, time)
    }

    /**
     * 获取最近一次版本检查时间
     */
    fun getCheckVersionTime(): Long = get(timeEntry, checkVersionTimeKey, 0L) as Long
    fun setCheckVersionTime(time: Long) {
        putAsync(timeEntry, checkVersionTimeKey, time)
    }

    /**
     * 获取最近一次请求隐私政策时间
     */
    fun getClientConfigTime(): Long = get(timeEntry, clientConfigTimeKey, 0L) as Long
    fun setClientConfigTime(time: Long) {
        putAsync(timeEntry, clientConfigTimeKey, time)
    }

    /**
     * 获取最近一次请求隐私政策时间
     */
    fun getPrivacyPolicyTime(): Long = get(timeEntry, privacyPolicyTimeKey, 0L) as Long
    fun setPrivacyPolicyTime(time: Long) {
        putAsync(timeEntry, privacyPolicyTimeKey, time)
    }

    /**
     * 获取最近一次请求用户协议时间
     */
    fun getUserAgreementTime(): Long = get(timeEntry, userAgreementTimeKey, 0L) as Long
    fun setUserAgreementTime(time: Long) {
        putAsync(timeEntry, userAgreementTimeKey, time)
    }

}