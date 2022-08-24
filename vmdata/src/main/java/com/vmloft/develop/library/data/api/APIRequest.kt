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
import org.json.JSONObject

import java.io.File

/**
 * Create by lzan13 on 2020/05/02 15:35
 * 描述：API 请求类
 */
object APIRequest : BaseRequest() {

    val appletAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(AppletAPI::class.java, RConstants.baseHost()) }
    val blacklistAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(BlacklistAPI::class.java, RConstants.baseHost()) }
    val commonAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(CommonAPI::class.java, RConstants.baseHost()) }
    val giftAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(GiftAPI::class.java, RConstants.baseHost()) }
    val likeAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(LikeAPI::class.java, RConstants.baseHost()) }
    val matchAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(MatchAPI::class.java, RConstants.baseHost()) }
    val postAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(PostAPI::class.java, RConstants.baseHost()) }
    val relationAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(RelationAPI::class.java, RConstants.baseHost()) }
    val roomAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(RoomAPI::class.java, RConstants.baseHost()) }
    val signAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(SignAPI::class.java, RConstants.baseHost()) }
    val tradeAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(TradeAPI::class.java, RConstants.baseHost()) }
    val userInfoAPI by lazy(LazyThreadSafetyMode.NONE) { getAPI(UserInfoAPI::class.java, RConstants.baseHost()) }

    override fun handleBuilder(builder: OkHttpClient.Builder) {

        val cacheSize = 30 * 1024 * 1024L
        val cacheDirectory = File(VMFile.cachePath, "responses")
        val cache = Cache(cacheDirectory, cacheSize)
        // 添加公共 Header
        builder.addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()

            builder.header("Content-Type", "application/json");

            // 设备信息
            val devicesJson = JSONObject()
            devicesJson.put("brand", Build.BRAND)
            devicesJson.put("id", Build.ID)
            devicesJson.put("hardware", Build.HARDWARE)
            devicesJson.put("sdkCode", Build.VERSION.SDK_INT)
            devicesJson.put("sdkName", Build.VERSION.RELEASE)
            devicesJson.put("androidId", VMSystem.deviceId())
            builder.addHeader("devices", devicesJson.toString())

            // 设置软件版本
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