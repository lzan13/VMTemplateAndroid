package com.vmloft.develop.app.template.ui.splash

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.app.App
import com.vmloft.develop.app.template.common.SPManager
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivitySplashBinding
import com.vmloft.develop.app.template.request.bean.Config
import com.vmloft.develop.app.template.request.viewmodel.SplashViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.ui.widget.AgreementPolicyDialog
import com.vmloft.develop.library.ads.ADSConstants
import com.vmloft.develop.library.ads.ADSManager
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.report.ReportManager
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.push.CustomPushManager

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 2021/5/17
 * 描述：闪屏页，做承接调整用
 */
class SplashActivity : BVMActivity<ActivitySplashBinding, SplashViewModel>() {

    override fun initVB() = ActivitySplashBinding.inflate(layoutInflater)

    override fun initVM(): SplashViewModel = getViewModel()

    override fun initUI() {
        super.initUI()

    }

    override fun initData() {
        mViewModel.clientConfig()
    }


    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "clientConfig"){
            ConfigManager.setupConfig((model.data as Config).content)
            checkAgreementPolicy()
        }
    }

    override fun onModelError(model: BViewModel.UIModel) {
        checkAgreementPolicy()
    }

    /**
     * 检查隐私协议状态
     */
    private fun checkAgreementPolicy() {
        if (SPManager.isAgreementPolicy()) {
            setTheme(R.style.AppTheme)
            if (ConfigManager.clientConfig.adsEntry.splashEntry) {
                loadAD()
            } else {
                jump()
            }
        } else {
            showAgreementPolicy()
        }
    }

    /**
     * 加载广告
     */
    private fun loadAD() {
        ADSManager.loadSplashAD(this) { status ->
            if (isFinishing) return@loadSplashAD
            if (status == ADSConstants.Status.timeout || status == ADSConstants.Status.failed) {
                jump()
            } else if (status == ADSConstants.Status.loaded) {
                ADSManager.showSplashAD(this@SplashActivity, mBinding.adsContainerLL)
            } else if (status == ADSConstants.Status.close) {
                jump()
            }
        }
    }

    /**
     * 页面跳转
     */
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

    /**
     * 显示隐私政策
     */
    private fun showAgreementPolicy() {
        mDialog = AgreementPolicyDialog(this)
        (mDialog as AgreementPolicyDialog).let { dialog ->
            dialog.setNegative("不同意") {
                finish()
            }
            dialog.setPositive("同意") {
                SPManager.setAgreementPolicy()

                CustomPushManager.init(App.appContext)

                ReportManager.init(App.appContext)

                jump()
            }
            dialog.show()
        }
    }

}