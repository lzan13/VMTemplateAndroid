package com.vmloft.develop.app.template.ui.sign

import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.app.template.databinding.FragmentSignInByPasswordBinding
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.app.template.request.viewmodel.SignViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMFragment
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.event.LDEventBus
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
        mBinding.accountET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                mAccount = s.toString().trim()
                verifyInputBox()
            }
        })
        mBinding.passwordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                mPassword = s.toString().trim()
                verifyInputBox()
            }
        })
        // 设置密码隐藏与显示
        mBinding.passwordIconIV.setOnClickListener {
            if (mBinding.passwordIconIV.isSelected) {
                // 隐藏密码
                mBinding.passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
                mBinding.passwordIconIV.isSelected = false
            } else {
                // 显示密码
                mBinding.passwordET.transformationMethod = HideReturnsTransformationMethod.getInstance()
                mBinding.passwordIconIV.isSelected = true
            }
        }

        mBinding.userAgreementTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "agreement") }
        mBinding.privatePolicyTV.setOnClickListener { CRouter.go(AppRouter.appSettingsAgreementPolicy, str0 = "policy") }
        mBinding.submitTV.setOnClickListener {
            if (mBinding.privatePolicyCB.isChecked) {
                mViewModel.signIn(mAccount, mPassword)
            } else {
                errorBar(R.string.agreement_policy_hint)
            }
        }
    }

    override fun initData() {
        val user = SignManager.getPrevUser() ?: User()
        if (user.username.isNotEmpty()) {
            mBinding.accountET.setText(user.username)
        } else if (user.email.isNotEmpty()) {
            mBinding.accountET.setText(user.email)
        } else if (user.phone.isNotEmpty()) {
            mBinding.accountET.setText(user.phone)
        }
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        mBinding.submitTV.isEnabled = !model.isLoading
        mBinding.loadingView.visibility = if (model.isLoading) View.VISIBLE else View.GONE
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "signIn") {
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
        // 检查输入框
        mBinding.submitTV.isEnabled = VMReg.isCommonReg(mAccount, "^[0-9a-zA-Z_.@]{6,32}$") && VMReg.isNormalPassword(mPassword)
    }
}