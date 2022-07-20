package com.vmloft.develop.app.template.ui.settings

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivitySettingsPrivacyBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.viewmodel.UserViewModel

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2022/04/08 23:06
 * 描述：隐私设置页
 */
@Route(path = AppRouter.appSettingsPrivacy)
class PrivacySettingsActivity : BVMActivity<ActivitySettingsPrivacyBinding, UserViewModel>() {

    private lateinit var user: User

    override fun initVM(): UserViewModel = getViewModel()

    override fun initVB() = ActivitySettingsPrivacyBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.settings_account_security)

        mBinding.strangerMsgLV.setOnClickListener { changeStrangerMsg() }
        mBinding.blacklistLV.setOnClickListener { CRouter.go(AppRouter.appMineBlacklist) }
    }

    override fun initData() {
        user = SignManager.getCurrUser()

        bindInfo()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "updateInfo") {
            SignManager.setCurrUser(model.data as User)
        }
    }

    /**
     * 切换陌生人私信开关
     */
    private fun changeStrangerMsg() {
        user.strangerMsg = !user.strangerMsg
        bindInfo()

        val params: MutableMap<String, Any> = mutableMapOf()
        params["strangerMsg"] = user.strangerMsg
        mViewModel.updateInfo(params)
    }

    private fun bindInfo() {
        mBinding.strangerMsgLV.isActivated = user.strangerMsg
    }

}