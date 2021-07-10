package com.vmloft.develop.library.im.conversation

import com.hyphenate.chat.EMConversation
import com.vmloft.develop.library.im.chat.IMChatManager

/**
 * Create by lzan13 2021/05/26
 * 描述：会话列表比对
 */
class ConversationDiff(oldList: List<EMConversation>, newList: List<EMConversation>) : BDiffCallback<EMConversation>(oldList, newList) {
    override fun equalsItem(oldPosition: Int, newPosition: Int): Boolean {
        return getOldItem(oldPosition).conversationId().equals(getNewItem(newPosition).conversationId())
    }

    override fun equalsContent(oldPosition: Int, newPosition: Int): Boolean {
        val oldItem = getOldItem(oldPosition)
        val newItem = getNewItem(newPosition)

        val oldUnread = IMChatManager.getConversationUnread(oldItem)
        val newUnread = IMChatManager.getConversationUnread(newItem)

        val oldTop = IMChatManager.getConversationTop(oldItem)
        val newTop = IMChatManager.getConversationTop(newItem)

        val oldDraft = IMChatManager.getConversationDraft(oldItem)
        val newDraft = IMChatManager.getConversationDraft(newItem)

        val oldSummary = IMChatManager.getSummary(oldItem.lastMessage)
        val newSummary = IMChatManager.getSummary(newItem.lastMessage)

        return oldUnread == newUnread && oldDraft == newDraft && oldSummary == newSummary
    }
}