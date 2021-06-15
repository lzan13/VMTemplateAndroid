package com.vmloft.develop.app.template.ui.settings

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BaseActivity
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.common.CSPManager
import com.vmloft.develop.library.common.notify.NotifyManager
import com.vmloft.develop.library.tools.utils.VMFile
import kotlinx.android.synthetic.main.activity_settings_notify.*


/**
 * Create by lzan13 on 2020/06/07 21:06
 * 描述：设置
 */
@Route(path = AppRouter.appSettingsNotify)
class NotifySettingsActivity : BaseActivity() {
    // 缓存地址
    private val cachePath = "${VMFile.cacheFromSDCard}${CConstants.cacheImageDir}"


    override fun layoutId(): Int = R.layout.activity_settings_notify

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.settings_notify)

        // 通知开关
        notifyMsgLV.setOnClickListener {
            notifyMsgLV.isActivated = !notifyMsgLV.isActivated
            notifyMsgSystemLL.visibility = if (notifyMsgLV.isActivated) View.VISIBLE else View.GONE
            CSPManager.instance.setNotifyMsgSwitch(notifyMsgLV.isActivated)
        }
        notifyMsgDetailLV.setOnClickListener {
            notifyMsgDetailLV.isActivated = !notifyMsgDetailLV.isActivated
            notifyMsgSystemLL.visibility = if (notifyMsgLV.isActivated) View.VISIBLE else View.GONE
            CSPManager.instance.setNotifyMsgDetailSwitch(notifyMsgDetailLV.isActivated)
        }

        // 打开设置
        notifyMsgSystemLV.setOnClickListener { NotifyManager.instance.openNotifySetting() }

    }

    override fun initData() {
        notifyMsgLV.isActivated = CSPManager.instance.isNotifyMsgSwitch()
        notifyMsgDetailLV.isActivated = CSPManager.instance.isNotifyMsgDetailSwitch()
        notifyMsgSystemLL.visibility = if (notifyMsgLV.isActivated) View.VISIBLE else View.GONE
    }

}