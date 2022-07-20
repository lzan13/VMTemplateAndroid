package com.vmloft.develop.library.im.chat.msg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hyphenate.chat.EMMessage
import com.vmloft.develop.library.data.bean.Gift
import com.vmloft.develop.library.data.common.CacheManager

import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImItemMsgGiftReceiveDelegateBinding
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2021/07/09 23:56
 * 描述：展示礼物消息 Item
 */
class MsgGiftReceiveDelegate(listener: BItemListener<EMMessage>, longListener: BItemLongListener<EMMessage>?=null) :
    MsgCommonDelegate<ImItemMsgGiftReceiveDelegateBinding>(listener, longListener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemMsgGiftReceiveDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemMsgGiftReceiveDelegateBinding>, item: EMMessage) {
        super.onBindView(holder, item)

        // 获取礼物消息扩展内容
        val giftStr = item.getStringAttribute(IMConstants.Common.msgAttrGift, "")
        val gift = JsonUtils.fromJson<Gift>(giftStr)
        IMGLoader.loadCover(holder.binding.imMsgGiftIV, gift.cover.path)

        holder.binding.imMsgContentTV.text = VMStr.byResArgs(R.string.im_gift_msg_receive, gift.title)
    }
}
