package com.vmloft.develop.app.template.common

import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.tools.utils.VMSP
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
     * -------------------------------------------------------------------------------
     * 记录设置项
     */

    /**
     * 获取当前运行的版本号
     */
    fun getLocalVersion(): Long = CSPManager.get(settingsEntry, localVersionKey, 0L) as Long
    fun setLocalVersion(version: Long) {
        CSPManager.putAsync(settingsEntry, localVersionKey, version)
    }


    /**
     * 判断启动时是否需要展示引导界面，这里根据本地记录的 appVersion 以及运行 APP 获取到的 appVersion 对比
     */
    fun isGuideShow(): Boolean {
        // 上次运行保存的版本号
        val localVersion = getLocalVersion()
        // 程序当前版本
        val version = VMSystem.versionCode
        return version > localVersion + 5 // 超过5个小版本之后再次显示引导界面
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
    fun isAgreementPolicy(): Boolean = CSPManager.get(settingsEntry, agreementPolicyKey, false) as Boolean
    fun setAgreementPolicy() {
        CSPManager.putAsync(settingsEntry, agreementPolicyKey, true)
    }


    /**
     * 深色模式
     */
    fun isDarkModeSystemSwitch(): Boolean = CSPManager.get(settingsEntry, darkModeSystemSwitchKey, true) as Boolean
    fun setDarkModeSystemSwitch(status: Boolean) {
        CSPManager.putAsync(settingsEntry, darkModeSystemSwitchKey, status)
    }

    fun getDarkModeManual(): Int = CSPManager.get(settingsEntry, darkModeManualKey, -1) as Int
    fun setDarkModeManual(mode: Int) {
        CSPManager.putAsync(settingsEntry, darkModeManualKey, mode)
    }


}