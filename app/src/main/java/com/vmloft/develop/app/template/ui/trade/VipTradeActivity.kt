package com.vmloft.develop.app.template.ui.trade

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager

import com.alibaba.android.arouter.facade.annotation.Route

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityVipTradeBinding
import com.vmloft.develop.library.data.bean.Commodity
import com.vmloft.develop.library.data.bean.Order
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.data.viewmodel.UserViewModel
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.base.widget.decoration.GridItemDecoration
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2021/8/11
 * 描述：VIP 相关界面
 */
@Route(path = AppRouter.appVipTrade)
class VipTradeActivity : BVMActivity<ActivityVipTradeBinding, UserViewModel>() {

    private lateinit var user: User

    // 当前选中
    private var currCommodity: Commodity? = null
    private var currPosition: Int = 0

    // 适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Commodity>()

    override fun initVM(): UserViewModel = getViewModel()

    override fun initVB() = ActivityVipTradeBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.gold_vip_title)
        setTopEndBtnListener(VMStr.byRes(R.string.order_list)) { CRouter.go(AppRouter.appOrderList) }

        mBinding.goVIPTV.setOnClickListener { createOrder(currCommodity!!) }

        initRecyclerView()

        setupBanner()
    }

    override fun initData() {
        user = SignManager.getCurrUser()

        bindInfo()

        // 获取虚拟商品
        mViewModel.virtualCommodityList()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "virtualCommodityList") {
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
                clickCommodityVIP(data, position)
            }
        }))

        mAdapter.items = mItems

        mBinding.vipRecyclerView.layoutManager = GridLayoutManager(this, 3)
        mBinding.vipRecyclerView.addItemDecoration(GridItemDecoration(VMDimen.dp2px(8), 3))
        mBinding.vipRecyclerView.setHasFixedSize(true)
        mBinding.vipRecyclerView.isNestedScrollingEnabled = false
        mBinding.vipRecyclerView.adapter = mAdapter
    }

    /**
     * 绑定信息
     */
    private fun bindInfo() {
        mBinding.goVIPTV.text = if (user.role.identity in 100..199) VMStr.byRes(R.string.gold_vip_renewal) else VMStr.byRes(R.string.gold_vip)
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

        mItems.addAll(listVIP.sortedBy { it.price })
        mAdapter.notifyDataSetChanged()
    }

    /**
     * 点击商品
     */
    private fun clickCommodityVIP(data: Commodity, position: Int) {
        if (currCommodity == data) return
        currCommodity?.let {
            currCommodity?.isSelected = false
            mAdapter.notifyItemChanged(currPosition)
        }

        currCommodity = data
        currCommodity?.isSelected = true
        currPosition = position
        mAdapter.notifyItemChanged(position)

        mBinding.goVIPTV.isEnabled = true
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

    /**
     * 装载轮播内容
     */
    private fun setupBanner() {
        val list = mutableListOf<VipBannerItem>()
        list.add(VipBannerItem(R.drawable.ic_vip, VMStr.byRes(R.string.gold_vip_privilege_logo)))
        list.add(VipBannerItem(R.drawable.ic_ads, VMStr.byRes(R.string.gold_vip_privilege_ads)))
        list.add(VipBannerItem(R.drawable.ic_puzzle_line, VMStr.byRes(R.string.gold_vip_privilege_random)))
        list.add(VipBannerItem(R.drawable.ic_paper_crane, VMStr.byRes(R.string.gold_vip_privilege_fast)))
        list.add(VipBannerItem(R.drawable.ic_game, VMStr.byRes(R.string.gold_vip_privilege_relaxation)))
        list.add(VipBannerItem(R.drawable.ic_mic, VMStr.byRes(R.string.gold_vip_privilege_voice)))
        list.add(VipBannerItem(R.drawable.ic_picture, VMStr.byRes(R.string.gold_vip_privilege_picture)))
        list.add(VipBannerItem(R.drawable.ic_call, VMStr.byRes(R.string.gold_vip_privilege_call)))

        mBinding.vipBanner2.setAdapter(BannerAdapter()).create(list)
    }

}