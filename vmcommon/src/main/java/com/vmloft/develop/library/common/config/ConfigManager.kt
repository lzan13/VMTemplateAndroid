package com.vmloft.develop.library.common.config

import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.common.utils.JsonUtils

/**
 * Create by lzan13 on 2022/3/4
 * 描述：客户端配置数据管理，需要从服务器获取，目前规则是 1 小时去服务器获取一次，其他时间都从本地获取
 */
object ConfigManager {


    private var _clientConfig: ClientConfig = ClientConfig()
    val clientConfig get() = _clientConfig

    /**
     * 装载配置信息
     */
    fun setupConfig(content: String) {
        _clientConfig = JsonUtils.fromJson(content, ClientConfig::class.java) ?: ClientConfig()
        LDEventBus.post(CConstants.clientConfigEvent)
    }
}

/**
 * 客户端配置数据 Bean，这里是通过服务器下发的配置解析出来的，有默认值
 */
data class ClientConfig(
    /*
    {
        "adsEntry": {
            "splashEntry": true,
            "exploreEntry": true,
            "goldEntry": true
        },
        "chatConfig":{
            "pictureLimit": 5,
            "callLimit":10,
            "callEntry": true
        },
        "homeFastChatEntry": true,
        "homeChatRoomEntry": true,
        "scoreEntry": true,
        "vipEntry": true
    }
     */
    var adsEntry: ADSEntry = ADSEntry(), // 广告相关入口

    var chatConfig: ChatConfig = ChatConfig(), // 聊天配置

    var homeChatFastEntry: Boolean = true, // 闪聊入口
    var homeChatRoomEntry: Boolean = true, // 聊天房入口

    var scoreEntry: Boolean = true, // 积分相关入口
    var vipEntry: Boolean = true, // VIP充值相关入口
)

/**
 * 广告部分入口
 */
data class ADSEntry(
    var splashEntry: Boolean = true, // 开屏广告入口
    var exploreEntry: Boolean = true, // 发现内容入口
    var goldEntry: Boolean = true, // 金币获取入口
)

/**
 * 聊天配置
 */
data class ChatConfig(
    var pictureLimit: Int = 5, // 聊天图片锁 限制数
    var callLimit: Int = 10, // 聊天语音通话 锁限制
    var callEntry: Boolean = true, // 聊天通话入口
)
