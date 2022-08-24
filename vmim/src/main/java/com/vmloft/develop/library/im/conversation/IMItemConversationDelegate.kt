package com.vmloft.develop.library.im.conversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMConversation
import com.vmloft.develop.library.im.databinding.ImItemConversationDelegateBinding
import com.vmloft.develop.library.tools.utils.VMColor

/**
 * Create by lzan13 on 2021/05/22 17:56
 * 描述：展示最近会话内容 Item
 */
class IMItemConversationDelegate(listener: BItemListener<IMConversation>, longListener: BItemLongListener<IMConversation>) :
    BItemDelegate<IMConversation, ImItemConversationDelegateBinding>(listener, longListener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemConversationDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemConversationDelegateBinding>, item: IMConversation) {
        holder.binding.imConversationTopIV.visibility = if (item.top) View.VISIBLE else View.GONE

        holder.binding.imConversationUnreadTV.text = FormatUtils.wrapUnread(item.unread)
        holder.binding.imConversationUnreadTV.visibility = if (item.unread > 0) View.VISIBLE else View.GONE

        val user = CacheManager.getUser(item.chatId)
        IMGLoader.loadAvatar(holder.binding.imConversationAvatarIV, user.avatar)

        // 身份
        holder.binding.imConversationIdentityIV.visibility = View.VISIBLE
        holder.binding.imConversationTitleTV.setTextColor(VMColor.byRes(R.color.app_identity_special))
        if (user.role.identity in 100..199 && ConfigManager.appConfig.tradeConfig.vipEntry) {
            holder.binding.imConversationIdentityIV.setImageResource(R.drawable.ic_vip)
        } else if (user.role.identity > 700) {
            holder.binding.imConversationIdentityIV.setImageResource(R.drawable.ic_official)
        } else {
            holder.binding.imConversationTitleTV.setTextColor(VMColor.byRes(R.color.app_title))
            holder.binding.imConversationIdentityIV.visibility = View.GONE
        }


        holder.binding.imConversationTitleTV.text = user.nickname
        holder.binding.imConversationContentTV.text = item.content
        holder.binding.imConversationTimeTV.text = FormatUtils.relativeTime(item.time)

    }
}
