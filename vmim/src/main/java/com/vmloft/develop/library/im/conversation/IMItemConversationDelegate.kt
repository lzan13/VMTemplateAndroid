package com.vmloft.develop.library.im.conversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hyphenate.chat.EMConversation

import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.databinding.ImItemConversationDelegateBinding
import com.vmloft.develop.library.tools.utils.VMColor

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

        val user = CacheManager.getUser(item.conversationId())
        IMGLoader.loadAvatar(holder.binding.imConversationAvatarIV, user.avatar)

        // 身份
        if (user.role.identity in 100..199 && ConfigManager.clientConfig.tradeConfig.vipEntry) {
            holder.binding.imConversationTitleTV.setTextColor(VMColor.byRes(R.color.app_identity_vip))
            holder.binding.imConversationIdentityIV.visibility = View.VISIBLE
        } else {
            holder.binding.imConversationTitleTV.setTextColor(VMColor.byRes(R.color.app_title))
            holder.binding.imConversationIdentityIV.visibility = View.GONE
        }

        holder.binding.imConversationTitleTV.text = if (user.nickname.isEmpty()) "小透明" else user.nickname
        holder.binding.imConversationContentTV.text = IMChatManager.getSummary(item.lastMessage)
        holder.binding.imConversationTimeTV.text = FormatUtils.relativeTime(IMChatManager.getConversationTime(item))

    }
}
