package com.vmloft.develop.library.base.common


/**
 * Create by lzan13 on 2020/8/12 22:03
 * 描述：基本常量
 */
object CConstants {

    const val cacheImageDir = "images"

    // 应用在 SDCard 创建区别其他项目目录，一般以项目名命名
    const val projectName = "VMTemplate"

    // 配置文件更新
    const val appConfigEvent = "appConfigEvent"

    // 分页默认数据
    const val defaultPage = 0
    const val defaultLimit = 20
    const val defaultLimitBig = 50

    // 时间常量
    const val timeSecond: Long = 1000
    const val timeMinute: Long = 60 * timeSecond
    const val timeHour: Long = 60 * timeMinute
    const val timeDay = 24 * timeHour // 天

    /**
     * 通知相关key
     */
    object Notify{
        const val notifyParams = "notifyParams" // 通知参数key
        const val notifyBName = "notifyBName" // 通知业务名称key

        // IM业务相关
        const val notifyBNameIM = "notifyBNameIM" // IM
        const val notifyChatId = "notifyChatId" // 会话Id
        const val notifyExtend = "notifyExtend" // 会话扩展
    }
}