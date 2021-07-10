package com.vmloft.develop.library.im.chat.msg

import android.view.View
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody

import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.databinding.ImItemMsgTextReceiveDelegateBinding

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示文本消息 Item
 */
class MsgTextReceiveDelegate(listener: BItemLongListener<EMMessage>? = null) : BItemDelegate<EMMessage, ImItemMsgTextReceiveDelegateBinding>(longListener = listener) {

    override fun layoutId(): Int = R.layout.im_item_msg_text_receive_delegate

    override fun onBindView(holder: BItemHolder<ImItemMsgTextReceiveDelegateBinding>, item: EMMessage) {
        holder.binding.imMsgTimeTV.visibility = if (IMChatManager.isShowTime(getPosition(holder), item)) View.VISIBLE else View.GONE

        val user = IM.imListener.getUser(item.from)
        IMGLoader.loadAvatar(holder.binding.imMsgAvatarIV, user?.avatar ?: "")

        holder.binding.time = item.localTime()

        holder.binding.content = (item.body as EMTextMessageBody).message

        holder.binding.executePendingBindings()
        // 点击头像
        holder.binding.imMsgAvatarIV.setOnClickListener { IM.imListener.onHeadClick(item.from) }
    }
}
