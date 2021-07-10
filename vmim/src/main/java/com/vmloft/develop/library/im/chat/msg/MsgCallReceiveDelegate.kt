package com.vmloft.develop.library.im.chat.msg

import android.view.View
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody

import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImItemMsgCallReceiveDelegateBinding

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示通话记录消息 Item
 */
class MsgCallReceiveDelegate(listener: BItemLongListener<EMMessage>? = null) : BItemDelegate<EMMessage, ImItemMsgCallReceiveDelegateBinding>(longListener = listener) {

    override fun layoutId(): Int = R.layout.im_item_msg_call_receive_delegate

    override fun onBindView(holder: BItemHolder<ImItemMsgCallReceiveDelegateBinding>, item: EMMessage) {
        holder.binding.imMsgTimeTV.visibility = if (IMChatManager.isShowTime(getPosition(holder), item)) View.VISIBLE else View.GONE

        val user = IM.imListener.getUser(item.from)
        IMGLoader.loadAvatar(holder.binding.imMsgAvatarIV, user?.avatar ?: "")

        holder.binding.type = item.getIntAttribute(IMConstants.Call.msgAttrCallType, IMConstants.Call.callTypeVoice)

        holder.binding.time = item.localTime()

        holder.binding.content = (item.body as EMTextMessageBody).message

        holder.binding.executePendingBindings()
        // 点击头像
        holder.binding.imMsgAvatarIV.setOnClickListener { IM.imListener.onHeadClick(item.from) }
    }
}
