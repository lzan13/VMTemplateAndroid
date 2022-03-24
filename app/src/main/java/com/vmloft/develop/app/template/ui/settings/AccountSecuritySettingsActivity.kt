package com.vmloft.develop.app.template.ui.settings

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivitySettingsAccountSecurityBinding
import com.vmloft.develop.app.template.request.viewmodel.SettingsViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.tools.utils.VMSystem

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2021/07/30 23:06
 * 描述：账户与安全设置页
 */
@Route(path = AppRouter.appSettingsAccountSecurity)
class AccountSecuritySettingsActivity : BVMActivity<ActivitySettingsAccountSecurityBinding, SettingsViewModel>() {

    override fun initVM(): SettingsViewModel = getViewModel()

    override fun initVB() = ActivitySettingsAccountSecurityBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.settings_account_security)

        mBinding.settingsSignDestroyLV.setOnClickListener { showSignDestroy() }
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