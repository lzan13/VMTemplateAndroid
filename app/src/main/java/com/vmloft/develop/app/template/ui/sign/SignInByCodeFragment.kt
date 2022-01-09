package com.vmloft.develop.app.template.ui.sign


import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.FragmentSignInByCodeBinding
import com.vmloft.develop.app.template.request.viewmodel.SignViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMFragment
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2020/12/29 17:10
 * 描述：验证码登录界面
 */
class SignInByCodeFragment : BVMFragment<FragmentSignInByCodeBinding, SignViewModel>() {

    private var mPhone: String = ""
    private var mCode: String = ""

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = FragmentSignInByCodeBinding.inflate(inflater, parent, false)

    override fun initVM(): SignViewModel = getViewModel()

    override fun initUI() {
        super.initUI()
        // 监听输入框变化
        mBinding.signAccountET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                mPhone = s.toString().trim()
                verifyInputBox()
            }
        })
        mBinding.signCodeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                mCode = s.toString().trim()
                verifyInputBox()
            }
        })

        mBinding.signCodeBtn.setOnClickListener {
            mViewModel.requestCodeBySMS(mPhone)
        }

        mBinding.signUserAgreementTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "agreement") }
        mBinding.signPrivatePolicyTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "policy") }
        mBinding.signSubmitBtn.setOnClickListener {
            if (mBinding.signPrivatePolicyCB.isChecked) {
                mViewModel.signInByCode(mPhone, mCode)
            } else {
                errorBar(R.string.agreement_policy_hint)
            }
        }
    }

    override fun initData() {

    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "requestCodeBySMS") {
            // 请求验证码成功
            mBinding.signCodeBtn.startTimer()
        }
        if (model.type == "signInBySMS") {
            // 登录成功
            activity?.finish()
        }
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() {
        mBinding.signCodeBtn.isEnabled = VMReg.isSimpleMobileNumber(mPhone)
        mBinding.signSubmitBtn.isEnabled = VMReg.isSimpleMobileNumber(mPhone) && VMReg.isCommonReg(mCode, "^[0-9]{6}\$")
    }
}
