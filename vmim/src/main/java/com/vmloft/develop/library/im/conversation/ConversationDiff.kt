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

        val oldUnread = IMChatManager.instance.getConversationUnread(oldItem)
        val newUnread = IMChatManager.instance.getConversationUnread(newItem)

        val oldTop = IMChatManager.instance.getConversationTop(oldItem)
        val newTop = IMChatManager.instance.getConversationTop(newItem)

        val oldDraft = IMChatManager.instance.getConversationDraft(oldItem)
        val newDraft = IMChatManager.instance.getConversationDraft(newItem)

        val oldSummary = IMChatManager.instance.getSummary(oldItem.lastMessage)
        val newSummary = IMChatManager.instance.getSummary(newItem.lastMessage)

        return oldUnread == newUnread && oldDraft == newDraft && oldSummary == newSummary
    }
}