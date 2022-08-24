package com.vmloft.develop.library.gift

import android.view.*
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.widget.decoration.StaggeredItemDecoration
import com.vmloft.develop.library.data.bean.Gift
import com.vmloft.develop.library.data.viewmodel.GiftViewModel
import com.vmloft.develop.library.gift.databinding.ActivityGiftMineBinding
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.tools.utils.VMDimen

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2022/07/13 07:54
 * 描述：我的礼物列表
 */
@Route(path = GiftRouter.giftMine)
class GiftMineActivity : BVMActivity<ActivityGiftMineBinding, GiftViewModel>() {


    @Autowired(name = CRouter.paramsStr0)
    lateinit var userId: String

    // 列表适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Gift>()
    private val mLayoutManager by lazy(LazyThreadSafetyMode.NONE) { StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL) }

    override fun initVB() = ActivityGiftMineBinding.inflate(layoutInflater)

    override fun initVM(): GiftViewModel = getViewModel()

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.gift_mine)

        initRecyclerView()

    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        mBinding.refreshLayout.autoRefresh()
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        // 注册各类消息
        mAdapter.register(ItemGiftDelegate(object : BItemDelegate.BItemListener<Gift> {
            override fun onClick(v: View, data: Gift, position: Int) {
                clickGift(data, position)
            }
        }, true))
        mAdapter.items = mItems

        mBinding.recyclerView.addItemDecoration(StaggeredItemDecoration(VMDimen.dp2px(8)))
        mBinding.recyclerView.layoutManager = mLayoutManager
        mBinding.recyclerView.adapter = mAdapter

        // 设置下拉刷新
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.giftRelationList(userId)
        }
        setEmptyClick { mViewModel.giftRelationList(userId) }
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        if (!model.isLoading) {
            mBinding.refreshLayout.finishRefresh()
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "giftRelationList") {
            refresh(model.data as RPaging<Gift>)
        }
    }

    /**
     * 点击礼物
     */
    private fun clickGift(gift: Gift, position: Int) {
        CRouter.go(GiftRouter.giftAnim, what = gift.type, obj0 = gift.cover, obj1 = gift.animation)
    }


    /**
     * 刷新界面
     */
    private fun refresh(paging: RPaging<Gift>) {
        mItems.clear()
        mItems.addAll(paging.data)
        mItems.sortBy { it.price }
        mAdapter.notifyDataSetChanged()

        checkEmpty()
    }

    private fun checkEmpty() {
        if (mItems.isEmpty()) {
            showEmptyNoData()
        } else {
            hideEmptyView()
        }
    }
}