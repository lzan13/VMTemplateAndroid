package com.vmloft.develop.library.data.common

/**
 * Create by lzan13 on 2022/7/6
 * 描述：数据模块常量类
 */
object DConstants {
    const val dbName = "wcdb_vmtemplate"
    const val dbPass = "wcdb_vmtemplate_lzan13"

    object Event{
        const val userInfo = "userInfo" // 用户信息改变事件
        const val matchInfo = "matchInfo" // 匹配信息改变事件
        const val giftGive = "giftGive" // 礼物赠送事件
    }
}