package com.vmloft.develop.library.im.bean

import org.json.JSONObject

/**
 * Create by lzan13 on 2022/8/10 22:11
 * 描述：信令消息 bean
 */
data class IMSignal(
    var action: String = "", // 指令
) : IMBMsg() {

    /**
     * 转为字符串
     */
    override fun toString(): String {
        val jsonObj = JSONObject()

        if (id.isNotEmpty()) jsonObj.put("id", id)
        if (from.isNotEmpty()) jsonObj.put("from", from)
        if (to.isNotEmpty()) jsonObj.put("to", to)
        jsonObj.put("chatType", chatType)
        if (action.isNotEmpty()) jsonObj.put("action", action)
        if (extend.isNotEmpty()) jsonObj.put("extend", extend)
        if (localId.isNotEmpty()) jsonObj.put("localId", localId)
        if (time > 0) jsonObj.put("time", time)

        return jsonObj.toString()
    }
}