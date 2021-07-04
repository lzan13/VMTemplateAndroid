package com.vmloft.develop.app.template.common

import com.vmloft.develop.library.tools.utils.VMSPUtil
import com.vmloft.develop.library.tools.utils.VMSystem

/**
 * Create by lzan13 on 2020/4/25 19:48
 * 描述：SharedPreference 管理
 */
class SPManager {
    /**
     * 记录设置项
     */
    private val settingsEntry = "settings"

    // 本地版本
    private val localVersionKey = "localVersionKey"

    // 隐私协议状态
    private val policyStatusKey = "policyStatusKey"

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


    companion object {
        val instance: SPManager by lazy {
            SPManager()
        }
    }
    /**
     * -------------------------------------------------------------------------------
     * 通用的几个方法
     */
    /**
     * 通用获取数据
     */
    fun get(entry: String, key: String, default: Any): Any? {
        return VMSPUtil.getEntry(entry).get(key, default)
    }

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
     * 保存当前运行版本号
     */
    fun setLocalVersion(version: Long) {
        putAsync(settingsEntry, localVersionKey, version)
    }

    /**
     * 获取当前运行的版本号
     */
    fun getLocalVersion(): Long {
        return get(settingsEntry, localVersionKey, 0L) as Long
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
        // 保存新的版本号
        setLocalVersion(VMSystem.versionCode)
    }

    /**
     * 设置隐私协议状态
     */
    fun setPolicyStatus() {
        putAsync(settingsEntry, policyStatusKey, true)
    }

    fun isPolicyStatus(): Boolean = get(settingsEntry, policyStatusKey, false) as Boolean

    /**
     * 深色模式
     */
    fun setDarkModeSystemSwitch(status: Boolean) {
        putAsync(settingsEntry, darkModeSystemSwitchKey, status)
    }

    fun isDarkModeSystemSwitch(): Boolean =
        get(settingsEntry, darkModeSystemSwitchKey, true) as Boolean

    fun setDarkModeManual(mode: Int) {
        putAsync(settingsEntry, darkModeManualKey, mode)
    }

    fun getDarkModeManual(): Int {
        return get(settingsEntry, darkModeManualKey, -1) as Int
    }

    /**
     * -------------------------------------------------------------------------------
     * 记录登录信息
     */

    /**
     * token 需要实时更新
     */
    fun getToken(): String {
        return get(signEntry, tokenKey, "") as String
    }

    fun putToken(token: String) {
        put(signEntry, tokenKey, token)
    }

    /**
     * 当前账户登录记录
     *
     * @return 如果为空，说明没有登录记录
     */
    fun getCurrUser(): String {
        return get(signEntry, currUserKey, "") as String
    }

    fun putCurrUser(userJson: String) {
        put(signEntry, currUserKey, userJson)
    }

    /**
     * 上一个账户登录记录
     *
     * @return 如果为空，说明没有登录记录
     */
    fun getPrevUser(): String {
        return get(signEntry, prevUserKey, "") as String
    }

    fun putPrevUser(userJson: String) {
        putAsync(signEntry, prevUserKey, userJson)
    }

    /**
     * 最后加入的房间记录
     */
    fun getLastRoom(): String {
        return get(signEntry, lastRoomKey, "") as String
    }

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
    fun getCategoryTime(): Long {
        return get(timeEntry, categoryTimeKey, 0L) as Long
    }

    /**
     * 设置最近一次分类获取缓存时间
     */
    fun setCategoryTime(time: Long) {
        putAsync(timeEntry, categoryTimeKey, time)
    }

    /**
     * 获取最近一次职业获取缓存时间
     */
    fun getProfessionTime(): Long {
        return get(timeEntry, professionTimeKey, 0L) as Long
    }

    /**
     * 设置最近一次职业获取缓存时间
     */
    fun setProfessionTime(time: Long) {
        putAsync(timeEntry, professionTimeKey, time)
    }


    /**
     * 获取最近一次版本检查时间
     */
    fun getCheckVersionTime(): Long {
        return get(timeEntry, checkVersionTimeKey, 0L) as Long
    }

    /**
     * 设置最近一次职业获取缓存时间
     */
    fun setCheckVersionTime(time: Long) {
        putAsync(timeEntry, checkVersionTimeKey, time)
    }
}