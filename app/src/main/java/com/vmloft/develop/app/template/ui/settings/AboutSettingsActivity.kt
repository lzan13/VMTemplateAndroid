package com.vmloft.develop.app.template.ui.settings

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.request.bean.Version
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.ui.main.mine.info.InfoViewModel
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.common.widget.CommonDialog
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMSystem

import kotlinx.android.synthetic.main.activity_settings_about.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/05/02 22:56
 * 描述：关于
 */
@Route(path = AppRouter.appSettingsAbout)
class AboutSettingsActivity : BVMActivity<InfoViewModel>() {

    private val maxCount = 5
    private val maxInterval = CConstants.timeSecond
    private var mCount: Int = 0
    private var mTime: Long = 0L


    override fun initVM(): InfoViewModel = getViewModel()

    override fun layoutId(): Int = R.layout.activity_settings_about

    override fun initUI() {
        super.initUI()

        aboutIconIV.setOnClickListener { goDebug() }
        aboutFeedbackLV.setOnClickListener { CRouter.go(AppRouter.appFeedback) }
        // 检查更新
        aboutCheckVersionLV.setOnClickListener { mViewModel.checkVersion(true) }

        aboutPrivatePolicyTV.setOnClickListener { AppRouter.goAgreementPolicy("policy") }
    }

    override fun initData() {
        aboutVersionTV.text = "Version ${VMSystem.versionName}"
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
            CRouter.goDebug()
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
        if (version.versionCode <= VMSystem.versionCode) return

        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            // 判断是否需要强制更新，如果强制更新，则不能关闭对话框
            val force = version.force or (version.versionCode - VMSystem.versionCode > 10)
            dialog.backDismissSwitch = !force
            dialog.touchDismissSwitch = !force

            dialog.setTitle(version.title)
            dialog.setContent(version.desc)
            dialog.setNegative(if (force) "" else VMStr.byRes(R.string.version_skip))
            dialog.setPositive(VMStr.byRes(R.string.version_upgrade)) {
                if (version.url.isNullOrEmpty()) {
                    errorBar("无法打开升级地址")
                } else {
                    CRouter.goWeb(version.url, true)
                }
            }
            dialog.show()
        }
    }

}