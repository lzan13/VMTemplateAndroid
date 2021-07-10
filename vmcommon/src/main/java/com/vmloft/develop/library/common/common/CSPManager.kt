package com.vmloft.develop.library.common.common

import com.vmloft.develop.library.common.BuildConfig
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMSPUtil

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
    // 记录键盘高度
    private val keyboardHeightKey = "keyboardHeightKey"
    // 资源设置 key
    private val mediaAutoLoadKey = "mediaAutoLoadKey"
    private val mediaSaveDICMKey = "mediaSaveDICMKey"

    // 通知开关
    private val notifyMsgSwitchKey = "notifyMsgSwitchKey"
    private val notifyMsgDetailSwitchKey = "notifyMsgDetailSwitchKey"

    /**
     * Debug 状态
     */
    fun setDebug(debug: Boolean) {
        VMSPUtil.getEntry(settingsEntry).putAsync(debugKey, debug)
    }

    fun isDebug(): Boolean = VMSPUtil.getEntry(settingsEntry).get(debugKey, BuildConfig.DEBUG) as Boolean

    /**
     * 保存键盘高度
     */
    fun putKeyboardHeight(height: Int) {
        VMSPUtil.getEntry(settingsEntry).putAsync(keyboardHeightKey, height)
    }

    fun getKeyboardHeight(): Int =
        VMSPUtil.getEntry(settingsEntry).get(keyboardHeightKey, VMDimen.dp2px(256)) as Int

    /**
     * 资源相关配置
     */
    fun setAutoLoad(auto: Boolean) {
        VMSPUtil.getEntry(settingsEntry).putAsync(mediaAutoLoadKey, auto)
    }

    fun isAutoLoad(): Boolean = VMSPUtil.getEntry(settingsEntry).get(mediaAutoLoadKey, true) as Boolean

    fun setSaveDICM(auto: Boolean) {
        VMSPUtil.getEntry(settingsEntry).putAsync(mediaSaveDICMKey, auto)
    }

    fun isSaveDICM(): Boolean = VMSPUtil.getEntry(settingsEntry).get(mediaSaveDICMKey, true) as Boolean

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
}