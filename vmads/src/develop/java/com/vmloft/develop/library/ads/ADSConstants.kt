package com.vmloft.develop.library.ads

/**
 * Create by lzan13 on 2022/3/9
 * 描述：广告模块静态资源类
 */
object ADSConstants {

    /**
     * 广告相关 Id
     */
    object ADSIds {
        const val adsSplashId = "b625832273c997"
        const val adsNativeId = "b625836c465ce1"
        const val adsVideoId = "b62583b7c12bdb"

        // 广告场景
        const val videoSceneScoreId = "f6261304415e10"
        const val videoSceneItemId = "f62a0ccd923fae"
    }

    /**
     * 广告状态
     */
    object Status {
        const val loaded = 0x00 // 已加载，可以展示

        const val timeout = 0x01 // 加载超时，这时候还在继续加载，最后会回调加载完成
        const val failed = 0x02  // 加载失败，没有广告或其他原因
        const val show = 0x04  // 广告展示回调
        const val click = 0x05  // 广告点击回调
        const val close = 0x06  // 广告关闭回调

        const val reward = 0x10 // 激励视频广告奖励
        const val playStart = 0x11 // 激励视频广告播放开始
        const val playEnd = 0x12 // 激励视频广告播放结束
        const val playFailed = 0x13 // 激励视频广告播放失败
    }

    /**
     * 获取 TopOn AppId
     */
    fun topOnAppId(): String {
        return BuildConfig.topOnAppId
    }

    /**
     * 获取 TopOn AppKey
     */
    fun topOnAppKey(): String {
        return BuildConfig.topOnAppKey
    }

    /**
     * 获取签名 secKey
     */
    fun secKey(): String {
        return BuildConfig.adsSecKey
    }


}