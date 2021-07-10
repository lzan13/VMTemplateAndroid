package com.vmloft.develop.app.template.ui.sign

import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.FragmentSignInByPasswordBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMFragment
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg
import com.vmloft.develop.library.tools.utils.VMStr
import kotlinx.android.synthetic.main.fragment_sign_in_by_code.*

import kotlinx.android.synthetic.main.fragment_sign_in_by_password.*
import kotlinx.android.synthetic.main.fragment_sign_in_by_password.signAccountET
import kotlinx.android.synthetic.main.fragment_sign_in_by_password.signPrivacyPolicyCB
import kotlinx.android.synthetic.main.fragment_sign_in_by_password.signSubmitBtn

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 2020/12/12
 * 通过密码登录
 */
class SignInByPasswordFragment : BVMFragment<SignViewModel>() {

    private var mAccount: String = ""
    private var mPassword: String = ""

    override fun initVM(): SignViewModel = getViewModel()

    override fun layoutId(): Int = R.layout.fragment_sign_in_by_password

    override fun initUI() {
        super.initUI()
        (mBinding as FragmentSignInByPasswordBinding).viewModel = mViewModel

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
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

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

        signSubmitBtn.setOnClickListener {
            if (signPrivacyPolicyCB.isChecked) {
                mViewModel.signIn(mAccount, mPassword)
            } else {
                errorBar(R.string.sign_privacy_policy_hint)
            }
        }
    }

    override fun initData() {
        val user = SignManager.getPrevUser()
        if (user?.username.isNullOrEmpty()) {
            signAccountET.setText(user?.username)
        } else if (user?.phone.isNullOrEmpty()) {
            signAccountET.setText(user?.phone)
        } else if (user?.email.isNullOrEmpty()) {
            signAccountET.setText(user?.email)
        }
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
        signSubmitBtn.isEnabled = VMReg.isCommonReg(mAccount, "^[0-9a-zA-Z_.@]{6,16}$") && mPassword.isNotEmpty()
    }
}