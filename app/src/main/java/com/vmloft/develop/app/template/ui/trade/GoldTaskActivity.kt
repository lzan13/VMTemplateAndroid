package com.vmloft.develop.app.template.ui.trade

import android.view.View

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityGoldTaskBinding
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.ads.ADSConstants
import com.vmloft.develop.library.ads.ADSManager
import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMSystem

/**
 * Create by lzan13 on 2021/8/11
 * 描述：金币相关界面
 */
@Route(path = AppRouter.appGoldTask)
class GoldTaskActivity : BActivity<ActivityGoldTaskBinding>() {
    private lateinit var user: User

    override fun initVB() = ActivityGoldTaskBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
    }

    override fun initData() {
        user = SignManager.getCurrUser() ?: User()

        loadAD()
    }

    /**
     * 加载广告
     */
    private fun loadAD() {
        mBinding.loadingInclude.commonLoadingLL.visibility = View.VISIBLE
        // 组装用户信息，主要是用户Id
        val params = mutableMapOf<String, String>()
        params["user_id"] = user.id
        params["user_custom_data"] = "userData"

        ADSManager.loadVideoAD(this, params) { status ->
            mBinding.loadingInclude.commonLoadingLL.visibility = View.GONE
            when (status) {
                ADSConstants.Status.loaded -> showAD()
                ADSConstants.Status.failed -> loadADFailed()
                ADSConstants.Status.playFailed -> loadADFailed()
                ADSConstants.Status.reward -> rewardAD()
                ADSConstants.Status.close -> closeAD()
            }
        }
    }

    /**
     * 显示广告
     */
    private fun showAD() {
        if (isFinishing) return
        ADSManager.showVideoAD(this)
    }

    /**
     * 广告加载失败
     */
    private fun loadADFailed() {
        errorBar(R.string.no_ads_video_data)

        VMSystem.runInUIThread({
            finish()
        }, 1000)
    }

    /**
     * 广告奖励，先执行
     */
    private fun rewardAD() {
        LDEventBus.post(Constants.Event.goldTaskReward, 0)
    }


    /**
     * 广告关闭，后执行
     */
    private fun closeAD() {
        ADSManager.closeVideoAD()

        finish()
    }


}