package com.vmloft.develop.library.im.chat.msg

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.common.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImItemMsgSystemDelegateBinding
import com.vmloft.develop.library.tools.utils.VMColor
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示系统消息 Item
 */
class MsgSystemDelegate : MsgCommonDelegate<ImItemMsgSystemDelegateBinding>() {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemMsgSystemDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemMsgSystemDelegateBinding>, item: IMMessage) {
        super.onBindView(holder, item)

        val content = when (IMChatManager.getMsgType(item)) {
            IMConstants.MsgType.imSystemRecall -> VMStr.byRes(R.string.im_recall_already)
            else -> {
                var tips = item.getStringAttribute(IMConstants.Common.extSystemTips, "")
                var url = item.getStringAttribute(IMConstants.Common.extSystemUrl, "")
                if (tips.isNotEmpty()) {
                    val content = item.body + tips
                    // 创建一个 SpannableString对象
                    val sp = SpannableString(item.body + tips)
                    var start = content.indexOf(tips)
                    var end = start + tips.length
                    //设置超链接
                    sp.setSpan(CustomURLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    //设置高亮样式
                    sp.setSpan(ForegroundColorSpan(VMColor.byRes(R.color.app_accent)), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    sp
                } else {
                    item.body
                }
            }
        }
        holder.binding.imMsgContentTV.text = content

    }
}

/**
 * 自定义超链接处理
 */
class CustomURLSpan(val url: String) : URLSpan(url) {
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        //设置超链接文本的颜色
//            ds.color = Color.RED
        //这里可以去除点击文本的默认的下划线
        ds.isUnderlineText = false
    }

    override fun onClick(widget: View) {
        CRouter.goWeb(url)
    }
}