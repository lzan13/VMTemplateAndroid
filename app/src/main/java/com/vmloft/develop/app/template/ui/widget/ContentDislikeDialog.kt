package com.vmloft.develop.app.template.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View

import com.vmloft.develop.app.template.databinding.WidgetContentDislikeDialogBinding
import com.vmloft.develop.library.tools.base.VMBDialog


/**
 * Create by lzan13 on 2021/07/10 21:41
 * 描述：内容举报反馈屏蔽对话框
 */
class ContentDislikeDialog(context: Context) : VMBDialog<WidgetContentDislikeDialogBinding>(context) {
    override fun initVB() = WidgetContentDislikeDialogBinding.inflate(LayoutInflater.from(context))

    /**
     * 设置屏蔽监听
     */
    fun setShieldListener(callback: (Int) -> Unit) {
        mBinding.shieldTitleTV.visibility = View.VISIBLE
        mBinding.shieldVG.visibility = View.VISIBLE

        mBinding.shieldContentTV.setOnClickListener { callback(0) }
        mBinding.shieldAuthorTV.setOnClickListener { callback(1) }
    }

    /**
     * 设置举报监听
     */
    fun setReportListener(callback: (Int) -> Unit) {
        mBinding.reportAdsTV.setOnClickListener { callback(1) }
        mBinding.reportSensitivityTV.setOnClickListener { callback(2) }
        mBinding.reportIllegalTV.setOnClickListener { callback(3) }
        mBinding.reportPornVulgarTV.setOnClickListener { callback(4) }
        mBinding.reportViolenceTV.setOnClickListener { callback(5) }
        mBinding.reportGuideTV.setOnClickListener { callback(6) }
        mBinding.reportUncivilizedTV.setOnClickListener { callback(7) }
        mBinding.reportFraudTV.setOnClickListener { callback(8) }
        mBinding.reportUncomfortableTV.setOnClickListener { callback(9) }
        mBinding.reportOtherTV.setOnClickListener { callback(10) }
    }
}