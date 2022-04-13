package com.vmloft.develop.app.template.ui.widget

import android.content.Context
import android.view.LayoutInflater
import com.vmloft.develop.app.template.R

import com.vmloft.develop.app.template.databinding.WidgetUserDislikeDialogBinding
import com.vmloft.develop.library.tools.base.VMBDialog


/**
 * Create by lzan13 on 2022/03/29 21:41
 * 描述：用户举报对话框
 */
class UserDislikeDialog(context: Context) : VMBDialog<WidgetUserDislikeDialogBinding>(context) {
    override fun initVB() = WidgetUserDislikeDialogBinding.inflate(LayoutInflater.from(context))

    /**
     * 设置黑名单监听
     */
    fun setBlacklistListener(blacklist: Int, callback: () -> Unit) {
        if (blacklist == 0 || blacklist == 2) {
            mBinding.blacklistTV.setText(R.string.blacklist_remove)
        }
        mBinding.blacklistTV.setOnClickListener { callback() }
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