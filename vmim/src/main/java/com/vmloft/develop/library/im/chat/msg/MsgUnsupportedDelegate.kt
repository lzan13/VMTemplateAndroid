package com.vmloft.develop.library.im.chat.msg

import android.view.View
import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImItemMsgUnsupportedDelegateBinding
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示未知消息 Item
 */
class MsgUnsupportedDelegate : BItemDelegate<EMMessage, ImItemMsgUnsupportedDelegateBinding>() {

    override fun layoutId(): Int = R.layout.im_item_msg_unsupported_delegate

    override fun onBindView(holder: BItemHolder<ImItemMsgUnsupportedDelegateBinding>, item: EMMessage) {
        holder.binding.imMsgTimeTV.visibility = if (IMChatManager.isShowTime(getPosition(holder), item)) View.VISIBLE else View.GONE

        holder.binding.time = item.localTime()

        val content = item.getStringAttribute(IMConstants.Common.msgAttrUnsupported, VMStr.byRes(R.string.im_unknown_msg))
        holder.binding.imMsgContentTV.text = content

        holder.binding.executePendingBindings()
    }
}
