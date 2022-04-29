package com.vmloft.develop.app.template.ui.settings

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.databinding.ActivitySettingsAboutBinding
import com.vmloft.develop.app.template.request.bean.Version
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.request.viewmodel.CommonViewModel
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMSystem
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/05/02 22:56
 * 描述：关于
 */
@Route(path = AppRouter.appSettingsAbout)
class AboutSettingsActivity : BVMActivity<ActivitySettingsAboutBinding, CommonViewModel>() {

    private val maxCount = 5
    private val maxInterval = CConstants.timeSecond
    private var mCount: Int = 0
    private var mTime: Long = 0L


    override fun initVB() = ActivitySettingsAboutBinding.inflate(layoutInflater)

    override fun initVM(): CommonViewModel = getViewModel()


    override fun initUI() {
        super.initUI()

        mBinding.aboutIconIV.setOnClickListener { goDebug() }
        // 反馈
        mBinding.aboutFeedbackLV.setOnClickListener { CRouter.go(AppRouter.appFeedback, Constants.FeedbackType.opinion) }
        // 检查更新
        mBinding.checkVersionLV.setOnClickListener { mViewModel.checkVersion(true) }

        mBinding.userNormTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "norm") }
        mBinding.userAgreementTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "agreement") }
        mBinding.privatePolicyTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "policy") }
    }

    override fun initData() {
        mBinding.aboutVersionTV.text = "Version ${VMSystem.versionName}"
    }

    /**
     * 打开调试界面
     */
    private fun goDebug() {
        val interval = System.currentTimeMillis() - mTime
        if (interval > maxInterval) {
            mCount = 0
        }
        if (mCount < maxCount) {
            mCount++
        } else {
            CRouter.go(CRouter.commonDebug)
        }
        mTime = System.currentTimeMillis()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "checkVersion") {
            if (model.data == null) return showBar(R.string.version_is_new)
            showVersionDialog(model.data as Version)
        }
    }

    /**
     * 显示版本更新弹窗
     */
    private fun showVersionDialog(version: Version) {
        if (version.versionCode <= VMSystem.versionCode) {
            return showBar(R.string.version_is_new)
        }

        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            // 判断是否需要强制更新，如果强制更新，则不能关闭对话框
            val force = version.force or (version.versionCode - VMSystem.versionCode > 3)
            dialog.backDismissSwitch = !force
            dialog.touchDismissSwitch = !force

            dialog.setTitle(version.title)
            dialog.setContent(version.desc)
            dialog.setNegative(if (force) "" else VMStr.byRes(R.string.version_skip))
            dialog.setPositive(VMStr.byRes(R.string.version_upgrade)) {
                if (version.url.isNullOrEmpty()) {
                    errorBar("升级地址出错，请联系管理员")
                } else {
                    CRouter.goWeb(version.url, true)
                }
            }
            dialog.show()
        }
    }

}