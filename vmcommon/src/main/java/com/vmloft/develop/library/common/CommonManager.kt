package com.vmloft.develop.library.common

import android.content.Context
import android.net.http.HttpResponseCache
import com.opensource.svgaplayer.SVGAParser
import com.vmloft.develop.library.tools.utils.VMFile
import java.io.File

/**
 * Create by lzan13 on 2022/7/11
 * 描述：
 */
object CommonManager {

    fun init(context: Context) {
        // SVGAParser 单例需要提前实例化
        SVGAParser.shareParser().init(context)

        // VGAParser 依赖 URLConnection, URLConnection 使用 HttpResponseCache 处理缓存。
        val cacheDir = File(VMFile.filesPath("gift"))
        HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)
    }
}