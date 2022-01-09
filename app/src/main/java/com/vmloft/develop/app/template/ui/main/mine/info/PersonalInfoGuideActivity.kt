package com.vmloft.develop.app.template.ui.main.mine.info

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.library.common.common.PermissionManager
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityPersonalInfoGuideBinding
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.viewmodel.UserViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.image.IMGChoose
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/11/15 21:56
 * 描述：个人信息编辑引导界面
 */
@Route(path = AppRouter.appPersonalInfoGuide)
class PersonalInfoGuideActivity : BVMActivity<ActivityPersonalInfoGuideBinding, UserViewModel>() {

    private lateinit var mUser: User

    private var nickname: String? = null

    override fun initVB() = ActivityPersonalInfoGuideBinding.inflate(layoutInflater)

    override fun initVM(): UserViewModel = getViewModel()

    override fun initUI() {
        super.initUI()

        setTopIcon(0)
        setTopEndBtnListener(VMStr.byRes(R.string.btn_skip)) { finish() }

        mBinding.avatarIV.setOnClickListener { chooseAvatar() }
        mBinding.nicknameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                nickname = s.toString().trim()
                verifyInputBox()
            }
        })
        mBinding.submitTV.setOnClickListener { submit() }
    }

    override fun initData() {
        mUser = SignManager.getCurrUser() ?: return finish()

        IMGLoader.loadAvatar(mBinding.avatarIV, mUser.avatar)
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "updateInfo") {
            SignManager.setCurrUser(model.data as User)
            finish()
        }
        if (model.type == "updateAvatar") {
            SignManager.setCurrUser(model.data as User)
        }
    }

    /**
     * 选择头像
     */
    private fun chooseAvatar() {
        if (!PermissionManager.storagePermission(this)) return
        IMGChoose.singleCrop(this) { result ->
            mViewModel.updateAvatar(result)
        }
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() { // 将用户名转为消息并修剪
        // 检查输入框是否为空
        mBinding.submitTV.isEnabled = !TextUtils.isEmpty(nickname)
    }

    /**
     * 提交
     */
    private fun submit() {
        if (!VMReg.isCommonReg(nickname, "^[\\s\\S]{1,12}$")) {
            return errorBar(R.string.info_nickname_tips)
        }
        val params = mutableMapOf<String, String>()
        params["nickname"] = nickname!!
        mViewModel.updateInfo(params)
    }
}