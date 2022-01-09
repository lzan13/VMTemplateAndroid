package com.vmloft.develop.library.im.chat.msg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody

import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.utils.FormatUtils
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.databinding.ImItemMsgSystemDelegateBinding
import com.vmloft.develop.library.im.databinding.ImItemMsgTextReceiveDelegateBinding

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示文本消息 Item
 */
class MsgTextReceiveDelegate(listener: BItemLongListener<EMMessage>? = null) : BItemDelegate<EMMessage, ImItemMsgTextReceiveDelegateBinding>(longListener = listener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemMsgTextReceiveDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemMsgTextReceiveDelegateBinding>, item: EMMessage) {
        holder.binding.imMsgTimeTV.visibility = if (IMChatManager.isShowTime(getPosition(holder), item)) View.VISIBLE else View.GONE
        holder.binding.imMsgTimeTV.text = FormatUtils.relativeTime(item.localTime())

        val user = IM.imListener.getUser(item.from)
        IMGLoader.loadAvatar(holder.binding.imMsgAvatarIV, user?.avatar ?: "")

        holder.binding.imMsgContentTV.text = (item.body as EMTextMessageBody).message

        // 点击头像
        holder.binding.imMsgAvatarIV.setOnClickListener { IM.imListener.onHeadClick(item.from) }
    }
}
