package com.vmloft.develop.app.template.router

import android.content.Context

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.service.SerializationService

import com.google.gson.Gson

import java.lang.reflect.Type

/**
 * Create by lzan13 on 2020/4/12 13:16
 * 描述：自定义序列化实现
 */
@Route(path = "/App/JsonServiceImpl")
class JSONServiceImpl : SerializationService {
    lateinit var gson: Gson
    override fun <T : Any?> json2Object(input: String?, clazz: Class<T>?): T {
        return gson.fromJson(input, clazz)
    }

    override fun init(context: Context?) {
        gson = Gson()
    }

    override fun object2Json(instance: Any?): String {
        return gson.toJson(instance)
    }

    override fun <T : Any?> parseObject(input: String?, clazz: Type?): T {
        return gson.fromJson(input, clazz)
    }
}