package com.vmloft.develop.app.template.ui.main.explore

import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.ui.post.ItemPostDelegate
import com.vmloft.develop.library.common.base.BVMFragment
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.common.widget.StaggeredItemDecoration
import com.vmloft.develop.library.tools.utils.VMDimen

import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.widget_common_empty_status_view.*

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：发现
 */
class ExploreFragment : BVMFragment<ExploreViewModel>() {

    private var page = CConstants.defaultPage

    // 适配器
    private val mAdapter by lazy { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private val mLayoutManager by lazy { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }


    override fun initVM(): ExploreViewModel = getViewModel()

    override fun layoutId() = R.layout.fragment_explore

    override fun initUI() {
        super.initUI()
        CUtils.setDarkMode(requireActivity(), true)
        setTopTitle(R.string.nav_explore)

        initRecyclerView()

        publishIV.setOnClickListener { CRouter.go(AppRouter.appPublish) }

        LDEventBus.observe(this, Constants.createPostEvent, Int::class.java) {
            page = CConstants.defaultPage
            mViewModel.getPostList()
        }
    }

    override fun initData() {

        mViewModel.getPostList()
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemPostDelegate(object : ItemPostDelegate.PostItemListener {
            override fun onClick(v: View, data: Post, position: Int) {
                AppRouter.goPostDetail(data)
            }

            override fun onLikeClick(item: Post, position: Int) {
                clickLike(item, position)
            }
        }))

        mAdapter.items = mItems

        recyclerView.layoutManager = mLayoutManager
        recyclerView.addItemDecoration(StaggeredItemDecoration(VMDimen.dp2px(8)))
        recyclerView.adapter = mAdapter

        // 刷新监听
        refreshLayout.setOnRefreshListener {
            refreshLayout.setNoMoreData(false)
            page = CConstants.defaultPage
            mViewModel.getPostList()
        }
        refreshLayout.setOnLoadMoreListener { mViewModel.getPostList(page++) }
    }

    private fun refresh(paging: RPaging<Post>) {
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
            refreshLayout.setNoMoreData(true)
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
            emptyStatusIV.setImageResource(R.drawable.ic_empty_data)
            refreshLayout.visibility = if (mItems.isEmpty()) View.GONE else View.VISIBLE
            emptyStatusLL.visibility = if (mItems.isEmpty()) View.VISIBLE else View.GONE
        } else {
            emptyStatusIV.setImageResource(R.drawable.ic_empty_failed)
            refreshLayout.visibility = View.GONE
            emptyStatusLL.visibility = View.VISIBLE
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (!model.isLoading) {
            refreshLayout.finishRefresh()
            refreshLayout.finishLoadMore()
        }
        if (model.type == "postList") {
            refresh(model.data as RPaging<Post>)
        }
    }

    override fun onModelError(model: BViewModel.UIModel) {
        super.onModelError(model)
        checkEmptyStatus(1)
    }

    /**
     * 处理喜欢点击
     */
    private fun clickLike(post: Post, position: Int) {
        post.isLike = !post.isLike
        if (post.isLike) {
            post.likeCount++
            mViewModel.like(post.id)
        } else {
            post.likeCount--
            mViewModel.cancelLike(post.id)
        }
        mAdapter.notifyItemChanged(position)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        activity?.let {
            CUtils.setDarkMode(it, true)
        }
    }
}