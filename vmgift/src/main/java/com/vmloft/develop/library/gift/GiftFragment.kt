package com.vmloft.develop.library.gift

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMFragment
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.base.widget.decoration.StaggeredItemDecoration
import com.vmloft.develop.library.data.bean.Gift
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.DConstants
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.viewmodel.GiftViewModel
import com.vmloft.develop.library.gift.databinding.FragmentGiftBinding
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.tools.utils.VMDimen

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2019/5/9 10:34
 * 描述：IM 礼物界面，可加载到自己的容器
 */
class GiftFragment : BVMFragment<FragmentGiftBinding, GiftViewModel>() {

    private var userId: String = ""
    private lateinit var selfUser: User

    // 列表适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Gift>()
    private val mLayoutManager by lazy(LazyThreadSafetyMode.NONE) { StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL) }

    private var currGift: Gift? = null
    private var currPosition: Int = -1

    companion object {
        private val argUserId = "argUserId"

        /**
         * Fragment 的工厂方法，方便创建并设置参数
         */
        fun newInstance(userId: String): GiftFragment {
            val fragment = GiftFragment()
            val args = Bundle()
            args.putString(argUserId, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initVM(): GiftViewModel = getViewModel()

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = FragmentGiftBinding.inflate(inflater, parent, false)

    override fun initUI() {
        super.initUI()

//        const val appGoldRecharge = "/App/GoldRecharge"
        mBinding.goRechargeTV.setOnClickListener { CRouter.go("/App/Gold") }
        mBinding.giveTV.setOnClickListener { giftGive() }

        initRecyclerView()
    }

    override fun initData() {
        userId = requireArguments().getString(argUserId) ?: ""

        selfUser = SignManager.getCurrUser()

        mViewModel.giftList()

        bindInfo()
    }

    /**
     * 初始化会话列表
     */
    private fun initRecyclerView() {
        // 注册各类消息
        mAdapter.register(ItemGiftDelegate(object : BItemDelegate.BItemListener<Gift> {
            override fun onClick(v: View, data: Gift, position: Int) {
                clickGift(data, position)
            }
        }))
        mAdapter.items = mItems

        mBinding.recyclerView.addItemDecoration(StaggeredItemDecoration(VMDimen.dp2px(8)))
        mBinding.recyclerView.layoutManager = mLayoutManager
        mBinding.recyclerView.adapter = mAdapter

        setEmptyClick { mViewModel.giftList() }
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        super.onModelLoading(model)

        if (model.type == "giftGive") {
            mBinding.giveTV.isEnabled = !model.isLoading
//            mBinding.loadingView.visibility = if (model.isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "giftList") {
            refresh(model.data as RPaging<Gift>)
        } else if (model.type == "giftGive") {
            giftGiveSuccess()
        }
    }

    override fun onModelError(model: BViewModel.UIModel) {
        super.onModelError(model)
        if (model.type == "giftList") {
            showEmptyFailed()
        }
    }

    private fun bindInfo() {
        mBinding.scoreTV.text = selfUser.score.toString()
    }

    /**
     * 点击礼物
     */
    private fun clickGift(data: Gift, position: Int) {
        if (currGift == data) return
        currGift?.let {
            currGift?.isSelected = false
            mAdapter.notifyItemChanged(currPosition)
        }

        currGift = data
        currGift?.isSelected = true
        currPosition = position
        mAdapter.notifyItemChanged(position)

        mBinding.giveTV.isEnabled = true
    }

    /**
     * 赠送礼物
     */
    private fun giftGive() {
        if (currGift == null) return
        if (selfUser.score < currGift!!.price) {
            return errorBar(R.string.gift_score_lack)
        }
        mViewModel.giftGive(userId, currGift!!.id, 1)
    }

    /**
     * 礼物赠送成功，更新下用户月
     */
    private fun giftGiveSuccess() {
        selfUser.score -= currGift!!.price * 1
        SignManager.setCurrUser(selfUser)
        bindInfo()

        LDEventBus.post(DConstants.Event.giftGive, currGift!!)
    }

    /**
     * 刷新界面
     */
    private fun refresh(paging: RPaging<Gift>) {
        mItems.clear()
        mItems.addAll(paging.data)
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
