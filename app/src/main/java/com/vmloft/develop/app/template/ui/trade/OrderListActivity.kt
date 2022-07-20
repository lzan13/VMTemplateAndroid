package com.vmloft.develop.app.template.ui.trade

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

import com.alibaba.android.arouter.facade.annotation.Route

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.databinding.ActivityOrderListBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.data.bean.Order
import com.vmloft.develop.library.data.viewmodel.TradeViewModel
import com.vmloft.develop.library.request.RPaging

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2021/8/11
 * 描述：订单列表相关界面
 */
@Route(path = AppRouter.appOrderList)
class OrderListActivity : BVMActivity<ActivityOrderListBinding, TradeViewModel>() {

    // 适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Order>()

    private var page = CConstants.defaultPage

    override fun initVB() = ActivityOrderListBinding.inflate(layoutInflater)

    override fun initVM(): TradeViewModel = getViewModel()

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.order_list)

        initRecyclerView()

        // 监听订单信息变化
        LDEventBus.observe(this, Constants.Event.orderStatus, Order::class.java) {
            var position = mItems.indexOf(it)
            mItems.removeAt(position)
            mItems.add(position, it)
            mAdapter.notifyItemChanged(position)
        }
    }

    override fun initData() {
        mBinding.refreshLayout.autoRefresh()
    }


    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "orderList") {
            refresh(model.data as RPaging<Order>)
        }
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemOrderDelegate(object : BItemDelegate.BItemListener<Order> {
            override fun onClick(v: View, data: Order, position: Int) {
                CRouter.go(AppRouter.appOrderDetail, obj0 = data)
            }
        }))

        mAdapter.items = mItems

        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = mAdapter
        // 设置下拉刷新
        mBinding.refreshLayout.setOnRefreshListener {
            mBinding.refreshLayout.setNoMoreData(false)
            page = CConstants.defaultPage
            mViewModel.orderList()
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.orderList(page)
        }
    }

    private fun refresh(paging: RPaging<Order>) {
        if (paging.page == 0) {
            mItems.clear()
            mItems.addAll(paging.data)
            mAdapter.notifyDataSetChanged()
        } else {
            val position = mItems.size
            val count = paging.data.size
            mItems.addAll(paging.data)
            mAdapter.notifyItemRangeInserted(position, count)
        }
        if (paging.currentCount + paging.page * paging.limit >= paging.totalCount) {
            mBinding.refreshLayout.setNoMoreData(true)
        }

        // 空态检查
        checkEmptyStatus()
    }

    /**
     * 空态检查
     * @param type  0-默认检查空态 1-请求失败
     */
    private fun checkEmptyStatus(type: Int = 0) {
        if (type == 0) {
            if (mItems.isEmpty()) {
                showEmptyNoData()
            } else {
                hideEmptyView()
            }
        } else {
            mBinding.refreshLayout.visibility = View.GONE
            showEmptyFailed()
        }
    }
}