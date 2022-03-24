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
 * {
 *  "chatPictureLimit": 5,
 *  "chatCallLimit":10,
 *  "chatCallEntry": true,
 *  "homeRoomEntry": true,
 *  "homeSecretEntry": false,
 *  "homeWishEntry": false,
 *  "scoreEntry": true,
 *  "vipEntry": false
 * }
 */
data class ClientConfig(
    var chatPictureLimit: Int = 5, // 聊天图片锁 限制数
    var chatCallLimit: Int = 10, // 聊天语音通话 锁限制
    var chatCallEntry: Boolean = true, // 聊天通话入口

    var homeRoomEntry: Boolean = true, // 聊天房入口
    var homeSecretEntry: Boolean = false, // 秘密入口
    var homeWishEntry: Boolean = false, // 愿望入口

    var scoreEntry: Boolean = true, // 积分相关入口
    var vipEntry: Boolean = false, // VIP充值相关入口

) {}