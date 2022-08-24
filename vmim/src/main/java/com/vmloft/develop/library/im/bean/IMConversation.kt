package com.vmloft.develop.library.im.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

import kotlinx.parcelize.Parcelize

/**
 * Create by lzan13 on 2022/8/10 22:00
 * 描述：会话 bean
 */
@Entity(tableName = "conversation")
@Parcelize
data class IMConversation(
    @PrimaryKey
    var chatId: String = "", // 会话Id
    var type: Int = 0, // 类型 0-单聊 1-群聊 2-聊天室
    var content: String = "", // 内容
    var time: Long = 0, // 时间戳
    var top: Boolean = false, // 置顶
    var receiveCount: Int = 0, // 接收数量
    var sendCount: Int = 0, // 发送数量
    var unread: Int = 0, // 未读

    var draft: String = "", // 草稿
    @Ignore
    var msgList: MutableList<IMMessage> = mutableListOf(), // 消息列表
) : Parcelable {

    /**
     * 加载更多消息
     *
     * @param msgId 开始加载的消息 Id
     * @param limit 加载条数
     */
    fun loadMoreMsg(msgId: String?, limit: Int): List<IMMessage> {
        val result = mutableListOf<IMMessage>()
//        for (message in list) {
//        }
        return result
    }

    /**
     * 添加消息
     */
    fun addMessage(message: IMMessage) {
        if (msgList.contains(message)) {
            msgList.remove(message)
        }
        msgList.add(message)
    }

    /**
     * 添加消息
     */
    fun addMessages(messages: List<IMMessage>, history: Boolean = true) {
        if (history) {
            msgList.addAll(0, messages)
        } else {
            msgList.addAll(messages)
        }
    }

    /**
     * 移除消息
     */
    fun removeMessage(message: IMMessage) {
        msgList.remove(message)
    }

    /**
     * 更新消息
     */
    fun updateMessage(message: IMMessage) {
    }

    /**
     * 清空消息
     */
    fun clearMsg() {
        msgList.clear()
    }

    /**
     * 消息数量增加
     */
    fun msgCountAdd(isSend: Boolean = false) {
        if (isSend) {
            sendCount++
        } else {
            receiveCount++
        }
    }

    /**
     * 当会话 id 相同就认为两个会话相同
     */
    override fun equals(other: Any?): Boolean {
        if (other is IMConversation) {
            return chatId == other.chatId
        }
        return super.equals(other)
    }
}