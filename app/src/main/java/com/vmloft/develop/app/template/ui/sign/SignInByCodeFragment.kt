package com.vmloft.develop.app.template.ui.sign


import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.FragmentSignInByCodeBinding
import com.vmloft.develop.app.template.request.viewmodel.SignViewModel
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMFragment
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.errorBar
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
        mBinding.accountET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                mPhone = s.toString().trim()
                verifyInputBox()
            }
        })
        mBinding.codeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                mCode = s.toString().trim()
                verifyInputBox()
            }
        })

        mBinding.codeBtn.setOnClickListener {
            mViewModel.requestCodeBySMS(mPhone)
        }

        mBinding.userAgreementTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "agreement") }
        mBinding.privatePolicyTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "policy") }
        mBinding.submitTV.setOnClickListener {
            if (mBinding.privatePolicyCB.isChecked) {
                mViewModel.signInByCode(mPhone, mCode)
            } else {
                errorBar(R.string.agreement_policy_hint)
            }
        }
    }

    override fun initData() {

    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        mBinding.submitTV.isEnabled = !model.isLoading
        mBinding.loadingView.visibility = if (model.isLoading) View.VISIBLE else View.GONE
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "requestCodeBySMS") {
            // 请求验证码成功
            mBinding.codeBtn.startTimer()
        }else if (model.type == "signInBySMS") {
            // 这里直接调用下 IM 的登录，不影响页面的继续
            mViewModel.signInIM()
        } else if (model.type == "signInIM") {
            // 登录成功，跳转到主页
            CRouter.goMain()
            LDEventBus.post(Constants.Event.finishPrev)
            activity?.finish()
        }
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() {
        mBinding.codeBtn.isEnabled = VMReg.isSimpleMobileNumber(mPhone)
        mBinding.submitTV.isEnabled = VMReg.isSimpleMobileNumber(mPhone) && VMReg.isCommonReg(mCode, "^[0-9]{6}\$")
    }
}
