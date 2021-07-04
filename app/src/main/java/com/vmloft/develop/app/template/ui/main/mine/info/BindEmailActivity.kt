package com.vmloft.develop.app.template.ui.main.mine.info

import android.text.Editable
import android.text.TextWatcher

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityBindEmailBinding
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg
import com.vmloft.develop.library.tools.utils.VMStr

import kotlinx.android.synthetic.main.activity_bind_email.*

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2021/07/03 22:56
 * 描述：绑定邮箱
 */
@Route(path = AppRouter.appBindEmail)
class BindEmailActivity : BVMActivity<InfoViewModel>() {

    private var email: String = ""
    private var code: String = ""

    override fun initVM(): InfoViewModel = getViewModel()

    override fun layoutId(): Int = R.layout.activity_bind_email

    override fun initUI() {
        super.initUI()
        (mBinding as ActivityBindEmailBinding).viewModel = mViewModel

        setTopTitle(R.string.info_bind_email)

        setTopEndBtnEnable(false)
        setTopEndBtnListener(VMStr.byRes(R.string.btn_submit)) { submit() }
        bindEmailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                email = s.toString().trim()
                verifyInputBox()
            }
        })
        bindCodeBtn.setOnClickListener { sendCodeEmail() }
        bindCodeET.addTextChangedListener(object : TextWatcher {
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
            bindCodeBtn.startTimer()
        } else if (model.type == "bindEmail") {
            SignManager.instance.setCurrUser(model.data as User)
            finish()
        }
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() {
        // 检查邮箱地址输入框
//        bindCodeBtn.isEnabled = VMReg.isEmail(email)

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