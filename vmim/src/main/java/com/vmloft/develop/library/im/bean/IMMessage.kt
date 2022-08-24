package com.vmloft.develop.library.im.bean

import androidx.room.Entity
import com.vmloft.develop.library.data.bean.Attachment
import org.json.JSONArray
import org.json.JSONObject

/**
 * Create by lzan13 on 2022/8/10 22:11
 * 描述：普通消息 bean
 */
@Entity(tableName = "message", primaryKeys = ["localId"])
data class IMMessage(
    var status: Int = 0, // 状态 0-发送中 1-发送成功 2-发送失败
    var type: Int = 0, // 类型 0-文本 1-系统 2-卡片 3-图片 4-语音 5-视频 6-礼物 7-表情
    var body: String = "", // 内容
    var attachments: MutableList<Attachment> = mutableListOf(),// 附件

    var resendCount: Int = 0, // 消息重发次数
    var isLocal: Boolean = false, // 本地消息
) : IMBMsg() {

    /**
     * 转为字符串
     */
    override fun toString(): String {
        val jsonObj = JSONObject()
        // 添加消息id
        if (id.isNotEmpty()) jsonObj.put("id", id)
        // 添加消息发送方
        if (from.isNotEmpty()) jsonObj.put("from", from)
        // 添加消息接收方
        if (to.isNotEmpty()) jsonObj.put("to", to)
        // 添加聊天类型
        jsonObj.put("chatType", chatType)
        // 添加消息状态
//        jsonObj.put("status", status)
        // 添加消息类型
        jsonObj.put("type", type)
        // 添加 body
        if (body.isNotEmpty()) jsonObj.put("body", body)

        // 转换添加附件，因为附件存储比较特殊，在发送时只能传递附件id
        if (attachments.isNotEmpty()) {
            val jsonArray = JSONArray()
            attachments.forEach {
                jsonArray.put(it.id)
            }
            jsonObj.put("attachments", jsonArray)
        }
        // 添加扩展
        if (extend.isNotEmpty()) jsonObj.put("extend", extend)
        // 添加本地id
        if (localId.isNotEmpty()) jsonObj.put("localId", localId)
        // 添加时间戳
        if (time > 0) jsonObj.put("time", time)

        return jsonObj.toString()
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