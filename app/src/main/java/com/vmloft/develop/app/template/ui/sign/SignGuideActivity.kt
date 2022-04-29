package com.vmloft.develop.app.template.ui.sign


import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivitySignGuideBinding
import com.vmloft.develop.app.template.request.viewmodel.SignViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMSystem
import com.vmloft.develop.library.tools.utils.VMUtils

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/6/4 17:10
 * 描述：注册登录入口界面
 */
@Route(path = AppRouter.appSignGuide)
class SignGuideActivity : BVMActivity<ActivitySignGuideBinding, SignViewModel>() {

    private lateinit var devicesId: String
    private var password: String = "123456"

    override fun initVB() = ActivitySignGuideBinding.inflate(layoutInflater)

    override fun initVM(): SignViewModel = getViewModel()

    override fun initUI() {
        super.initUI()
        setTopIcon(R.drawable.ic_close)
        setTopTitle(R.string.sign_welcome_to_join)

        setTopEndIcon(R.drawable.ic_info) { CRouter.go(AppRouter.appSettingsAbout) }

        mBinding.signUserAgreementTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "agreement") }
        mBinding.signPrivatePolicyTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "policy") }
        mBinding.signByDevicesIdBtn.setOnClickListener {
            if (mBinding.signPrivatePolicyCB.isChecked) {
                mViewModel.signInByDevicesId(devicesId, password)
            } else {
                errorBar(R.string.agreement_policy_hint)
            }
        }
        mBinding.signByPasswordBtn.setOnClickListener { CRouter.go(AppRouter.appSignIn) }

        mBinding.loadingView.visibility = View.INVISIBLE
    }

    override fun initData() {
        devicesId = VMSystem.deviceId()
        password = SignManager.getPrevUser()?.password ?: VMStr.toMD5("123456")
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        mBinding.loadingView.visibility = if (model.isLoading) View.VISIBLE else View.GONE
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "signInByDevicesId" || model.type == "signUpByDevicesId") {
            // 这里直接调用下 IM 的登录，不影响页面的继续
            mViewModel.signInIM()

            CRouter.goMain()
            finish()
        }
    }

    override fun onModelError(model: BViewModel.UIModel) {
        if (model.type == "signInByDevicesId" && model.code == 404) {
            // 账户不存在，去注册一下
            mViewModel.signUpByDevicesId(devicesId, "123456")
        } else {
            super.onModelError(model)
        }
    }
}
