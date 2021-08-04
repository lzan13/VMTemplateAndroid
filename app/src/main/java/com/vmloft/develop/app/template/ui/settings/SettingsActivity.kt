package com.vmloft.develop.app.template.ui.settings

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BaseActivity
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.widget.CommonDialog

import kotlinx.android.synthetic.main.activity_settings.*



/**
 * Create by lzan13 on 2020/06/07 21:06
 * 描述：设置
 */
@Route(path = AppRouter.appSettings)
class SettingsActivity : BaseActivity() {


    override fun layoutId(): Int = R.layout.activity_settings

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.settings)

        settingsAccountSecurityLV.setOnClickListener { CRouter.go(AppRouter.appSettingsAccountSecurity) }

        settingsDarkLV.setOnClickListener { CRouter.go(AppRouter.appSettingsDark) }

        settingsNotifyLV.setOnClickListener { CRouter.go(AppRouter.appSettingsNotify) }
        settingsPictureLV.setOnClickListener { CRouter.go(AppRouter.appSettingsMedia) }
        settingsAboutLV.setOnClickListener { CRouter.go(AppRouter.appSettingsAbout) }

        settingsSignOutLV.setOnClickListener { showSignOut() }
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