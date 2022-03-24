package com.vmloft.develop.app.template.router

import android.content.Context

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.service.SerializationService

import com.vmloft.develop.library.common.utils.JsonUtils

import java.lang.reflect.Type

/**
 * Create by lzan13 on 2020/4/12 13:16
 * 描述：自定义序列化实现
 */
@Route(path = "/App/JsonServiceImpl")
class JSONServiceImpl : SerializationService {
    override fun <T : Any?> json2Object(input: String, clazz: Class<T>): T {
        return JsonUtils.fromJson(input,clazz)!!
    }

    override fun init(context: Context) {    }

    override fun object2Json(instance: Any): String {
        return JsonUtils.toJson(instance)
    }

    override fun <T : Any?> parseObject(input: String, clazz: Type): T {
        return JsonUtils.fromJson(input, clazz)!!
    }
}