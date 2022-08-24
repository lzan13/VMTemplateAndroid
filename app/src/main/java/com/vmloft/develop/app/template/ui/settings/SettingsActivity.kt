package com.vmloft.develop.app.template.ui.settings

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivitySettingsBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.SignManager

/**
 * Create by lzan13 on 2020/06/07 21:06
 * 描述：设置
 */
@Route(path = AppRouter.appSettings)
class SettingsActivity : BActivity<ActivitySettingsBinding>() {

    lateinit var user:User

    override fun initVB() = ActivitySettingsBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.settings)

        mBinding.settingsAccountSecurityLV.setOnClickListener { CRouter.go(AppRouter.appSettingsAccountSecurity) }
        mBinding.settingsPrivacyLV.setOnClickListener { CRouter.go(AppRouter.appSettingsPrivacy) }

        mBinding.settingsDarkLV.setOnClickListener { CRouter.go(AppRouter.appSettingsDark) }

        mBinding.settingsNotifyLV.setOnClickListener { CRouter.go(AppRouter.appSettingsNotify) }
        mBinding.settingsPictureLV.setOnClickListener { CRouter.go(AppRouter.appSettingsMedia) }
        mBinding.settingsAboutLV.setOnClickListener { CRouter.go(AppRouter.appSettingsAbout) }

        mBinding.settingsSignOutLV.setOnClickListener { showSignOut() }
    }

    override fun initData() {
        user = SignManager.getSignUser()
    }

    /**
     * 退出登录二次弹窗
     */
    private fun showSignOut() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            if(user.username.isEmpty()){
                dialog.setContent(R.string.sign_out_username_hint)
            }else{
                dialog.setContent(R.string.sign_out_hint)
            }
            dialog.setPositive(listener = {
                // 清空登录信息统一交给 Main 界面处理
                CRouter.goMain(1)
            })
            dialog.show()
        }
    }


}