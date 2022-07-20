package com.vmloft.develop.library.im.chat.msg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMVoiceMessageBody

import com.vmloft.develop.library.im.chat.IMVoiceManager
import com.vmloft.develop.library.im.databinding.ImItemMsgVoiceSendDelegateBinding
import com.vmloft.develop.library.tools.utils.VMDimen

/**
 * Create by lzan13 on 2021/07/03 23:56
 * 描述：展示语音消息 Item
 */
class MsgVoiceSendDelegate(longListener: BItemLongListener<EMMessage>) : MsgCommonDelegate<ImItemMsgVoiceSendDelegateBinding>(longListener = longListener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemMsgVoiceSendDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemMsgVoiceSendDelegateBinding>, item: EMMessage) {
        super.onBindView(holder, item)

        val body = item.body as EMVoiceMessageBody
        holder.binding.imMsgVoiceTimeTV.text = "${body.length / 1000}''"

        changeWaveWidth(holder, body.length)

        holder.binding.imMsgContentLL.setOnClickListener {
            IMVoiceManager.preparePlay(item, holder.binding.imMsgVoicePlayIV, holder.binding.imMsgWaveView)
        }
    }

    /**
     * 修改波形控件宽度
     */
    private fun changeWaveWidth(holder: BItemHolder<ImItemMsgVoiceSendDelegateBinding>, time: Int) {
        val lp = holder.binding.imMsgWaveView.layoutParams
        lp.width = when {
            time < 5000 -> VMDimen.dp2px(36)
            time < 10000 -> VMDimen.dp2px(56)
            time < 20000 -> VMDimen.dp2px(72)
            time < 30000 -> VMDimen.dp2px(96)
            time < 50000 -> VMDimen.dp2px(128)
            else -> VMDimen.dp2px(192)
        }
        holder.binding.imMsgWaveView.layoutParams = lp
        holder.binding.imMsgWaveView.requestLayout()
    }
}
