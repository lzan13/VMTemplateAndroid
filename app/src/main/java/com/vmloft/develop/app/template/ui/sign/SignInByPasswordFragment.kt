package com.vmloft.develop.app.template.ui.sign

import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.FragmentSignInByPasswordBinding
import com.vmloft.develop.app.template.request.viewmodel.SignViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMFragment
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 2020/12/12
 * 通过密码登录
 */
class SignInByPasswordFragment : BVMFragment<FragmentSignInByPasswordBinding, SignViewModel>() {

    private var mAccount: String = ""
    private var mPassword: String = ""

    override fun initVM(): SignViewModel = getViewModel()

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = FragmentSignInByPasswordBinding.inflate(inflater, parent, false)

    override fun initUI() {
        super.initUI()
        // 监听输入框变化
        mBinding.signAccountET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                mAccount = s.toString().trim()
                verifyInputBox()
            }
        })
        mBinding.signPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                mPassword = s.toString().trim()
                verifyInputBox()
            }
        })
        // 设置密码隐藏与显示
        mBinding.signPasswordIcon.setOnClickListener {
            if (mBinding.signPasswordIcon.isSelected) {
                // 隐藏密码
                mBinding.signPasswordET.transformationMethod = PasswordTransformationMethod.getInstance()
                mBinding.signPasswordIcon.isSelected = false
            } else {
                // 显示密码
                mBinding.signPasswordET.transformationMethod = HideReturnsTransformationMethod.getInstance()
                mBinding.signPasswordIcon.isSelected = true
            }
        }

        mBinding.signUserAgreementTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "agreement") }
        mBinding.signPrivatePolicyTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "policy") }
        mBinding.signSubmitBtn.setOnClickListener {
            if (mBinding.signPrivatePolicyCB.isChecked) {
                mViewModel.signIn(mAccount, mPassword)
            } else {
                errorBar(R.string.agreement_policy_hint)
            }
        }
    }

    override fun initData() {
        val user = SignManager.getPrevUser()
        if (user?.username.isNullOrEmpty()) {
            mBinding.signAccountET.setText(user?.username)
        } else if (user?.phone.isNullOrEmpty()) {
            mBinding.signAccountET.setText(user?.phone)
        } else if (user?.email.isNullOrEmpty()) {
            mBinding.signAccountET.setText(user?.email)
        }
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        mBinding.loadingView.visibility = if (model.isLoading) View.VISIBLE else View.GONE
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "signIn") {
            // 登录成功，跳转到主页
            CRouter.goMain()
            activity?.finish()
        }
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() {
        // 检查输入框
        mBinding.signSubmitBtn.isEnabled = VMReg.isCommonReg(mAccount, "^[0-9a-zA-Z_.@]{6,16}$") && mPassword.isNotEmpty()
    }
}