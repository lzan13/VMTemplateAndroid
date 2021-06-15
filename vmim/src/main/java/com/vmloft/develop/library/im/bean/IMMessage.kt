package com.vmloft.develop.library.im.bean

import com.hyphenate.chat.EMMessage
import com.hyphenate.exceptions.HyphenateException

import com.vmloft.develop.library.tools.utils.VMStr.isEmpty

import org.json.JSONArray
import org.json.JSONObject

/**
 * Create by lzan13 on 2019/5/21 15:11
 *
 * 自定义 IMMessage 消息对象
 */
class IMMessage {
    // 内部消息对象
    private var mMessage: EMMessage? = null

    constructor() {}

    constructor(message: EMMessage?) {
        mMessage = message
    }

    /**
     * 获取 boolean 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    fun getBooleanAttribute(key: String?, value: Boolean): Boolean {
        return if (isEmpty(key)) {
            value
        } else mMessage!!.getBooleanAttribute(key, value)
    }

    /**
     * 获取 int 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    fun getIntAttribute(key: String?, value: Int): Int {
        return if (isEmpty(key)) {
            value
        } else mMessage!!.getIntAttribute(key, value)
    }

    /**
     * 获取 long 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    fun getLongAttribute(key: String?, value: Long): Long {
        return if (isEmpty(key)) {
            value
        } else mMessage!!.getLongAttribute(key, value)
    }

    /**
     * 获取 String 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    fun getStringAttribute(key: String?, value: String): String {
        return if (isEmpty(key)) {
            value
        } else mMessage!!.getStringAttribute(key, value)
    }

    /**
     * 获取 JSONObject 类型扩展属性
     *
     * @param key 属性名
     */
    fun getJSONObjectAttribute(key: String?): JSONObject? {
        if (isEmpty(key)) {
            return null
        }
        try {
            return mMessage!!.getJSONObjectAttribute(key)
        } catch (e: HyphenateException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取 JSONArray 类型扩展属性
     *
     * @param key 属性名
     */
    fun getJSONArrayAttribute(key: String?): JSONArray? {
        if (isEmpty(key)) {
            return null
        }
        try {
            return mMessage!!.getJSONArrayAttribute(key)
        } catch (e: HyphenateException) {
            e.printStackTrace()
        }
        return null
    }
}