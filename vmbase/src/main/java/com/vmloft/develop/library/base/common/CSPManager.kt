package com.vmloft.develop.library.base.common

import com.vmloft.develop.library.base.BuildConfig
import com.vmloft.develop.library.tools.utils.VMSP

/**
 * Create by lzan13 on 2020/08/11
 *
 * 描述：SharedPreferences 配置管理类
 */
object CSPManager {
    /**
     * 记录设置项
     */
    private val settingsEntry = "settings"
    // debug 开关
    private val debugKey = "debugKey"
    // 资源设置 key
    private val mediaAutoLoadKey = "mediaAutoLoadKey"
    private val mediaSaveDICMKey = "mediaSaveDICMKey"

    // 通知开关
    private val notifyMsgSwitchKey = "notifyMsgSwitchKey"
    private val notifyMsgDetailSwitchKey = "notifyMsgDetailSwitchKey"

    // 引导前缀
    private val guideKeyPrefix = "guideKeyPrefix"

    /**
     * Debug 状态
     */
    fun setDebug(debug: Boolean) {
        VMSP.getEntry(settingsEntry).putAsync(debugKey, debug)
    }

    fun isDebug(): Boolean = VMSP.getEntry(settingsEntry).get(debugKey, BuildConfig.DEBUG) as Boolean

    /**
     * 资源相关配置
     */
    fun setAutoLoad(auto: Boolean) {
        VMSP.getEntry(settingsEntry).putAsync(mediaAutoLoadKey, auto)
    }

    fun isAutoLoad(): Boolean = VMSP.getEntry(settingsEntry).get(mediaAutoLoadKey, true) as Boolean

    fun setSaveDICM(auto: Boolean) {
        VMSP.getEntry(settingsEntry).putAsync(mediaSaveDICMKey, auto)
    }

    fun isSaveDICM(): Boolean = VMSP.getEntry(settingsEntry).get(mediaSaveDICMKey, true) as Boolean

    /**
     * 通知开关
     */
    fun setNotifyMsgSwitch(status: Boolean) {
        putAsync(settingsEntry, notifyMsgSwitchKey, status)
    }

    fun isNotifyMsgSwitch(): Boolean = get(settingsEntry, notifyMsgSwitchKey, true) as Boolean

    fun setNotifyMsgDetailSwitch(status: Boolean) {
        putAsync(settingsEntry, notifyMsgDetailSwitchKey, status)
    }

    fun isNotifyMsgDetailSwitch(): Boolean =
        get(settingsEntry, notifyMsgDetailSwitchKey, true) as Boolean

    /**
     * 检查指定模块是否需要显示引导
     */
    fun isNeedGuide(module: String): Boolean = get(settingsEntry, guideKeyPrefix + module, true) as Boolean
    fun setNeedGuide(module: String, need: Boolean) {
        putAsync(settingsEntry, guideKeyPrefix + module, need)
    }


    /**
     * -------------------------------------------------------------------------------
     * 通用的几个方法
     */
    /**
     * 通用获取数据
     */
    fun get(entry: String, key: String, default: Any): Any? {
        return VMSP.getEntry(entry).get(key, default)
    }

    /**
     * 通用设置数据
     */
    fun put(entry: String, key: String, value: Any) {
        VMSP.getEntry(entry).put(key, value)
    }

    /**
     * 通用设置数据，异步
     */
    fun putAsync(entry: String, key: String, value: Any) {
        VMSP.getEntry(entry).putAsync(key, value)
    }
}