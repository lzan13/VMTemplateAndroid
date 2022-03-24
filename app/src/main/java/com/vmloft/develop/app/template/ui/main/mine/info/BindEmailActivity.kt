package com.vmloft.develop.app.template.ui.main.mine.info

import android.text.Editable
import android.text.TextWatcher

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityBindEmailBinding
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.viewmodel.UserViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2021/07/03 22:56
 * 描述：绑定邮箱
 */
@Route(path = AppRouter.appBindEmail)
class BindEmailActivity : BVMActivity<ActivityBindEmailBinding, UserViewModel>() {

    private var email: String = ""
    private var code: String = ""

    override fun initVM(): UserViewModel = getViewModel()

    override fun initVB() = ActivityBindEmailBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.info_bind_email)

        setTopEndBtnEnable(false)
        setTopEndBtnListener(VMStr.byRes(R.string.btn_submit)) { submit() }
        mBinding.bindEmailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                email = s.toString().trim()
                verifyInputBox()
            }
        })
       mBinding.bindCodeBtn.setOnClickListener { sendCodeEmail() }
       mBinding.bindCodeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                code = s.toString().trim()
                verifyInputBox()
            }
        })
    }

    override fun initData() {

    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "sendCodeEmail") {
            mBinding.bindCodeBtn.startTimer()
        } else if (model.type == "bindEmail") {
            SignManager.setCurrUser(model.data as User)
            finish()
        }
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() {
        // 检查整体输入框是否为空
        setTopEndBtnEnable(VMReg.isEmail(email) && !code.isNullOrEmpty())
    }

    /**
     * 请求邮箱验证码
     */
    private fun sendCodeEmail() {
        if (!VMReg.isEmail(email)) {
            return errorBar(R.string.sign_email_hint)
        }
        mViewModel.sendCodeEmail(email)
    }

    /**
     * 提交
     */
    private fun submit() {
        mViewModel.bindEmail(email, code)
    }
}