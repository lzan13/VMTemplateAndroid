package com.vmloft.develop.library.im.chat.msg

import android.view.LayoutInflater
import android.view.ViewGroup

import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.chat.IMVoiceManager
import com.vmloft.develop.library.im.databinding.ImItemMsgVoiceReceiveDelegateBinding
import com.vmloft.develop.library.tools.utils.VMDimen

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示文本消息 Item
 */
class MsgVoiceReceiveDelegate(longListener: BItemLongListener<IMMessage>) : MsgCommonDelegate<ImItemMsgVoiceReceiveDelegateBinding>(longListener = longListener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemMsgVoiceReceiveDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemMsgVoiceReceiveDelegateBinding>, item: IMMessage) {
        super.onBindView(holder, item)

        val attachment = item.attachments[0]
        holder.binding.imMsgVoiceTimeTV.text = "${attachment.duration / 1000}''"

        changeWaveWidth(holder, attachment.duration)

        holder.binding.imMsgContentLL.setOnClickListener {
            IMVoiceManager.preparePlay(item, holder.binding.imMsgVoicePlayIV, holder.binding.imMsgWaveView)
        }
    }

    /**
     * 修改波形控件宽度
     */
    private fun changeWaveWidth(holder: BItemHolder<ImItemMsgVoiceReceiveDelegateBinding>, time: Int) {
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
