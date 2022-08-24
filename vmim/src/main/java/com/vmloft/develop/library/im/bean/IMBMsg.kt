package com.vmloft.develop.library.im.bean

import android.os.Parcelable
import androidx.room.Ignore
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.im.common.IMConstants

import kotlinx.parcelize.Parcelize

import org.json.JSONArray
import org.json.JSONObject

/**
 * Create by lzan13 on 2022/8/10 22:11
 * 描述：信令消息 bean
 */
@Parcelize
open class IMBMsg(
    @SerializedName("_id")
    var id: String = "", // 消息Id
    var from: String = "", // 发送者
    var to: String = "", // 接受者
    var chatType: Int = 0, // 消息聊天类型 0-单聊 1-群聊 2-聊天室
    var extend: String = "", // 扩展
    var localId: String = "", // 前端生成的消息Id，由 时间戳+随机数 组成
    var time: Long = 0, // 时间戳

) : Parcelable {

    /**
     * 消息所属会话Id
     */
    var chatId: String = ""
        get() {
            return if (SignManager.getSignId() == from) {
                to
            } else {
                if (chatType == IMConstants.ChatType.imSingle) {
                    from
                } else {
                    to
                }
            }
        }

    /**
     * 判断是否为发送消息
     */
    @Ignore
    var isSend: Boolean = false
        get() = from == SignManager.getSignId()

    /**
     * 设置扩展属性
     *
     * @param key   属性名
     * @param value 值
     */
    fun setAttribute(key: String, value: Any) {
        val jsonObj = getExtendObj()
        jsonObj.put(key, value)
        extend = jsonObj.toString()
    }

    /**
     * 获取 boolean 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    fun getBooleanAttribute(key: String, value: Boolean = false): Boolean {
        val jsonObj = getExtendObj()
        return jsonObj.optBoolean(key, value)
    }

    /**
     * 获取 int 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    fun getIntAttribute(key: String, value: Int = 0): Int {
        val jsonObj = getExtendObj()
        return jsonObj.optInt(key, value)
    }

    /**
     * 获取 long 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    fun getLongAttribute(key: String, value: Long = 0): Long {
        val jsonObj = getExtendObj()
        return jsonObj.optLong(key, value)
    }

    /**
     * 获取 String 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    fun getStringAttribute(key: String, value: String): String {
        val jsonObj = getExtendObj()
        return jsonObj.optString(key, value)
    }

    /**
     * 获取 JSONObject 类型扩展属性
     *
     * @param key 属性名
     */
    fun getJSONObjectAttribute(key: String): JSONObject? {
        val jsonObj = getExtendObj()
        return jsonObj.optJSONObject(key)
    }

    /**
     * 获取 JSONArray 类型扩展属性
     *
     * @param key 属性名
     */
    fun getJSONArrayAttribute(key: String): JSONArray? {
        val jsonObj = getExtendObj()
        return jsonObj.optJSONArray(key)
    }

    /**
     * 获取扩展对象
     */
    private fun getExtendObj(): JSONObject {
        return if (extend.isEmpty()) {
            JSONObject()
        } else {
            JSONObject(extend)
        }
    }

    /**
     * 当会话 id 相同就认为两个会话相同
     */
    override fun equals(other: Any?): Boolean {
        if (other is IMBMsg) {
            return id == other.id && localId == other.localId
        }
        return super.equals(other)
    }
}