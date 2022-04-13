package com.vmloft.develop.app.template.ui.main.mine.relation

import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager

import com.alibaba.android.arouter.facade.annotation.Route

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivityRelationListBinding
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.viewmodel.UserViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.widget.CommonDialog

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2022/04/08 21:54
 * 描述：黑名单列表
 */
@Route(path = AppRouter.appMineBlacklist)
class BlacklistActivity : BVMActivity<ActivityRelationListBinding, UserViewModel>() {

    private var type = 0

    private var page = CConstants.defaultPage
    private var currUser: User? = null
    private var currPosition: Int = -1

    // 适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private val mLayoutManager by lazy(LazyThreadSafetyMode.NONE) { LinearLayoutManager(this) }

    override fun initVB() = ActivityRelationListBinding.inflate(layoutInflater)

    override fun initVM(): UserViewModel = getViewModel()

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.mine_blacklist)

        initRecyclerView()

    }

    override fun initData() {
        mViewModel.getBlacklist(type)
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemRelationDelegate(object : ItemRelationDelegate.RelationItemListener {
            override fun onClick(v: View, data: User, position: Int) {
                CRouter.go(AppRouter.appUserInfo, obj0 = data)
            }

            override fun onRelationClick(item: User, position: Int) {
                currUser = item
                currPosition = position
                clickRelation()
            }
        }, true))

        mAdapter.items = mItems

        mBinding.recyclerView.layoutManager = mLayoutManager
        mBinding.recyclerView.adapter = mAdapter
        // 设置下拉刷新
        mBinding.refreshLayout.setOnRefreshListener {
            mBinding.refreshLayout.setNoMoreData(false)
            page = CConstants.defaultPage
            mViewModel.getBlacklist(type)
        }
        mBinding.refreshLayout.setOnLoadMoreListener { mViewModel.getBlacklist(type, page++) }
    }

    private fun refresh(paging: RPaging<User>) {
        if (paging.page == CConstants.defaultPage) {
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

    override fun onModelLoading(model: BViewModel.UIModel) {
        if (!model.isLoading) {
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            hideLoading()
        } else {
            if (model.type != "getBlacklist") {
                showLoading()
            }
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "getBlacklist") {
            refresh(model.data as RPaging<User>)
        } else if (model.type == "blacklist") {
            currUser!!.relation++
            mAdapter.notifyItemChanged(currPosition)
        } else if (model.type == "cancelBlacklist") {
            currUser!!.relation--
            mAdapter.notifyItemChanged(currPosition)
        }
    }

    override fun onModelError(model: BViewModel.UIModel) {
        super.onModelError(model)
        if (model.type == "getBlacklist") {
            checkEmptyStatus(1)
        }
    }

    /**
     * 点击关系按钮
     */
    private fun clickRelation() {
        if (currUser!!.relation == 0 || currUser!!.relation == 2) {
            showCancelRelationDialog()
        } else {
            mViewModel.blacklist(currUser!!.id)
        }
    }

    /**
     * 取消拉黑对话框
     */
    private fun showCancelRelationDialog() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.blacklist_remove_tips)
            dialog.setPositive {
                mViewModel.cancelBlacklist(currUser!!.id)
            }
            dialog.show()
        }
    }

}