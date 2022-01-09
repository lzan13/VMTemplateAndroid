package com.vmloft.develop.app.template.ui.post

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.databinding.ActivityPostDetailsBinding
import com.vmloft.develop.app.template.request.bean.Comment
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.request.viewmodel.PostViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.router.CRouter

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2021/05/20 22:56
 * 描述：帖子详情界面
 */
@Route(path = AppRouter.appPostDetails)
class PostDetailsActivity : BVMActivity<ActivityPostDetailsBinding, PostViewModel>() {

    @Autowired(name = CRouter.paramsObj0)
    lateinit var post: Post

    private var page = CConstants.defaultPage

    // 适配器
    private val mAdapter by lazy { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()

    override fun initVB() = ActivityPostDetailsBinding.inflate(layoutInflater)

    override fun initVM(): PostViewModel = getViewModel()

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.post_detail_title)

        mBinding.commentIV.setOnClickListener { CRouter.go(AppRouter.appPostComment, str0 = post.id) }

        initRecyclerView()

        LDEventBus.observe(this, Constants.createCommentEvent, Comment::class.java) {
            mItems.add(1, it)
            mAdapter.notifyItemInserted(1)
        }
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        mItems.add(post)
        mAdapter.notifyDataSetChanged()

        mViewModel.postInfo(post.id)
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "postInfo") {
            post = model.data as Post

            mViewModel.commentList(post.id)
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
                clickLikePost()
            }
        }))
        mAdapter.register(ItemPostDetailsCommentDelegate(object : BItemDelegate.BItemListener<Comment> {
            override fun onClick(v: View, data: Comment, position: Int) {
                CRouter.go(AppRouter.appPostComment, str0 = post.id, obj0 =  data)
            }
        }))

        mAdapter.items = mItems

        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = mAdapter
        // 设置下拉刷新
        mBinding.refreshLayout.setOnRefreshListener {
            mBinding.refreshLayout.setNoMoreData(false)
            page = CConstants.defaultPage
            mViewModel.commentList(post.id)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.postList(post.id, page++)
        }
    }

    private fun refresh(paging: RPaging<Comment>) {
        val position = mItems.size
        val count = paging.data.size
        mItems.addAll(paging.data)
        mAdapter.notifyItemRangeInserted(position, count)
        if (paging.currentCount + paging.page * paging.limit >= paging.totalCount) {
            mBinding.refreshLayout.setNoMoreData(true)
        }
    }


    /**
     * 处理点击喜欢帖子事件
     */
    private fun clickLikePost() {
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