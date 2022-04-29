package com.vmloft.develop.app.template.ui.trade

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityGoldBinding
import com.vmloft.develop.app.template.request.bean.Commodity
import com.vmloft.develop.app.template.request.bean.Order
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.request.viewmodel.UserViewModel
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.common.config.ConfigManager
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

        mBinding.goldVipCL.setOnClickListener { CRouter.go(AppRouter.appGoldVIP) }
        mBinding.clockTV.setOnClickListener { clock() }

        mBinding.moreVideoTaskLV.setOnClickListener { CRouter.go(AppRouter.appGoldTask) }
        mBinding.morePerfectInfoLV.setOnClickListener { }
        mBinding.moreRechargeLV.setOnClickListener { CRouter.go(AppRouter.appGoldRecharge) }

        // 监听用户信息变化
        LDEventBus.observe(this, Constants.Event.userInfo, User::class.java, {
            user = it
            bindInfo()
        })

        // 金币任务完成，更新下用户信息
        LDEventBus.observe(this, Constants.Event.goldTaskReward, Int::class.java) {
            user.score += 50
            mViewModel.userInfo()
        }
    }

    override fun initData() {
        user = SignManager.getCurrUser() ?: User()

        bindInfo()

        mViewModel.userInfo()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "clock") {
            mViewModel.userInfo()
        } else if (model.type == "userInfo") {
            SignManager.setCurrUser(model.data as User)
        }
    }

    /**
     * 绑定信息
     */
    private fun bindInfo() {
        mBinding.goldCountTV.text = user.score.toString()

        mBinding.clockTitleTV.text = VMStr.byResArgs(R.string.gold_clock_count, user.clockTotalCount)

        // 检查是否已签到
        val clockTime = VMDate.utc2Long(user.clockTime)
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
        mBinding.roleTipsTV.text = if (user.role.identity in 100..199) VMStr.byRes(R.string.gold_role_vip_date) + FormatUtils.defaultTime(user.roleDate) else VMStr.byRes(R.string.gold_role_hint)

        if (ConfigManager.clientConfig.adsEntry.goldEntry) {
            mBinding.moreVideoTaskLV.visibility = View.VISIBLE
        } else {
            mBinding.moreVideoTaskLV.visibility = View.GONE
        }
    }

    /**
     * 签到
     */
    private fun clock() {
        mViewModel.clock()
    }
}