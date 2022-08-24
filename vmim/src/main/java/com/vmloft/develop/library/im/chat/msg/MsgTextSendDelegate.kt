package com.vmloft.develop.library.im.chat.msg

import android.view.LayoutInflater
import android.view.ViewGroup

import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.databinding.ImItemMsgTextSendDelegateBinding

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示文本消息 Item
 */
class MsgTextSendDelegate(listener: BItemLongListener<IMMessage>? = null) : MsgCommonDelegate<ImItemMsgTextSendDelegateBinding>(longListener = listener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemMsgTextSendDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemMsgTextSendDelegateBinding>, item: IMMessage) {
        super.onBindView(holder, item)

        holder.binding.imMsgContentTV.text = item.body
    }
}
