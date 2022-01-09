package com.vmloft.develop.library.common.utils.json

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Create by lzan13 on 2020/7/30 17:19
 * 描述：Json 工具类
 */
object JsonUtils {

    val gson: Gson = GsonBuilder()
        .registerTypeAdapterFactory(GsonAdapterFactory())
        .create()

    /**
     * 将 json 转为对象
     */
    fun <T> fromJson(str: String, clazz: Type): T? {
        if (TextUtils.isEmpty(str)) {
            return null
        }
        return gson.fromJson(str, clazz)
    }

    /**
     * 将 json 转为集合
     */
    inline fun <reified T> fromJson(json: String): T {
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }

    /**
     * 将对象转为 json
     */
    fun <T> toJson(t: T?): String {
        if (t == null) {
            return ""
        }
        return gson.toJson(t)
    }

    /**
     * 将 map 集合转为 json 字符串
     */
    fun map2json(map: Map<String, Any>): String {
        return gson.toJson(map)
    }
}