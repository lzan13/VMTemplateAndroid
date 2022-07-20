package com.vmloft.develop.app.template.ui.post

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.drakeet.multitype.MultiTypeAdapter
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.library.data.common.SignManager

import com.vmloft.develop.app.template.databinding.FragmentCommonListBinding
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.data.bean.Post
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.viewmodel.PostViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.ui.widget.ContentDislikeDialog
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMFragment
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.base.widget.decoration.StaggeredItemDecoration
import com.vmloft.develop.library.tools.utils.VMDimen
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：展示指定用户发布的帖子
 */
class PostFallsFragment : BVMFragment<FragmentCommonListBinding, PostViewModel>() {

    private lateinit var user: User

    private var page = CConstants.defaultPage

    // 长按弹出菜单
    private var currPost: Post? = null
    private var currPosition = -1

    // 适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private val mLayoutManager by lazy(LazyThreadSafetyMode.NONE) { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }

    private var userId: String = ""

    companion object {
        private val argUserId = "argUserId"

        /**
         * Fragment 的工厂方法，方便创建并设置参数
         */
        fun newInstance(userId: String = ""): PostFallsFragment {
            val fragment = PostFallsFragment()
            val args = Bundle()
            args.putString(argUserId, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = FragmentCommonListBinding.inflate(inflater, parent, false)

    override fun initVM(): PostViewModel = getViewModel()

    override fun initUI() {
        super.initUI()

        initRecyclerView()
    }

    override fun initData() {
        user = SignManager.getCurrUser()
        userId = requireArguments().getString(argUserId) ?: ""

        mBinding.refreshLayout.autoRefresh()
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemPostDelegate(object : ItemPostDelegate.PostItemListener {
            override fun onClick(v: View, data: Post, position: Int) {
                CRouter.go(AppRouter.appPostDetails, obj0 = data)
            }

            override fun onLikeClick(item: Post, position: Int) {
                likePost(item, position)
            }
        }, object : BItemDelegate.BItemLongListener<Post> {
            override fun onLongClick(v: View, event: MotionEvent, data: Post, position: Int): Boolean {
                // 自己的时候不需要处理
                if (userId == user.id) return true

                currPost = data
                currPosition = position
                reportPostDialog()
                return true
            }
        }))

        mAdapter.items = mItems

        mBinding.recyclerView.layoutManager = mLayoutManager
        mBinding.recyclerView.addItemDecoration(StaggeredItemDecoration(VMDimen.dp2px(4)))
        mBinding.recyclerView.adapter = mAdapter
        // 设置下拉刷新
        mBinding.refreshLayout.setOnRefreshListener {
            mBinding.refreshLayout.setNoMoreData(false)
            page = CConstants.defaultPage
            mViewModel.postList(userId)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.postList(userId, page++)
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
        }
    }

    /**
     * 结果刷新
     */
    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "postList") {
            refresh(model.data as RPaging<Post>)
        } else if (model.type == "shieldPost") {
            LDEventBus.post(Constants.Event.shieldPost, currPost!!)
        }
    }

    override fun onModelError(model: BViewModel.UIModel) {
        super.onModelError(model)
        if (model.type == "postList") {
            checkEmptyStatus(1)
        } else if (model.type == "shieldPost") {
            showBar(R.string.feedback_hint)
        }
    }

    /**
     * 处理点击喜欢帖子事件
     */
    private fun likePost(post: Post, position: Int) {
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

    /**
     * 长按 post 弹出屏蔽举报菜单
     */
    private fun reportPostDialog() {
        mDialog = ContentDislikeDialog(requireContext())
        (mDialog as ContentDislikeDialog).let { dialog ->
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
        currPost?.isShielded = true
        mViewModel.shieldPost(currPost!!)

    }

    /**
     * 举报 Post
     * 0-意见建议 1-广告引流 2-政治敏感 3-违法违规 4-色情低俗 5-血腥暴力 6-诱导信息 7-谩骂攻击 8-涉嫌诈骗 9-引人不适 10-其他
     */
    private fun reportPost(type: Int) {
        mDialog?.dismiss()

        CRouter.go(AppRouter.appFeedback, what = type, obj0 = currPost)
    }
}