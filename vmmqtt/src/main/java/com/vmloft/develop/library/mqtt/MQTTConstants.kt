package com.vmloft.develop.library.mqtt


/**
 * Create by lzan13 on 2022/3/9
 * 描述：
 */
object MQTTConstants {

    /**
     * 聊天类型
     */
    object Event {
        const val connectStatus = "mqttConnectStatus" // 链接状态
        const val msgNotify = "mqttMsgNotify" // 消息通知
    }

    /**
     * 主题类型
     */
    object Topic {
        const val newMatchInfo = "newMatchInfo" // 新的匹配信息
    }

    /**
     * 获取MQTT AppId
     */
    fun mqttAppId(): String {
        return BuildConfig.mqttAppId
    }

    /**
     * 获取MQTT host 地址
     */
    fun mqttHost(): String {
        return BuildConfig.mqttHost
    }

    /**
     * 获取MQTT port 端口
     */
    fun mqttPort(): String {
        return BuildConfig.mqttPort
    }


}