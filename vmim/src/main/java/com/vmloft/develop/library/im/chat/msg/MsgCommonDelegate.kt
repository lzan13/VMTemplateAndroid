package com.vmloft.develop.library.im.chat.msg

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.viewbinding.ViewBinding

import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.common.IMChatManager
import com.vmloft.develop.library.image.IMGLoader

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：通用的消息处理
 */
abstract class MsgCommonDelegate<VB : ViewBinding>(val listener: BItemListener<IMMessage>? = null, val longListener: BItemLongListener<IMMessage>? = null) :
    BItemDelegate<IMMessage, VB>() {
    override fun onBindView(holder: BItemHolder<VB>, item: IMMessage) {
        // 统一设置点击处理
        val imMsgBubbleView = holder.binding.root.findViewById<View>(R.id.imMsgBubbleView)
        imMsgBubbleView?.setOnClickListener { listener?.onClick(it, item, getPosition(holder)) }

        imMsgBubbleView?.setOnTouchListener { _, event ->
            mEvent = event
            false
        }
        imMsgBubbleView?.setOnLongClickListener { longListener?.onLongClick(it, mEvent, item, getPosition(holder)) ?: false }

        val imMsgTimeTV = holder.binding.root.findViewById<TextView>(R.id.imMsgTimeTV)
        imMsgTimeTV.visibility = if (IMChatManager.isShowTime(getPosition(holder), item)) View.VISIBLE else View.GONE
        imMsgTimeTV.text = FormatUtils.relativeTime(item.time)

        // 有些消息不带有头像（系统消息，撤回消息，未知消息），这里需要做个非空判断
        val imMsgAvatarIV = holder.binding.root.findViewById<ImageView>(R.id.imMsgAvatarIV)
        imMsgAvatarIV?.let {

            val user = if (item.isSend) {
                SignManager.getSignUser()
            } else {
                CacheManager.getUser(item.from)
            }
            IMGLoader.loadAvatar(imMsgAvatarIV, user.avatar)

            // 点击头像
            imMsgAvatarIV.setOnClickListener {
                if (item.isSend) {
                    // TODO 自己暂时不可点击
                    return@setOnClickListener
                }
                IM.onHeadClick(item.from)
            }
        }
        // 只有普通消息的发送方才有发送状态和失败的情况
        val imMsgLoadingView = holder.binding.root.findViewById<View>(R.id.imMsgLoadingView)
        val imMsgFailedIV = holder.binding.root.findViewById<View>(R.id.imMsgFailedIV)
        imMsgLoadingView?.visibility = if (item.status == 0) View.VISIBLE else View.GONE
        imMsgFailedIV?.visibility = if (item.status == 2) View.VISIBLE else View.GONE
        // 重发消息
        imMsgFailedIV?.setOnClickListener {
            item.resendCount++
            IMChatManager.sendMessage(item)
        }

    }
}
