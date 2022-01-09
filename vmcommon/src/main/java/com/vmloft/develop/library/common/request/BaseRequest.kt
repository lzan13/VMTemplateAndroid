package com.vmloft.develop.library.common.request


import com.vmloft.develop.library.common.BuildConfig
import com.vmloft.develop.library.common.utils.json.JsonUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

/**
 * Create by lzan13 on 2020/02/14 15:35
 * 描述：网络请求基类
 */
abstract class BaseRequest {

    companion object {
        private const val TIME_OUT = 5
    }

    private val client: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            // 日志拦截
            val logging = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                logging.level = HttpLoggingInterceptor.Level.BODY
            } else {
                logging.level = HttpLoggingInterceptor.Level.BASIC
            }
            builder.addInterceptor(logging).connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)

            handleBuilder(builder)

            return builder.build()
        }


    fun <S> getAPI(apiClass: Class<S>, baseUrl: String): S {
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(JsonUtils.gson))
            .baseUrl(baseUrl)
            .build().create(apiClass)
    }

    abstract fun handleBuilder(builder: OkHttpClient.Builder)

}
