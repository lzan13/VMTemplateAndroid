package com.vmloft.develop.app.template.ui.splash

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SPManager
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.ui.widget.AgreementPolicyDialog
import com.vmloft.develop.library.common.base.BaseActivity
import com.vmloft.develop.library.common.router.CRouter
import kotlin.system.exitProcess

/**
 * Create by lzan13 2021/5/17
 * 描述：闪屏页，做承接调整用
 */
class SplashActivity : BaseActivity() {

    override fun layoutId(): Int = R.layout.activity_splash

    override fun initUI() {
        super.initUI()
        if (SPManager.isAgreementPolicy()) {
            jump()
        } else {
            showAgreementPolicy()
        }
    }

    override fun initData() {

    }

    private fun jump() {
        if (SPManager.isGuideShow()) {
            CRouter.go(AppRouter.appGuide)
        } else if (!SignManager.isSingIn()) {
            CRouter.go(AppRouter.appSignGuide)
        } else {
            CRouter.goMain()
        }
        finish()
    }

    private fun showAgreementPolicy() {
        mDialog = AgreementPolicyDialog(this)
        (mDialog as AgreementPolicyDialog).let { dialog ->
            dialog.setNegative("不同意") {
                finish()
            }
            dialog.setPositive("同意") {
                SPManager.setAgreementPolicy()
                jump()
            }
            dialog.show()
        }
    }
}