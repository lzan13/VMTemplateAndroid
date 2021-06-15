package com.vmloft.develop.app.template.ui.settings

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BaseActivity
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.tools.utils.VMSystem

import kotlinx.android.synthetic.main.activity_settings_about.*

/**
 * Create by lzan13 on 2020/05/02 22:56
 * 描述：关于
 */
@Route(path = AppRouter.appSettingsAbout)
class AboutSettingsActivity : BaseActivity() {

    private val maxCount = 5
    private val maxInterval = CConstants.timeSecond
    private var mCount: Int = 0
    private var mTime: Long = 0L

    override fun layoutId(): Int = R.layout.activity_settings_about

    override fun initUI() {
        super.initUI()

        aboutIconIV.setOnClickListener { goDebug() }
        aboutFeedbackLV.setOnClickListener { CRouter.go(AppRouter.appFeedback) }
        // 检查更新
        aboutCheckVersionLV.setOnClickListener { showBar(R.string.version_check_toast) }


        aboutPrivacyPolicyTV.setOnClickListener { CRouter.goWeb(Constants.privatePolicyUrl) }
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


}