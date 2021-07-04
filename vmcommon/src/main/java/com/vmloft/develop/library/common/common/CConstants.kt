package com.vmloft.develop.library.common.common

import com.vmloft.develop.library.common.BuildConfig

/**
 * Create by lzan13 on 2020/8/12 22:03
 * 描述：基本常量
 */
object CConstants {

    const val cacheImageDir = "images"

    // 应用在 SDCard 创建区别其他项目目录，一般以项目名命名
    const val projectDir = "VMMatch/"

    // 分页默认数据
    const val defaultPage = 0
    const val defaultLimit = 50
    const val defaultLimitBig = 100

    // 时间常量
    const val timeSecond: Long = 1000
    const val timeMinute: Long = 60 * timeSecond
    const val timeHour: Long = 60 * timeMinute
    const val timeDay = 24 * timeHour // 天


    /**
     * 获取接口 host 地址，根据 debug 状态返回不同地址
     */
    fun baseHost(): String {
        return if (CSPManager.instance.isDebug()) {
            BuildConfig.baseDebugUrl
        } else {
            BuildConfig.baseReleaseUrl
        }
    }

    /**
     * 获取媒体资源 host 地址
     */
    fun mediaHost(): String {
        return baseHost() + "public"
    }
}