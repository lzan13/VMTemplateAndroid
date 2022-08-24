package com.vmloft.develop.app.template.ui.main.mine.info

import android.text.Editable
import android.text.TextWatcher
import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivityPersonalInfoGuideBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.PermissionManager
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.viewmodel.UserViewModel
import com.vmloft.develop.library.image.IMGChoose
import com.vmloft.develop.library.image.IMGLoader
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

    private var nickname: String = ""
    private var gender: Int = 2

    override fun initVB() = ActivityPersonalInfoGuideBinding.inflate(layoutInflater)

    override fun initVM(): UserViewModel = getViewModel()

    override fun initUI() {
        super.initUI()

        setTopIcon(0)
        setTopEndBtnListener(VMStr.byRes(R.string.btn_skip)) { finish() }

        mBinding.avatarIV.setOnClickListener { chooseAvatar() }
        mBinding.genderManIV.setOnClickListener { chooseGender(1) }
        mBinding.genderWomanIV.setOnClickListener { chooseGender(0) }

        mBinding.nicknameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                nickname = s.toString().trim()
                verifyInfo()
            }
        })
        mBinding.submitTV.setOnClickListener { submit() }
    }

    override fun initData() {
        mUser = SignManager.getSignUser()

        bindInfo()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "updateInfo") {
            SignManager.setSignUser(model.data as User)
            finish()
        }
        if (model.type == "updateAvatar") {
            mUser = model.data as User
            SignManager.setSignUser(mUser)
            IMGLoader.loadAvatar(mBinding.avatarIV, mUser.avatar)
        }
    }

    private fun bindInfo() {
        IMGLoader.loadAvatar(mBinding.avatarIV, mUser.avatar)
        chooseGender(mUser.gender)
        mBinding.nicknameET.setText(mUser.nickname)
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
     * 选择性别
     */
    private fun chooseGender(value: Int) {
        gender = value
        mBinding.genderManIV.isSelected = gender == 1
        mBinding.genderWomanIV.isSelected = gender == 0
        verifyInfo()
    }

    /**
     * 校验用户信息
     */
    private fun verifyInfo() {
        // 检查输入框是否为空
        mBinding.submitTV.isEnabled = nickname.isNotEmpty() && gender != 2 && mUser.avatar.isNotEmpty()

    }

    /**
     * 提交
     */
    private fun submit() {
        if (!VMReg.isCommonReg(nickname, "^[\\s\\S]{1,12}$")) {
            return errorBar(R.string.info_nickname_tips)
        }
        val params = mutableMapOf<String, Any>()
        params["nickname"] = nickname
        params["gender"] = gender
        mViewModel.updateInfo(params)
    }
}