package com.vmloft.develop.library.im.chat.msg

import android.view.View
import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImItemMsgSystemDelegateBinding
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示系统消息 Item
 */
class MsgSystemDelegate : BItemDelegate<EMMessage, ImItemMsgSystemDelegateBinding>() {

    override fun layoutId(): Int = R.layout.im_item_msg_system_delegate

    override fun onBindView(holder: BItemHolder<ImItemMsgSystemDelegateBinding>, item: EMMessage) {
        holder.binding.imMsgTimeTV.visibility = if (IMChatManager.isShowTime(getPosition(holder), item)) View.VISIBLE else View.GONE

        holder.binding.time = item.localTime()

        val content = when (IMChatManager.getMsgType(item)) {
            IMConstants.MsgType.imRecall -> VMStr.byRes(R.string.im_recall_already)
            else -> item.getStringAttribute(IMConstants.Common.msgAttrSystem, "")
        }
        holder.binding.imMsgContentTV.text = content

        holder.binding.executePendingBindings()
    }
}
