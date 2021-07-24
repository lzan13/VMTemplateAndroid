package com.vmloft.develop.app.template.ui.widget

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.router.AppRouter

import com.vmloft.develop.library.common.widget.CommonDialog
import com.vmloft.develop.library.tools.utils.VMColor
import com.vmloft.develop.library.tools.utils.VMStr
import kotlinx.android.synthetic.main.widget_agreement_policy_dialog.*


/**
 * Create by lzan13 on 2021/07/10 21:41
 * 描述：用户政策与隐私协议对话框
 */
class AgreementPolicyDialog(context: Context) : CommonDialog(context) {

    init {
        val agreementContent = VMStr.byRes(R.string.agreement_policy_dialog_content)
        val userAgreement = VMStr.byRes(R.string.user_agreement)
        val privatePolicy = VMStr.byRes(R.string.private_policy)
        //创建一个 SpannableString对象
        val sp = SpannableString(agreementContent)
        var start = 0
        var end = 0

        start = agreementContent.indexOf(userAgreement)
        end = start + userAgreement.length
        //设置超链接
        sp.setSpan(CustomURLSpan("agreement"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        //设置高亮样式
        sp.setSpan(ForegroundColorSpan(VMColor.byRes(R.color.app_accent)), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置高亮样式二

        start = agreementContent.indexOf(privatePolicy)
        end = start + privatePolicy.length
        //设置超链接
        sp.setSpan(CustomURLSpan("policy"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        //设置高亮样式
        sp.setSpan(ForegroundColorSpan(VMColor.byRes(R.color.app_accent)), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        //设置斜体
//        sp.setSpan(StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 19, 21, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置下划线
//        sp.setSpan(UnderlineSpan(), 22, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        dialogContentTV.text = sp
        dialogContentTV.movementMethod = LinkMovementMethod.getInstance()

    }

    override fun layoutId() = R.layout.widget_agreement_policy_dialog


    override fun getNegativeTV(): TextView? = dialogNegativeTV

    override fun getPositiveTV(): TextView? = dialogPositiveTV


    class CustomURLSpan(val type: String) : URLSpan(type) {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            //设置超链接文本的颜色
//            ds.color = Color.RED
            //这里可以去除点击文本的默认的下划线
            ds.isUnderlineText = false
        }

        override fun onClick(widget: View) {
            //去除点击后字体出现的背景色
            (widget as? TextView)?.highlightColor = VMColor.byRes(R.color.vm_transparent)
            AppRouter.goAgreementPolicy(type)
        }
    }
}