package com.vmloft.develop.app.template.ui.main.mine.info

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityPersonalInfoEditBinding
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.viewmodel.UserViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/08/03 22:56
 * 描述：编辑签名
 */
@Route(path = AppRouter.appEditSignature)
class EditSignatureActivity : BVMActivity<ActivityPersonalInfoEditBinding, UserViewModel>() {

    @Autowired(name = CRouter.paramsStr0)
    lateinit var signature: String

    override fun initVB() = ActivityPersonalInfoEditBinding.inflate(layoutInflater)

    override fun initVM(): UserViewModel = getViewModel()

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.info_signature)

        setTopEndBtnEnable(false)
        setTopEndBtnListener(VMStr.byRes(R.string.btn_confirm)) { save() }

        mBinding.infoSingleET.visibility = View.GONE
        mBinding.infoMultiLineET.visibility = View.VISIBLE
        mBinding.infoMultiLineET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                signature = s.toString().trim()
                verifyInputBox()
            }
        })
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        mBinding.infoMultiLineET.setText(signature)
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
        setTopEndBtnEnable(!TextUtils.isEmpty(signature))
    }

    /**
     * 保存
     */
    private fun save() {
        if (!VMReg.isCommonReg(signature, "^[\\s\\S]{2,32}$")) {
            return errorBar("签名长度必须在 2-32 之间")
        }
        val params: MutableMap<String, Any> = mutableMapOf()
        params["signature"] = signature
        mViewModel.updateInfo(params)
    }
}