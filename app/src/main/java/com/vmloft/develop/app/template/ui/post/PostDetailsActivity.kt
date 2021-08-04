package com.vmloft.develop.app.template.ui.post

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.request.bean.Comment
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.widget.StaggeredItemDecoration
import com.vmloft.develop.library.tools.utils.VMDimen

import kotlinx.android.synthetic.main.activity_post_details.*

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2021/05/20 22:56
 * 描述：帖子详情界面
 */
@Route(path = AppRouter.appPostDetails)
class PostDetailsActivity : BVMActivity<PostViewModel>() {

    @Autowired
    lateinit var post: Post

    private var page = CConstants.defaultPage

    // 适配器
    private val mAdapter by lazy { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()


    override fun initVM(): PostViewModel = getViewModel()

    override fun layoutId() = R.layout.activity_post_details

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.post_title)

        initRecyclerView()
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        mItems.add(post)
        mAdapter.notifyDataSetChanged()

        mViewModel.getCommentList(post.id)
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "postDetails") {
//            post = model.data as Post
        } else if (model.type == "commentList") {
            refresh(model.data as RPaging<Comment>)
        }
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemPostDetailsHeaderDelegate(object : ItemPostDetailsHeaderDelegate.PostItemListener {
            override fun onClick(v: View, data: Post, position: Int) {}
            override fun onLikeClick(item: Post, position: Int) {
                clickLike(item, position)
            }
        }))
        mAdapter.register(ItemPostDetailsCommentDelegate())

        mAdapter.items = mItems

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter
        // 设置下拉刷新
        refreshLayout.setOnRefreshListener {
            refreshLayout.setNoMoreData(false)
            page = CConstants.defaultPage
            mViewModel.getCommentList(post.id)
        }
        refreshLayout.setOnLoadMoreListener {
            mViewModel.getPostList(post.id, page++)
        }
    }

    private fun refresh(paging: RPaging<Comment>) {
        if (paging.page == 0) {
            mItems.clear()
            mItems.add(post)
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
        mAdapter.notifyItemChanged(0)
    }

}