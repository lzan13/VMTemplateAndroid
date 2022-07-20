package com.vmloft.develop.library.data.api

import android.os.Build

import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.request.BaseRequest
import com.vmloft.develop.library.request.RConstants
import com.vmloft.develop.library.tools.utils.VMDate
import com.vmloft.develop.library.tools.utils.VMFile
import com.vmloft.develop.library.tools.utils.VMSystem

import okhttp3.Cache
import okhttp3.OkHttpClient

import java.io.File

/**
 * Create by lzan13 on 2020/05/02 15:35
 * 描述：API 请求类
 */
object APIRequest : BaseRequest() {

    val appletAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.AppletAPI::class.java, RConstants.baseHost()) }
    val blacklistAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.BlacklistAPI::class.java, RConstants.baseHost()) }
    val commonAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.CommonAPI::class.java, RConstants.baseHost()) }
    val giftAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.GiftAPI::class.java, RConstants.baseHost()) }
    val likeAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.LikeAPI::class.java, RConstants.baseHost()) }
    val matchAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.MatchAPI::class.java, RConstants.baseHost()) }
    val postAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.PostAPI::class.java, RConstants.baseHost()) }
    val relationAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.RelationAPI::class.java, RConstants.baseHost()) }
    val roomAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.RoomAPI::class.java, RConstants.baseHost()) }
    val signAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.SignAPI::class.java, RConstants.baseHost()) }
    val tradeAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.TradeAPI::class.java, RConstants.baseHost()) }
    val userInfoAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(com.vmloft.develop.library.data.api.UserInfoAPI::class.java, RConstants.baseHost()) }

    override fun handleBuilder(builder: OkHttpClient.Builder) {

        val cacheSize = 30 * 1024 * 1024L
        val cacheDirectory = File(VMFile.cachePath, "responses")
        val cache = Cache(cacheDirectory, cacheSize)
        // 添加公共 Header
        builder.addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()

            builder.header("Content-Type", "application/json");
            // 设备唯一信息
            val brand = Build.BRAND + Build.VERSION.INCREMENTAL
            // 设备标识，最好能标识唯一设备
            builder.addHeader("deviceInfo", brand)
            // 设置软件
            builder.addHeader("version", VMSystem.versionName ?: "1.0.0")
            // 当前时间戳
            builder.addHeader("timestamp", VMDate.currentMilli().toString())

            val token = SignManager.getToken()
            if (!token.isNullOrEmpty()) {
                // 在请求头添加 Token
                builder.header("Authorization", "Bearer $token")
            }

            builder.method(original.method, original.body)

            return@addInterceptor chain.proceed(builder.build())
        }
        // 缓存配置
        builder.cache(cache).addInterceptor { chain ->
            var request = chain.request()
            val response = chain.proceed(request)

            val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
            response
        }
    }
}