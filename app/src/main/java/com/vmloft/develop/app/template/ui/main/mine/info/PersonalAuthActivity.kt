package com.vmloft.develop.app.template.ui.main.mine.info

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivityPersonalAuthBinding
import com.vmloft.develop.app.template.request.viewmodel.UserViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/08/03 22:56
 * 描述：个人实名认证
 */
@Route(path = AppRouter.appPersonalAuth)
class PersonalAuthActivity : BVMActivity<ActivityPersonalAuthBinding, UserViewModel>() {

    var realName: String? = null
    var idCardNumber: String? = null

    override fun initVM(): UserViewModel = getViewModel()

    override fun initVB() = ActivityPersonalAuthBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.personal_auth)

        setTopEndBtnEnable(false)
        setTopEndBtnListener(VMStr.byRes(R.string.btn_submit)) { submit() }
        mBinding.authRealNameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                realName = s.toString().trim()
                verifyInputBox()
            }
        })
        mBinding.authIDCardNumberET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                idCardNumber = s.toString().trim()
                verifyInputBox()
            }
        })
    }

    override fun initData() {
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        finish()
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() {
        // 检查输入内容格式
        setTopEndBtnEnable(!TextUtils.isEmpty(realName) && !TextUtils.isEmpty(idCardNumber))
    }

    /**
     * 提交数据
     */
    private fun submit() {
        if (!VMReg.isCommonReg(realName, "^[\\s\\S]{1,8}$")) {
            return errorBar(R.string.auth_real_name_error)
        }
        if (!VMReg.isCommonReg(idCardNumber, "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]\$")) {
            return errorBar(R.string.auth_id_card_number_error)
        }

        mViewModel.personalAuth(realName!!, idCardNumber!!)
    }
}