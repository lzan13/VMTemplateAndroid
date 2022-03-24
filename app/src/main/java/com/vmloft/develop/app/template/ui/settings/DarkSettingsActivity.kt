package com.vmloft.develop.app.template.ui.settings

import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SPManager
import com.vmloft.develop.app.template.databinding.ActivitySettingsDarkBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BActivity

/**
 * Create by lzan13 on 2020/05/02 22:56
 * 描述：主题设置
 */
@Route(path = AppRouter.appSettingsDark)
class DarkSettingsActivity: BActivity<ActivitySettingsDarkBinding>() {

    override fun initVB() = ActivitySettingsDarkBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.settings_dark)

        mBinding.darkSystemSwitchLV.setOnClickListener {
            mBinding.darkSystemSwitchLV.isActivated = !mBinding.darkSystemSwitchLV.isActivated
            mBinding.darkManualLL.visibility = if (SPManager.isDarkModeSystemSwitch()) View.VISIBLE else View.GONE

            SPManager.setDarkModeSystemSwitch(mBinding.darkSystemSwitchLV.isActivated)
        }

        mBinding.darkManualNormalLV.setOnClickListener {
            SPManager.setDarkModeManual(AppCompatDelegate.MODE_NIGHT_NO)
            mBinding.darkManualNormalLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_NO
            mBinding.darkManualDarkLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_YES
        }
        mBinding.darkManualDarkLV.setOnClickListener {
            SPManager.setDarkModeManual(AppCompatDelegate.MODE_NIGHT_YES)
            mBinding.darkManualNormalLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_NO
            mBinding.darkManualDarkLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_YES
        }
    }

    override fun initData() {
        // 获取开关状态
        mBinding.darkSystemSwitchLV.isActivated = SPManager.isDarkModeSystemSwitch()

        mBinding.darkManualLL.visibility = if (SPManager.isDarkModeSystemSwitch()) View.GONE else View.VISIBLE

        mBinding.darkManualNormalLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_NO
        mBinding.darkManualDarkLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_YES
    }

}