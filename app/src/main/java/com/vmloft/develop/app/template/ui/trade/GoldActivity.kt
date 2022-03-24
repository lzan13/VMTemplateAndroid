package com.vmloft.develop.app.template.ui.trade

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager

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
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.base.widget.decoration.GridItemDecoration
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.tools.utils.VMDate
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2021/8/11
 * 描述：金币相关界面
 */
@Route(path = AppRouter.appGold)
class GoldActivity : BVMActivity<ActivityGoldBinding, UserViewModel>() {

    private lateinit var user: User

    // 当前选中
    private var currCommodity: Commodity? = null
    private var currPosition: Int = 0

    // 适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Commodity>()

    // 当前选中
    private var currCommodityVIP: Commodity? = null
    private var currPositionVIP: Int = 0

    // 适配器
    private val mAdapterVIP by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItemsVIP = ArrayList<Commodity>()

    override fun initVM(): UserViewModel = getViewModel()

    override fun initVB() = ActivityGoldBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.gold_title)
        setTopEndBtnListener(VMStr.byRes(R.string.gold_desc)) { CRouter.go(AppRouter.appGoldDesc) }

        mBinding.clockTV.setOnClickListener { clock() }

        mBinding.goOrderTV.setOnClickListener { CRouter.go(AppRouter.appOrderList) }

        mBinding.goRechargeTV.setOnClickListener { createOrder(currCommodity!!) }
        mBinding.goRechargeTVVIP.setOnClickListener { createOrder(currCommodityVIP!!) }

        initRecyclerView()

        // 监听用户信息变化
        LDEventBus.observe(this, Constants.Event.userInfo, User::class.java, {
            user = it
            bindInfo()
        })

        // 订单支付成功，更新下用户信息
        LDEventBus.observe(this, Constants.Event.orderStatus, Order::class.java) {
            mViewModel.userInfo()
        }
    }

    override fun initData() {
        user = SignManager.getCurrUser() ?: User()

        bindInfo()

        // 获取虚拟商品
        mViewModel.virtualCommodityList()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "clock") {
            mViewModel.userInfo()
        } else if (model.type == "userInfo") {
            SignManager.setCurrUser(model.data as User)
        } else if (model.type == "virtualCommodityList") {
            refreshCommodityList(model.data as RPaging<Commodity>)
        } else if (model.type == "createOrder") {
            CRouter.go(AppRouter.appOrderDetail, obj0 = model.data as Order)
        }
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemVirtualCommodityDelegate(object : BItemDelegate.BItemListener<Commodity> {
            override fun onClick(v: View, data: Commodity, position: Int) {
                clickCommodity(data, position)
            }
        }))

        mAdapter.items = mItems

        mBinding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        mBinding.recyclerView.addItemDecoration(GridItemDecoration(VMDimen.dp2px(8), 3))
        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.isNestedScrollingEnabled = false
        mBinding.recyclerView.adapter = mAdapter

        // 会员部分
        mAdapterVIP.register(ItemVirtualCommodityDelegate(object : BItemDelegate.BItemListener<Commodity> {
            override fun onClick(v: View, data: Commodity, position: Int) {
                clickCommodityVIP(data, position)
            }
        }))

        mAdapterVIP.items = mItemsVIP

        mBinding.recyclerViewVIP.layoutManager = GridLayoutManager(this, 3)
        mBinding.recyclerViewVIP.addItemDecoration(GridItemDecoration(VMDimen.dp2px(8), 3))
        mBinding.recyclerViewVIP.setHasFixedSize(true)
        mBinding.recyclerViewVIP.isNestedScrollingEnabled = false
        mBinding.recyclerViewVIP.adapter = mAdapterVIP
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

        mBinding.goRechargeTVVIP.text = if (user.role.identity in 100..199) VMStr.byRes(R.string.gold_recharge_vip_renewal) else VMStr.byRes(R.string.gold_recharge_vip)

        if (ConfigManager.clientConfig.vipEntry) {
            mBinding.roleTipsTV.visibility = View.VISIBLE
            mBinding.rechargeCL.visibility = View.VISIBLE
        } else {
            mBinding.roleTV.setText(R.string.gold_balance)
            mBinding.roleTipsTV.visibility = View.GONE
            mBinding.rechargeCL.visibility = View.GONE
        }
    }

    /**
     * 刷新商品信息
     */
    private fun refreshCommodityList(paging: RPaging<Commodity>) {
        val list = mutableListOf<Commodity>()
        val listVIP = mutableListOf<Commodity>()
        for (item in paging.data) {
            if (item.type == 0) {
                list.add(item)
            } else {
                listVIP.add(item)
            }
        }

        mItems.addAll(list.sortedBy { it.price.toFloat() })
        mAdapter.notifyDataSetChanged()


        mItemsVIP.addAll(listVIP.sortedBy { it.price.toFloat() })
        mAdapterVIP.notifyDataSetChanged()
    }

    /**
     * 点击商品
     */
    private fun clickCommodity(data: Commodity, position: Int) {
        if (currCommodity == data) return
        currCommodity?.let {
            currCommodity?.isSelected = false
            mAdapter.notifyItemChanged(currPosition)
        }

        currCommodity = data
        currCommodity?.isSelected = true
        currPosition = position
        mAdapter.notifyItemChanged(position)

        mBinding.goRechargeTV.isEnabled = true
    }

    /**
     * 点击商品
     */
    private fun clickCommodityVIP(data: Commodity, position: Int) {
        if (currCommodityVIP == data) return
        currCommodityVIP?.let {
            currCommodityVIP?.isSelected = false
            mAdapterVIP.notifyItemChanged(currPositionVIP)
        }

        currCommodityVIP = data
        currCommodityVIP?.isSelected = true
        currPositionVIP = position
        mAdapterVIP.notifyItemChanged(position)

        mBinding.goRechargeTVVIP.isEnabled = true
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
    private fun createOrder(commodity: Commodity) {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.order_create_hint)
            dialog.setPositive(listener = {
                var list = arrayListOf(commodity.id)
                mViewModel.createOrder(list, commodity.remarks)
            })
            dialog.show()
        }
    }
}