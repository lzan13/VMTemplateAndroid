package com.vmloft.develop.library.im.common

import com.vmloft.develop.library.tools.utils.VMSPUtil

/**
 * Create by lzan13 on 2021/05/21
 *
 * 描述：SharedPreference 管理
 */
object IMSPManager {
    /**
     * 记录设置项
     */
    private val settingsEntry = "settings"
    // 当前登录自己的 id
    private val imSelfIdKey = "imSelfIdKey"

    // 聊天开关
    // 圆形头像
    private val imCircleAvatarKey = "imCircleAvatarKey"

    // 麦克风播放语音
    private val imSpeakerVoiceKey = "imSpeakerVoiceKey"


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
    fun put(entry: String, key: String, value: Any) {
        VMSPUtil.getEntry(entry).put(key, value)
    }

    /**
     * 通用设置数据，异步
     */
    fun putAsync(entry: String, key: String, value: Any) {
        VMSPUtil.getEntry(entry).putAsync(key, value)
    }

    /**
     * 保存当前登录账户 Id
     *
     * @param userId 当前账户 Id
     */
    fun putSelfId(userId: String) {
        put(settingsEntry, imSelfIdKey, userId)
    }

    /**
     * 获取当前登录账户 Id
     */
    fun getSelfId(): String {
        return get(settingsEntry, imSelfIdKey, "") as String
    }

    /**
     * ---------------------------------------------------------------------------------
     * 设置是否启用圆形头像
     */
    fun putCircleAvatar(circle: Boolean) {
        put(settingsEntry, imCircleAvatarKey, circle)
    }

    /**
     * 是否启用圆形头像
     */
    fun isCircleAvatar(): Boolean {
        return get(settingsEntry, imCircleAvatarKey, true) as Boolean
    }

    /**
     * 设置是否扬声器播放语音
     */
    fun putSpeakerVoice(speaker: Boolean) {
        put(settingsEntry, imSpeakerVoiceKey, speaker)
    }

    /**
     * 是否扬声器播放语音
     */
    fun isSpeakerVoice(): Boolean {
        return get(settingsEntry, imSpeakerVoiceKey, false) as Boolean
    }
}