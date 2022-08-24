package com.vmloft.develop.library.im.common

import com.vmloft.develop.library.base.common.CSPManager

/**
 * Create by lzan13 on 2021/05/21
 * 描述：SharedPreference 管理
 */
object IMSPManager {
    /**
     * 记录设置项
     */
    private val settingsEntry = "settings"

    // 聊天开关
    // 圆形头像
    private val imCircleAvatarKey = "imCircleAvatarKey"

    // 麦克风播放语音
    private val imSpeakerVoiceKey = "imSpeakerVoiceKey"

    /**
     * ---------------------------------------------------------------------------------
     * 设置是否启用圆形头像
     */
    fun putCircleAvatar(circle: Boolean) {
        CSPManager.put(settingsEntry, imCircleAvatarKey, circle)
    }

    /**
     * 是否启用圆形头像
     */
    fun isCircleAvatar(): Boolean {
        return CSPManager.get(settingsEntry, imCircleAvatarKey, true) as Boolean
    }

    /**
     * 设置是否扬声器播放语音
     */
    fun putSpeakerVoice(speaker: Boolean) {
        CSPManager.put(settingsEntry, imSpeakerVoiceKey, speaker)
    }

    /**
     * 是否扬声器播放语音
     */
    fun isSpeakerVoice(): Boolean {
        return CSPManager.get(settingsEntry, imSpeakerVoiceKey, false) as Boolean
    }
}