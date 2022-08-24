package com.vmloft.develop.app.template.ui.trade

import android.view.View

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.databinding.ActivityGoldBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.ads.ADSConstants
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.common.DConstants
import com.vmloft.develop.library.data.viewmodel.UserViewModel
import com.vmloft.develop.library.tools.utils.VMDate
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2021/8/11
 * 描述：金币相关界面
 */
@Route(path = AppRouter.appGold)
class GoldActivity : BVMActivity<ActivityGoldBinding, UserViewModel>() {

    private lateinit var user: User

    override fun initVM(): UserViewModel = getViewModel()

    override fun initVB() = ActivityGoldBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.gold_title)
        setTopEndBtnListener(VMStr.byRes(R.string.gold_desc)) { CRouter.go(AppRouter.appGoldDesc) }

        mBinding.goldVipCL.setOnClickListener { CRouter.go(AppRouter.appVipTrade) }
        mBinding.clockTV.setOnClickListener { clock() }

        mBinding.moreRechargeLV.setOnClickListener { CRouter.go(AppRouter.appGoldRecharge) }
        mBinding.moreVideoTaskLV.setOnClickListener { CRouter.go(AppRouter.appRewardVideo, str0 = ADSConstants.ADSIds.videoSceneScoreId) }
        mBinding.morePerfectInfoLV.setOnClickListener { }

        // 监听用户信息变化
        LDEventBus.observe(this, DConstants.Event.userInfo, User::class.java) {
            user = it
            bindInfo()
        }

        // 视频奖励完成，更新下用户信息
        LDEventBus.observe(this, Constants.Event.videoReward, String::class.java) {
            val params = mutableMapOf<String, String>()
            params["userId"] = user.id
            params["rewardAmount"] = "1"
            params["rewardName"] = "score"
            params["time"] = VMDate.currentMilli().toString()

            val signStr = "userId=${params["userId"]}&rewardAmount=${params["rewardAmount"]}&rewardName=${params["rewardName"]}&time=${params["time"]}&secKey=${ADSConstants.secKey()}"
            val sign = VMStr.toMD5(signStr)
            params["sign"] = sign

            mViewModel.videoReward(params)
        }
    }

    override fun initData() {
        user = SignManager.getSignUser()

        bindInfo()

        mViewModel.userInfo()
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        if (model.type == "clock") {
            mBinding.clockTV.isEnabled = !model.isLoading
            mBinding.clockLoadingView.visibility = if (model.isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "clock") {
            mViewModel.userInfo()
        } else if (model.type == "videoReward") {
            mViewModel.userInfo()
        } else if (model.type == "userInfo") {
            SignManager.setSignUser(model.data as User)
        }
    }

    /**
     * 绑定信息
     */
    private fun bindInfo() {
        mBinding.goldCountTV.text = if (user.score > 9999) {
            "${user.score / 10000}万"
        } else {
            user.score.toString()
        }

        mBinding.clockTitleTV.text = VMStr.byResArgs(R.string.gold_clock_count, user.clockTotalCount)

        // 检查是否已签到
        val clockTime = user.clockTime
        val isYesterday = VMDate.isSameDate(clockTime, System.currentTimeMillis() - CConstants.timeDay)
        val isToday = VMDate.isSameDate(clockTime, System.currentTimeMillis())

        val continuousCount = if (!isToday && !isYesterday) 0 else user.clockContinuousCount
        mBinding.continuousClockCountTV.text = VMStr.byResArgs(R.string.gold_clock_continuous_count, continuousCount)

        mBinding.clockTV.setText(if (isToday) R.string.gold_clock_complete else R.string.gold_clock)
        mBinding.clockTV.isEnabled = !isToday


        mBinding.continuousClockLL1.isSelected = continuousCount > 0
        mBinding.continuousClockLL2.isSelected = continuousCount > 1
        mBinding.continuousClockLL3.isSelected = continuousCount > 2
        mBinding.continuousClockLL4.isSelected = continuousCount > 3
        mBinding.continuousClockLL5.isSelected = continuousCount > 4
        mBinding.continuousClockLL6.isSelected = continuousCount > 5
        mBinding.continuousClockLL7.isSelected = continuousCount > 6

        mBinding.continuousClockTV7.text = "7天"
        mBinding.continuousClockIV1.setImageResource(if (continuousCount > 0) R.drawable.ic_done else R.drawable.ic_gold)
        mBinding.continuousClockIV2.setImageResource(if (continuousCount > 1) R.drawable.ic_done else R.drawable.ic_gold)
        mBinding.continuousClockIV3.setImageResource(if (continuousCount > 2) R.drawable.ic_done else R.drawable.ic_gold)
        mBinding.continuousClockIV4.setImageResource(if (continuousCount > 3) R.drawable.ic_done else R.drawable.ic_gold)
        mBinding.continuousClockIV5.setImageResource(if (continuousCount > 4) R.drawable.ic_done else R.drawable.ic_gold)
        mBinding.continuousClockIV6.setImageResource(if (continuousCount > 5) R.drawable.ic_done else R.drawable.ic_gold)
        mBinding.continuousClockIV7.setImageResource(if (continuousCount > 6) R.drawable.ic_done else R.drawable.ic_gold)

        if (user.clockContinuousCount > 6) {
            mBinding.continuousClockTV6.text = "..."
            mBinding.continuousClockTV7.text = "${user.clockContinuousCount + 1}天"
        }

        mBinding.roleTV.text = if (user.role.identity in 100..199) VMStr.byRes(R.string.gold_role_vip) else VMStr.byRes(R.string.gold_role)
        mBinding.roleTipsTV.text = if (user.role.identity in 100..199) VMStr.byRes(R.string.gold_role_vip_date) + FormatUtils.defaultTime(user.roleTime) else VMStr.byRes(R.string.gold_role_hint)

        // 激励视频广告入口显示
        if (ConfigManager.appConfig.adsConfig.goldEntry) {
            mBinding.moreVideoTaskLV.visibility = View.VISIBLE
        } else {
            mBinding.moreVideoTaskLV.visibility = View.GONE
        }

        // 根据配置控制入口展示
        mBinding.moreRechargeLV.visibility = if (ConfigManager.appConfig.tradeConfig.tradeEntry && user.role.identity > 8) View.VISIBLE else View.GONE
        mBinding.goldVipCL.visibility = if (ConfigManager.appConfig.tradeConfig.vipEntry) View.VISIBLE else View.GONE
    }

    /**
     * 签到
     */
    private fun clock() {
        mViewModel.clock()
    }
}