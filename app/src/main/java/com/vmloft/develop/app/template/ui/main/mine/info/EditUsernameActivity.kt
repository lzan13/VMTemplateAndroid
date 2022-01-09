package com.vmloft.develop.app.template.ui.main.mine.info

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityPersonalInfoEditBinding
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.viewmodel.UserViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/08/03 22:56
 * 描述：编辑用户名
 */
@Route(path = AppRouter.appEditUsername)
class EditUsernameActivity : BVMActivity<ActivityPersonalInfoEditBinding, UserViewModel>() {

    private var username: String? = null

    override fun initVB() = ActivityPersonalInfoEditBinding.inflate(layoutInflater)

    override fun initVM(): UserViewModel = getViewModel()

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.info_username)

        setTopEndBtnEnable(false)
        setTopEndBtnListener(VMStr.byRes(R.string.btn_save)) { save() }
        mBinding.infoSingleET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                username = s.toString().trim()
                verifyInputBox()
            }
        })
        mBinding.infoDescTV.text = VMStr.byRes(R.string.info_username_tips)
    }

    override fun initData() {

    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        SignManager.setCurrUser(model.data as User)
        finish()
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() { // 将用户名转为消息并修剪
        // 检查输入框是否为空
        setTopEndBtnEnable(!TextUtils.isEmpty(username))
    }

    /**
     * 保存用户名
     */
    private fun save() {
        if (!VMReg.isCommonReg(username, "^[a-zA-Z0-9_.@]{4,16}\$")) {
            return errorBar(R.string.info_username_tips)
        }
        mViewModel.updateUsername(username!!)
    }
}