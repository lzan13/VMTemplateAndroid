package com.vmloft.develop.library.im.chat.msg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody

import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImItemMsgSystemDelegateBinding
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示系统消息 Item
 */
class MsgSystemDelegate : MsgCommonDelegate<ImItemMsgSystemDelegateBinding>() {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemMsgSystemDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemMsgSystemDelegateBinding>, item: EMMessage) {
        super.onBindView(holder, item)

        val content = when (IMChatManager.getMsgType(item)) {
            IMConstants.MsgType.imRecall -> VMStr.byRes(R.string.im_recall_already)
            else -> {
                var content = item.getStringAttribute(IMConstants.Common.msgAttrSystem, "")
                if (content.isNullOrEmpty()) {
                    content = (item.body as EMTextMessageBody).message
                }
                content
            }
        }
        holder.binding.imMsgContentTV.text = content

    }
}
