package com.vmloft.develop.app.template.ui.settings

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivitySettingsAccountSecurityBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.common.widget.CommonDialog
import com.vmloft.develop.library.tools.utils.VMSystem
import kotlinx.android.synthetic.main.activity_settings_account_security.*


import org.koin.androidx.viewmodel.ext.android.getViewModel

import kotlin.system.exitProcess


/**
 * Create by lzan13 on 2021/07/30 23:06
 * 描述：账户与安全设置页
 */
@Route(path = AppRouter.appSettingsAccountSecurity)
class AccountSecuritySettingsActivity : BVMActivity<SettingsViewModel>() {

    override fun initVM(): SettingsViewModel = getViewModel()

    override fun layoutId(): Int = R.layout.activity_settings_account_security

    override fun initUI() {
        super.initUI()
        (mBinding as ActivitySettingsAccountSecurityBinding).viewModel = mViewModel

        setTopTitle(R.string.settings_account_security)

        settingsSignDestroyLV.setOnClickListener { showSignDestroy() }
    }

    override fun initData() {
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "signDestroy") {
            showBar(R.string.account_destroy_success)
            VMSystem.runInUIThread({ CRouter.goMain(1) }, CConstants.timeSecond * 2)
        }
    }

    /**
     * 销毁账户二次弹窗
     */
    private fun showSignDestroy() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.account_destroy_hint)
            dialog.setPositive(listener = {
                // 销毁账户后直接结束 App
                mViewModel.signDestroy()
            })
            dialog.show()
        }
    }

}