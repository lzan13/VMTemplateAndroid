package com.vmloft.develop.library.im.conversation

import com.hyphenate.chat.EMConversation

import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.databinding.ImItemConversationDelegateBinding

/**
 * Create by lzan13 on 2021/05/22 17:56
 * 描述：展示最近会话内容 Item
 */
class IMItemConversationDelegate(listener: BItemListener<EMConversation>, longListener: BItemLongListener<EMConversation>) :
    BItemDelegate<EMConversation, ImItemConversationDelegateBinding>(listener, longListener) {

    override fun layoutId(): Int = R.layout.im_item_conversation_delegate

    override fun onBindView(holder: BItemHolder<ImItemConversationDelegateBinding>, item: EMConversation) {
        holder.binding.top = IMChatManager.getConversationTop(item)

        holder.binding.unread = IMChatManager.getConversationUnread(item)

        val user = IM.imListener.getUser(item.conversationId())
        IMGLoader.loadAvatar(holder.binding.imConversationAvatarIV, user?.avatar ?: "")
        holder.binding.title = user?.nickname ?: "小透明"
        holder.binding.content = IMChatManager.getSummary(item.lastMessage)
        holder.binding.time = IMChatManager.getConversationTime(item)

        holder.binding.executePendingBindings()
    }
}
