package com.vmloft.develop.app.template.ui.sign


import android.text.Editable
import android.text.TextWatcher
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.FragmentSignInByCodeBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMFragment
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg

import kotlinx.android.synthetic.main.fragment_sign_in_by_code.*
import kotlinx.android.synthetic.main.fragment_sign_in_by_code.signAccountET
import kotlinx.android.synthetic.main.fragment_sign_in_by_code.signPrivacyPolicyCB
import kotlinx.android.synthetic.main.fragment_sign_in_by_code.signPrivacyPolicyTV
import kotlinx.android.synthetic.main.fragment_sign_in_by_code.signSubmitBtn
import kotlinx.android.synthetic.main.fragment_sign_in_by_code.signUserAgreementTV
import kotlinx.android.synthetic.main.fragment_sign_in_by_password.*

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2020/12/29 17:10
 * 描述：验证码登录界面
 */
class SignInByCodeFragment : BVMFragment<SignViewModel>() {

    private lateinit var mPhone: String
    private lateinit var mCode: String

    override fun initVM(): SignViewModel = getViewModel()

    override fun layoutId(): Int = R.layout.fragment_sign_in_by_code

    override fun initUI() {
        super.initUI()
        (mBinding as FragmentSignInByCodeBinding).viewModel = mViewModel

        // 监听输入框变化
        signAccountET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                mPhone = s.toString().trim()
                verifyInputBox()
            }
        })
        signCodeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                mCode = s.toString().trim()
                verifyInputBox()
            }
        })

        signCodeBtn.setOnClickListener {
            mViewModel.requestCodeBySMS(mPhone)
        }

        signUserAgreementTV.setOnClickListener { AppRouter.goAgreementPolicy() }
        signPrivacyPolicyTV.setOnClickListener { AppRouter.goAgreementPolicy("policy") }
        signSubmitBtn.setOnClickListener {
            if (signPrivacyPolicyCB.isChecked) {
                mViewModel.signInByCode(mPhone, mCode)
            }else{
                errorBar(R.string.agreement_policy_hint)
            }
        }
    }

    override fun initData() {

    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "requestCodeBySMS") {
            // 请求验证码成功
            signCodeBtn.startTimer()
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
        signCodeBtn.isEnabled = VMReg.isSimpleMobileNumber(mPhone)
        signSubmitBtn.isEnabled = VMReg.isSimpleMobileNumber(mPhone) && VMReg.isCommonReg(mCode, "^[0-9]{6}\$")
    }
}
