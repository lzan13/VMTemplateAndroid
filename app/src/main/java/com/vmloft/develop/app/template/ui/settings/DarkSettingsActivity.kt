package com.vmloft.develop.app.template.ui.settings

import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.alibaba.android.arouter.facade.annotation.Route
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SPManager
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BaseActivity
import kotlinx.android.synthetic.main.activity_settings_dark.*

/**
 * Create by lzan13 on 2020/05/02 22:56
 * 描述：主题设置
 */
@Route(path = AppRouter.appSettingsDark)
class DarkSettingsActivity : BaseActivity() {

    override fun layoutId(): Int = R.layout.activity_settings_dark

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.settings_dark)

        darkSystemSwitchLV.setOnClickListener {
            darkSystemSwitchLV.isActivated = !darkSystemSwitchLV.isActivated
            darkManualLL.visibility = if (SPManager.isDarkModeSystemSwitch()) View.VISIBLE else View.GONE

            SPManager.setDarkModeSystemSwitch(darkSystemSwitchLV.isActivated)
        }

        darkManualNormalLV.setOnClickListener {
            SPManager.setDarkModeManual(AppCompatDelegate.MODE_NIGHT_NO)
            darkManualNormalLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_NO
            darkManualDarkLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_YES
        }
        darkManualDarkLV.setOnClickListener {
            SPManager.setDarkModeManual(AppCompatDelegate.MODE_NIGHT_YES)
            darkManualNormalLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_NO
            darkManualDarkLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_YES
        }
    }

    override fun initData() {
        // 获取开关状态
        darkSystemSwitchLV.isActivated = SPManager.isDarkModeSystemSwitch()

        darkManualLL.visibility = if (SPManager.isDarkModeSystemSwitch()) View.GONE else View.VISIBLE

        darkManualNormalLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_NO
        darkManualDarkLV.isActivated = SPManager.getDarkModeManual() == AppCompatDelegate.MODE_NIGHT_YES
    }

}