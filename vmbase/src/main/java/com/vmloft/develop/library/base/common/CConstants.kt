package com.vmloft.develop.library.base.common


/**
 * Create by lzan13 on 2020/8/12 22:03
 * 描述：基本常量
 */
object CConstants {

    const val cacheImageDir = "images"

    // 应用在 SDCard 创建区别其他项目目录，一般以项目名命名
    const val projectName = "VMNepenthe"

    // 配置文件更新
    const val clientConfigEvent = "clientConfigEvent"

    // 分页默认数据
    const val defaultPage = 0
    const val defaultLimit = 50
    const val defaultLimitBig = 100

    // 时间常量
    const val timeSecond: Long = 1000
    const val timeMinute: Long = 60 * timeSecond
    const val timeHour: Long = 60 * timeMinute
    const val timeDay = 24 * timeHour // 天

}