package com.vmloft.develop.app.template.ui.settings

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivitySettingsBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BActivity
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.ui.widget.CommonDialog

/**
 * Create by lzan13 on 2020/06/07 21:06
 * 描述：设置
 */
@Route(path = AppRouter.appSettings)
class SettingsActivity : BActivity<ActivitySettingsBinding>() {

    override fun initVB()=ActivitySettingsBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.settings)

        mBinding.settingsAccountSecurityLV.setOnClickListener { CRouter.go(AppRouter.appSettingsAccountSecurity) }

        mBinding.settingsDarkLV.setOnClickListener { CRouter.go(AppRouter.appSettingsDark) }

        mBinding.settingsNotifyLV.setOnClickListener { CRouter.go(AppRouter.appSettingsNotify) }
        mBinding.settingsPictureLV.setOnClickListener { CRouter.go(AppRouter.appSettingsMedia) }
        mBinding.settingsAboutLV.setOnClickListener { CRouter.go(AppRouter.appSettingsAbout) }

        mBinding.settingsSignOutLV.setOnClickListener { showSignOut() }
    }

    override fun initData() {
    }

    /**
     * 退出登录二次弹窗
     */
    private fun showSignOut() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.sign_out_hint)
            dialog.setPositive(listener = {
                // 清空登录信息统一交给 Main 界面处理
                CRouter.goMain(1)
            })
            dialog.show()
        }
    }


}