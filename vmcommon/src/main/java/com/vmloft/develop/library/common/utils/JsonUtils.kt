package com.vmloft.develop.library.common.utils

import android.text.TextUtils
import com.google.gson.Gson

/**
 * Create by lzan13 on 2020/7/30 17:19
 * 描述：Json 工具类
 */
object JsonUtils {
    /**
     * 将 json 转为对象
     */
    fun <T> formJson(str: String, clazz: Class<T>): T? {
        if (TextUtils.isEmpty(str)) {
            return null
        }
        return Gson().fromJson(str, clazz)
    }

    /**
     * 将对象转为 json
     */
    fun <T> toJson(t: T?, clazz: Class<T>): String {
        if (t == null) {
            return ""
        }
        return Gson().toJson(t, clazz)
    }

    /**
     * 将 map 集合转为 json 字符串
     */
    fun map2json(map: Map<String, Any>): String {
        return Gson().toJson(map)
    }
}