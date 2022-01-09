package com.vmloft.develop.library.im.conversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.EMConversation

import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.utils.FormatUtils
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.databinding.ImItemConversationDelegateBinding

/**
 * Create by lzan13 on 2021/05/22 17:56
 * 描述：展示最近会话内容 Item
 */
class IMItemConversationDelegate(listener: BItemListener<EMConversation>, longListener: BItemLongListener<EMConversation>) :
    BItemDelegate<EMConversation, ImItemConversationDelegateBinding>(listener, longListener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemConversationDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemConversationDelegateBinding>, item: EMConversation) {
        holder.binding.imConversationTopIV.visibility = if (IMChatManager.getConversationTop(item)) View.VISIBLE else View.GONE

        holder.binding.imConversationUnreadTV.text = IMChatManager.getConversationUnread(item).toString()
        holder.binding.imConversationUnreadTV.visibility = if (IMChatManager.getConversationUnread(item) > 0) View.VISIBLE else View.GONE

        val user = IM.imListener.getUser(item.conversationId())
        IMGLoader.loadAvatar(holder.binding.imConversationAvatarIV, user?.avatar ?: "")

        holder.binding.imConversationTitleTV.text = user?.nickname ?: "小透明"
        holder.binding.imConversationContentTV.text = IMChatManager.getSummary(item.lastMessage)
        holder.binding.imConversationTimeTV.text = FormatUtils.relativeTime(IMChatManager.getConversationTime(item))

    }
}
