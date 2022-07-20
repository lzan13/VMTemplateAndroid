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
        "adsConfig":{
            "splashEntry":true,
            "exploreEntry":true,
            "goldEntry":true
        },
        "chatConfig":{
            "voiceLimit":2,
            "pictureLimit":2,
            "callLimit":5,
            "voiceEntry":true,
            "pictureEntry":true,
            "callEntry":true
        },
        "homeConfig":{
            "randomEntry":true,
            "chatFastEntry":true,
            "gameEntry":true,
            "roomEntry":true
        },
        "tradeConfig":{
            "scoreEntry":true,
            "vipEntry":true
        }
    }
     */
    var adsConfig: ADSConfig = ADSConfig(), // 广告相关配置

    var chatConfig: ChatConfig = ChatConfig(), // 聊天相关配置

    var homeConfig: HomeConfig = HomeConfig(), // 首页相关配置

    var tradeConfig: TradeConfig = TradeConfig(), // 交易相关配置
)

/**
 * 广告部分入口
 */
data class ADSConfig(
    var splashEntry: Boolean = true, // 开屏广告入口
    var exploreEntry: Boolean = true, // 发现内容入口
    var goldEntry: Boolean = true, // 金币获取入口
)


/**
 * 聊天配置
 */
data class ChatConfig(
    var voiceLimit: Int = 5, // 聊天图片锁 限制数
    var pictureLimit: Int = 5, // 聊天图片锁 限制数
    var callLimit: Int = 10, // 聊天语音通话 锁限制

    var voiceEntry: Boolean = true, // 语音入口
    var pictureEntry: Boolean = true, // 图片入口
    var callEntry: Boolean = true, // 聊天通话入口
)

/**
 * 首页部分入口
 */
data class HomeConfig(
    var randomEntry: Boolean = true, // 随机入口
    var chatFastEntry: Boolean = true, // 闪聊入口
    var relaxationEntry: Boolean = true, // 娱乐入口
    var roomEntry: Boolean = true, // 聊天房入口
)

/**
 * 交易配置
 */
data class TradeConfig(
    var scoreEntry: Boolean = true, // 积分相关入口
    var vipEntry: Boolean = true, // VIP相关入口
)
