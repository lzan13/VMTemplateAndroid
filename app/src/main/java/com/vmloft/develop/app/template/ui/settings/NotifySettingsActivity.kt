package com.vmloft.develop.app.template.ui.settings

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivitySettingsNotifyBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.base.notify.NotifyManager
import com.vmloft.develop.library.tools.utils.VMFile

/**
 * Create by lzan13 on 2020/06/07 21:06
 * 描述：设置
 */
@Route(path = AppRouter.appSettingsNotify)
class NotifySettingsActivity: BActivity<ActivitySettingsNotifyBinding>() {
    // 缓存地址
    private val cachePath = "${VMFile.cachePath}${CConstants.cacheImageDir}"


    override fun initVB() = ActivitySettingsNotifyBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.settings_notify)

        // 通知开关
        mBinding.notifyMsgLV.setOnClickListener {
            mBinding.notifyMsgLV.isActivated = !mBinding.notifyMsgLV.isActivated
            mBinding.notifyMsgSystemLL.visibility = if (mBinding.notifyMsgLV.isActivated) View.VISIBLE else View.GONE
            CSPManager.setNotifyMsgSwitch(mBinding.notifyMsgLV.isActivated)
        }
        mBinding.notifyMsgDetailLV.setOnClickListener {
            mBinding.notifyMsgDetailLV.isActivated = !mBinding.notifyMsgDetailLV.isActivated
            mBinding.notifyMsgSystemLL.visibility = if (mBinding.notifyMsgLV.isActivated) View.VISIBLE else View.GONE
            CSPManager.setNotifyMsgDetailSwitch(mBinding.notifyMsgDetailLV.isActivated)
        }

        // 打开设置
        mBinding.notifyMsgSystemLV.setOnClickListener { NotifyManager.openNotifySetting() }

    }

    override fun initData() {
        mBinding.notifyMsgLV.isActivated = CSPManager.isNotifyMsgSwitch()
        mBinding.notifyMsgDetailLV.isActivated = CSPManager.isNotifyMsgDetailSwitch()
        mBinding.notifyMsgSystemLL.visibility = if (mBinding.notifyMsgLV.isActivated) View.VISIBLE else View.GONE
    }

}