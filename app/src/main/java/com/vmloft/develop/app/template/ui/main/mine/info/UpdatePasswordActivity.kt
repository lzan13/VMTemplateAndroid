package com.vmloft.develop.app.template.ui.main.mine.info

import android.text.Editable
import android.text.TextWatcher
import android.view.View

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityUpdatePasswordBinding
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.viewmodel.UserViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.tools.utils.VMReg
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2022/04/16 22:56
 * 描述：更新密码
 */
@Route(path = AppRouter.appUpdatePassword)
class UpdatePasswordActivity : BVMActivity<ActivityUpdatePasswordBinding, UserViewModel>() {

    private lateinit var user: User

    private var code: String = ""
    private var password: String = ""

    override fun initVM(): UserViewModel = getViewModel()

    override fun initVB() = ActivityUpdatePasswordBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.update_password)

        setTopEndBtnEnable(false)
        setTopEndBtnListener(VMStr.byRes(R.string.btn_confirm)) { submit() }

        mBinding.emailTV.setOnClickListener { if (user.email.isNullOrEmpty()) CRouter.go(AppRouter.appBindEmail) }
        mBinding.codeBtn.setOnClickListener { sendCodeEmail() }

        mBinding.codeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                code = s.toString().trim()
                verifyInputBox()
            }
        })
        mBinding.passwordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                password = s.toString().trim()
                verifyInputBox()
            }
        })
    }

    override fun initData() {
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        if (model.type == "sendCodeEmail") {
            mBinding.loadingView.visibility = if (model.isLoading) View.VISIBLE else View.GONE
            mBinding.codeBtn.isEnabled = false
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "sendCodeEmail") {
            mBinding.codeBtn.startTimer()
        } else if (model.type == "updatePassword") {
            showSignOut()
        }
    }

    /**
     * 密码修改成功弹窗
     */
    private fun showSignOut() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.update_password_success)
            dialog.setPositive(listener = {
                // 清空登录信息统一交给 Main 界面处理
                CRouter.goMain(1)
            })
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        bindInfo()
    }

    private fun bindInfo() {
        user = SignManager.getCurrUser()
        mBinding.emailTV.text = if (user.email.isNullOrEmpty()) {
            VMStr.byRes(R.string.info_bind_email)
        } else {
            user.email
        }
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() {
        // 检查整体输入框是否为空
        setTopEndBtnEnable(VMReg.isNormalPassword(password) && !code.isNullOrEmpty())
    }

    /**
     * 请求邮箱验证码
     */
    private fun sendCodeEmail() {
        if (!VMReg.isEmail(user.email)) {
            return errorBar(R.string.update_password_email_null)
        }
        mViewModel.sendCodeEmail(user.email)
    }

    /**
     * 提交
     */
    private fun submit() {
        if (!VMReg.isNormalPassword(password)) {
            return errorBar(R.string.sign_password_tips)
        }
        mViewModel.updatePassword(password, user.email, code)
    }
}