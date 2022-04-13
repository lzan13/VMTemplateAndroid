package com.vmloft.develop.app.template.ui.post

import android.view.Gravity
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
import com.vmloft.develop.app.template.ui.widget.PostDislikeDialog
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.request.RPaging
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
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()

    override fun initVB() = ActivityPostDetailsBinding.inflate(layoutInflater)

    override fun initVM(): PostViewModel = getViewModel()

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.post_detail_title)

        mBinding.commentIV.setOnClickListener { CRouter.go(AppRouter.appPostComment, str0 = post.id) }

        initRecyclerView()

        // 监听评论信息添加
        LDEventBus.observe(this, Constants.Event.createComment, Comment::class.java) {
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

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemPostDetailsHeaderDelegate(object : ItemPostDetailsHeaderDelegate.PostItemListener {
            override fun onClick(v: View, data: Post, position: Int) {}
            override fun onLikeClick(item: Post, position: Int) {
                clickLikePost()
            }

            override fun onReportClick(item: Post, position: Int) {
                clickReportPost()
            }
        }))
        mAdapter.register(ItemPostDetailsCommentDelegate(object : BItemDelegate.BItemListener<Comment> {
            override fun onClick(v: View, data: Comment, position: Int) {
                CRouter.go(AppRouter.appPostComment, str0 = post.id, obj0 = data)
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
            mViewModel.commentList(post.id, page++)
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "postInfo") {
            post = model.data as Post

            refreshPost()

            mViewModel.commentList(post.id)
        } else if (model.type == "commentList") {
            refreshComment(model.data as RPaging<Comment>)
        } else if (model.type == "shieldPost") {
            LDEventBus.post(Constants.Event.shieldPost, post)
        }
    }

    /**
     * 刷新帖子内容
     */
    private fun refreshPost() {
        mItems.removeAt(0)
        mItems.add(0, post)
        mAdapter.notifyItemChanged(0)
    }

    /**
     * 刷新评论
     */
    private fun refreshComment(paging: RPaging<Comment>) {
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

    /**
     * 弹出屏蔽举报菜单
     */
    private fun clickReportPost() {
        mDialog = PostDislikeDialog(this)
        (mDialog as PostDislikeDialog).let { dialog ->
            dialog.setShieldListener { type -> shieldPost(type) }
            dialog.setReportListener { type -> reportPost(type) }
            dialog.show(Gravity.BOTTOM)
        }
    }

    /**
     * 屏蔽 Post
     */
    private fun shieldPost(type: Int) {
        mDialog?.dismiss()

        if (type == 0) {
            // TODO 屏蔽内容
        } else if (type == 1) {
            // TODO 屏蔽用户
        }
        post.isShielded = true
        mViewModel.shieldPost(post)
    }

    /**
     * 举报 Post
     * 0-意见建议 1-广告引流 2-政治敏感 3-违法违规 4-色情低俗 5-血腥暴力 6-诱导信息 7-谩骂攻击 8-涉嫌诈骗 9-引人不适 10-其他
     */
    private fun reportPost(type: Int) {
        mDialog?.dismiss()

        CRouter.go(AppRouter.appFeedback, what = type, obj0 = post)
    }
}