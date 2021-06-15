package com.vmloft.develop.app.template.ui.post


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.FragmentPostFallsBinding
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.base.BVMFragment
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.widget.StaggeredItemDecoration
import com.vmloft.develop.library.tools.utils.VMDimen

import kotlinx.android.synthetic.main.fragment_post_falls.postEmptyIV
import kotlinx.android.synthetic.main.fragment_post_falls.recyclerView
import kotlinx.android.synthetic.main.fragment_post_falls.refreshLayout

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：展示指定用户发布的内容
 */
class PostFallsFragment : BVMFragment<PostViewModel>() {

    private var page = CConstants.defaultPage

    // 适配器
    private val mAdapter by lazy { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private val mLayoutManager by lazy { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }

    private lateinit var userId: String

    companion object {
        private val argUserId = "argUserId"

        /**
         * Fragment 的工厂方法，方便创建并设置参数
         */
        fun newInstance(userId: String): PostFallsFragment {
            val fragment = PostFallsFragment()
            val args = Bundle()
            args.putString(argUserId, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initVM(): PostViewModel = getViewModel()

    override fun layoutId() = R.layout.fragment_post_falls

    override fun initUI() {
        super.initUI()

        (mBinding as FragmentPostFallsBinding).viewModel = mViewModel

        initRecyclerView()
    }

    override fun initData() {
        userId = arguments!!.getString(argUserId) ?: ""

        mViewModel.getPostList(userId)
    }

    /**
     * 结果刷新
     */
    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (!model.isLoading) {
            refreshLayout.finishRefresh()
            refreshLayout.finishLoadMore()
        }
        if (model.type == "postList") {
            refresh(model.data as RPaging<Post>)
        }
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
        // 设置下拉刷新
        refreshLayout.setOnRefreshListener {
            refreshLayout.setNoMoreData(false)
            page = CConstants.defaultPage
            mViewModel.getPostList(userId)
        }
        refreshLayout.setOnLoadMoreListener {
            mViewModel.getPostList(userId, page++)
        }
    }

    private fun refresh(paging: RPaging<Post>) {
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
            refreshLayout.setNoMoreData(true)
        }
        // 空数据展示
        postEmptyIV.visibility = if (mItems.isEmpty()) View.VISIBLE else View.GONE
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
}