package com.vmloft.develop.library.request

import com.vmloft.develop.library.base.common.CSPManager

/**
 * Create by lzan13 on 2022/3/9
 * 描述：
 */
object RConstants {

    /**
     * 获取接口 host 地址，根据 debug 状态返回不同地址
     */
    fun baseHost(): String {
        return if (CSPManager.isDebug()) {
            BuildConfig.baseUrlDebug
        } else {
            BuildConfig.baseUrlRelease
        }
    }

    /**
     * 获取媒体资源 host 地址
     */
    fun mediaHost(): String {
        return BuildConfig.mediaUrl
    }
}