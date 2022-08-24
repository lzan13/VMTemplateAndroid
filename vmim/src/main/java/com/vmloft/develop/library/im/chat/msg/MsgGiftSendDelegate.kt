package com.vmloft.develop.library.im.chat.msg

import android.view.LayoutInflater
import android.view.ViewGroup

import com.vmloft.develop.library.data.bean.Gift
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImItemMsgGiftSendDelegateBinding
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2021/07/09 23:56
 * 描述：展示礼物消息 Item
 */
class MsgGiftSendDelegate(listener: BItemListener<IMMessage>, longListener: BItemLongListener<IMMessage>? = null) : MsgCommonDelegate<ImItemMsgGiftSendDelegateBinding>(listener, longListener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemMsgGiftSendDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemMsgGiftSendDelegateBinding>, item: IMMessage) {
        super.onBindView(holder, item)

        IMGLoader.loadCover(holder.binding.imMsgGiftIV, item.attachments[0].path)

        holder.binding.imMsgContentTV.text = VMStr.byResArgs(R.string.im_gift_msg_send, item.body)
    }
}
