package com.vmloft.develop.library.im.bean

import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage

import kotlin.collections.ArrayList

/**
 * Create by lzan13 on 2019/5/21 15:00
 *
 * 自定义 IMConversation 会话对象
 */
class IMConversation constructor(chatId: String, chatType: Int) {

    private var conversationId: String = chatId

    // 内部会话对象
    private var conversation: EMConversation? = null

    init {
        val conversationType =
            when (chatType) {
                1 -> EMConversation.EMConversationType.GroupChat
                2 -> EMConversation.EMConversationType.ChatRoom
                else -> EMConversation.EMConversationType.Chat
            }
        conversation = EMClient.getInstance().chatManager().getConversation(conversationId, conversationType, true)
    }
    // 是否置顶
//    var isTop = false
//        get() {
//            field = IMChatUtils.getConversationTop(mConversation)
//            return field
//        }
//        set(top) {
//            field = top
//            IMChatUtils.setConversationTop(mConversation, top)
//        }

    // 未读状态
//    var isUnread = false
//        get() {
//            field = IMChatUtils.getConversationUnread(mConversation)
//            return field
//        }
//        set(unread) {
//            field = unread
//            IMChatUtils.setConversationUnread(mConversation, unread)
//        }

    // 最后一次更新时间
    private var mLastTime: Long = 0

    // 最后一条消息
    private var mLastMessage: IMMessage? = null

//    val id: String?
//        get() {
//            mId = mConversation!!.conversationId()
//            return mId
//        }
//    var draft: String?
//        get() {
//            mDraft = IMChatUtils.getConversationDraft(mConversation)
//            return mDraft
//        }
//        set(draft) {
//            mDraft = draft
//            IMChatUtils.setConversationDraft(mConversation, draft)
//        }
//    var lastTime: Long
//        get() {
//            mLastTime = IMChatUtils.getConversationLastTime(mConversation)
//            return mLastTime
//        }
//        set(lastTime) {
//            mLastTime = lastTime
//            IMChatUtils.setConversationLastTime(mConversation, lastTime)
//        }
//    var lastMessage: IMMessage?
//        get() {
//            mLastMessage = if (mConversation!!.allMsgCount == 0) {
//                IMMessage()
//            } else {
//                IMMessage(mConversation!!.lastMessage)
//            }
//            return mLastMessage
//        }
//        set(mLastMessage) {
//            this.mLastMessage = mLastMessage
//        }

    /**
     * 获取会话的所有消息
     */
    val allMessage: List<IMMessage>
        get() {
            val result = mutableListOf<IMMessage>()
            val list = conversation?.allMessages ?: ArrayList<EMMessage>()
            for (message in list) {
                result.add(IMMessage(message))
            }
            return result
        }

    /**
     * 加载更多消息
     *
     * @param msgId 开始加载的消息 Id
     * @param limit 加载条数
     */
    fun loadMoreMessage(msgId: String?, limit: Int): List<IMMessage> {
        val result = mutableListOf<IMMessage>()
        val list = conversation?.loadMoreMsgFromDB(msgId, limit) ?: ArrayList<EMMessage>()
        for (message in list) {
            result.add(IMMessage(message))
        }
        return result
    }

    /**
     * 当会话 id 相同就认为两个会话相同
     */
    override fun equals(other: Any?): Boolean {
        if (other is IMConversation) {
            return conversationId == other.conversationId
        }
        return super.equals(other)
    }
}