package com.vmloft.develop.app.template.ui.sign


import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivitySignUpBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg

import kotlinx.android.synthetic.main.activity_sign_up.*

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2020/6/19 17:10
 * 描述：注册界面
 */
@Route(path = AppRouter.appSignUp)
class SignUpActivity : BVMActivity<SignViewModel>() {

    private var mAccount: String = ""
    private var mPassword: String = ""

    override fun initVM(): SignViewModel = getViewModel()

    override fun layoutId(): Int = R.layout.activity_sign_up

    override fun initUI() {
        super.initUI()
        (mBinding as ActivitySignUpBinding).viewModel = mViewModel
        setTopTitle(R.string.sign_up)

        // 监听输入框变化
        signAccountET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                mAccount = s.toString().trim()
                verifyInputBox()
            }
        })
        signPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                mPassword = s.toString().trim()
                verifyInputBox()
            }
        })

        // 设置密码隐藏与显示
        signPasswordIcon.setOnClickListener {
            if (signPasswordIcon.isSelected) {
                // 隐藏密码
                signPasswordET.transformationMethod = PasswordTransformationMethod.getInstance()
                signPasswordIcon.isSelected = false
            } else {
                // 显示密码
                signPasswordET.transformationMethod = HideReturnsTransformationMethod.getInstance()
                signPasswordIcon.isSelected = true
            }
        }
        signUserAgreementTV.setOnClickListener { AppRouter.goAgreementPolicy() }
        signPrivatePolicyTV.setOnClickListener { AppRouter.goAgreementPolicy("policy") }
        signSubmitBtn.setOnClickListener {
            if (signPrivatePolicyCB.isChecked) {
                if (VMReg.isEmail(mAccount)) {
                    mViewModel.signUpByEmail(mAccount, mPassword)
                } else {
                    // TODO 只能使用邮箱注册
                    errorBar("只能使用邮箱注册账户")
                }
                // 屏蔽手机号注册
//                if (VMReg.isSimpleMobileNumber(mAccount)) {
//                    mViewModel.signUpByPhone(mAccount, mPassword)
//                }
            } else {
                errorBar(R.string.agreement_policy_hint)
            }
        }
    }

    override fun initData() {

    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "signUpByPhone") {
            // 注册成功，跳转到主页
            CRouter.goMain()
            finish()
        }
        if (model.type == "signUpByEmail") {
            // 注册成功，跳转到主页
            CRouter.goMain()
            finish()
        }
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() {
        signSubmitBtn.isEnabled = VMReg.isEmail(mAccount) && mPassword.isNotEmpty()
    }

}
