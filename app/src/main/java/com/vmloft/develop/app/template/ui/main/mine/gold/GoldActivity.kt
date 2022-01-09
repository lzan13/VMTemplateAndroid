package com.vmloft.develop.app.template.ui.main.mine.gold

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityGoldBinding
import com.vmloft.develop.app.template.request.bean.Order
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.request.viewmodel.UserViewModel
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.ui.widget.CommonDialog
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

    // 当前选中充值金额
    private var rechargeIndex: Int = 1
    private var rechargeCount: Int = 8

    override fun initVM(): UserViewModel = getViewModel()

    override fun initVB() = ActivityGoldBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.gold_title)
        setTopEndBtnListener(VMStr.byRes(R.string.gold_desc)) { CRouter.go(AppRouter.appGoldDesc) }

        mBinding.goldClockGoTV.setOnClickListener { clock() }
        mBinding.goldRechargeLL1.setOnClickListener { changeRecharge(1) }
        mBinding.goldRechargeLL2.setOnClickListener { changeRecharge(2) }
        mBinding.goldRechargeLL3.setOnClickListener { changeRecharge(3) }
        mBinding.goldRechargeLL4.setOnClickListener { changeRecharge(4) }
        mBinding.goldRechargeLL5.setOnClickListener { changeRecharge(5) }
        mBinding.goldRechargeLL6.setOnClickListener { changeRecharge(6) }
        mBinding.goldRechargeLL7.setOnClickListener { changeRecharge(7) }
        mBinding.goldRechargeLL8.setOnClickListener { changeRecharge(8) }

        mBinding.goldRechargeGoTV.setOnClickListener { createOrder() }
        mBinding.goldGoOrderTV.setOnClickListener { CRouter.go(AppRouter.appOrderList) }

        LDEventBus.observe(this, Constants.userInfoEvent, User::class.java, {
            user = it
            bindInfo()
        })
    }

    override fun initData() {
        user = SignManager.getCurrUser() ?: User()

        bindInfo()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "clock") {
            mViewModel.userInfo()
        }
        if (model.type == "userInfo") {
            SignManager.setCurrUser(model.data as User)
        }
        if (model.type == "createOrder") {
            CRouter.go(AppRouter.appOrderDetail, obj0 =  model.data as Order)
        }
    }

    /**
     * 签到
     */
    private fun clock() {
        mViewModel.clock()
    }

    /**
     * 创建订单
     */
    private fun createOrder() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.order_create_hint)
            dialog.setPositive(listener = {
                mViewModel.createOrder(rechargeCount.toString(), "忘忧币充值")
            })
            dialog.show()
        }
    }

    /**
     * 绑定信息
     */
    private fun bindInfo() {
        mBinding.goldCountTV.text = user.score.toString()

        mBinding.goldClockTitleTV.text = VMStr.byResArgs(R.string.gold_clock_count, user.clockTotalCount)

        // 检查是否已签到
        val clockTime = VMDate.milliFormUTC(user.clockTime)
        val isYesterday = VMDate.isSameDate(clockTime, System.currentTimeMillis() - CConstants.timeDay)
        val isToday = VMDate.isSameDate(clockTime, System.currentTimeMillis())

        val continuousCount = if (!isToday && !isYesterday) 0 else user.clockContinuousCount
        mBinding.goldClockContinuousCountTV.text = VMStr.byResArgs(R.string.gold_clock_continuous_count, continuousCount)

        mBinding.goldClockGoTV.setText(if (isToday) R.string.gold_clock_complete else R.string.gold_clock)
        mBinding.goldClockGoTV.isEnabled = !isToday


        mBinding.goldClockContinuousLL1.isSelected = continuousCount > 0
        mBinding.goldClockContinuousLL2.isSelected = continuousCount > 1
        mBinding.goldClockContinuousLL3.isSelected = continuousCount > 2
        mBinding.goldClockContinuousLL4.isSelected = continuousCount > 3
        mBinding.goldClockContinuousLL5.isSelected = continuousCount > 4
        mBinding.goldClockContinuousLL6.isSelected = continuousCount > 5
        mBinding.goldClockContinuousLL7.isSelected = continuousCount > 6

        mBinding.goldClockContinuousTV7.text = "7天"
        if (isYesterday && user.clockContinuousCount > 0) {
            mBinding.goldClockContinuousIV1.setImageResource(R.drawable.ic_done)
        }
        if (isYesterday && user.clockContinuousCount > 1) {
            mBinding.goldClockContinuousIV2.setImageResource(R.drawable.ic_done)
        }
        if (isYesterday && user.clockContinuousCount > 2) {
            mBinding.goldClockContinuousIV3.setImageResource(R.drawable.ic_done)
        }
        if (isYesterday && user.clockContinuousCount > 3) {
            mBinding.goldClockContinuousIV4.setImageResource(R.drawable.ic_done)
        }
        if (isYesterday && user.clockContinuousCount > 4) {
            mBinding.goldClockContinuousIV5.setImageResource(R.drawable.ic_done)
        }
        if (isYesterday && user.clockContinuousCount > 5) {
            mBinding.goldClockContinuousIV6.setImageResource(R.drawable.ic_done)
        }
        if (isYesterday && user.clockContinuousCount > 6) {
            mBinding.goldClockContinuousTV6.text = "..."
            mBinding.goldClockContinuousTV7.text = "${user.clockContinuousCount + 1}天"
            mBinding.goldClockContinuousIV7.setImageResource(R.drawable.ic_done)
        }

        changeRecharge(1)

    }

    /**
     * 改变充值金额
     */
    private fun changeRecharge(index: Int) {
        rechargeIndex = index
        when (index) {
            1 -> rechargeCount = 8
            2 -> rechargeCount = 16
            3 -> rechargeCount = 32
            4 -> rechargeCount = 64
            5 -> rechargeCount = 128
            6 -> rechargeCount = 256
            7 -> rechargeCount = 512
            8 -> rechargeCount = 1024
        }
        mBinding.goldRechargeLL1.isSelected = rechargeIndex == 1
        mBinding.goldRechargeLL2.isSelected = rechargeIndex == 2
        mBinding.goldRechargeLL3.isSelected = rechargeIndex == 3
        mBinding.goldRechargeLL4.isSelected = rechargeIndex == 4
        mBinding.goldRechargeLL5.isSelected = rechargeIndex == 5
        mBinding.goldRechargeLL6.isSelected = rechargeIndex == 6
        mBinding.goldRechargeLL7.isSelected = rechargeIndex == 7
        mBinding.goldRechargeLL8.isSelected = rechargeIndex == 8

        mBinding.goldRechargeGoTV.text = "${VMStr.byRes(R.string.gold_recharge)} ￥$rechargeCount"
    }
}