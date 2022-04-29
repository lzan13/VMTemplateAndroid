package com.vmloft.develop.app.template.ui.main.explore

import android.view.*
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.common.SPManager
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.FragmentExploreBinding
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.viewmodel.ExploreViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.ui.post.ItemPostDelegate
import com.vmloft.develop.app.template.ui.widget.ContentDislikeDialog
import com.vmloft.develop.library.ads.ADSConstants
import com.vmloft.develop.library.ads.ADSItem
import com.vmloft.develop.library.ads.ADSItemDelegate
import com.vmloft.develop.library.ads.ADSManager
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMFragment
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.base.widget.decoration.StaggeredItemDecoration
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.widget.guide.GuideItem
import com.vmloft.develop.library.tools.widget.guide.VMGuide
import com.vmloft.develop.library.tools.widget.guide.VMGuideView
import com.vmloft.develop.library.tools.widget.guide.VMShape

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：发现
 */
class ExploreFragment : BVMFragment<FragmentExploreBinding, ExploreViewModel>() {

    private lateinit var user: User
    private var isFirst = true
    private var page = CConstants.defaultPage

    // 长按弹出菜单
    private var currPost: Post? = null
    private var currPosition = -1

    // 适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private val mLayoutManager by lazy(LazyThreadSafetyMode.NONE) { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = FragmentExploreBinding.inflate(layoutInflater)

    override fun initVM(): ExploreViewModel = getViewModel()

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.nav_explore)

        initRecyclerView()

        mBinding.postAddIV.setOnClickListener { CRouter.go(AppRouter.appPostCreate) }

        // 监听 Post 创建事件
        LDEventBus.observe(this, Constants.Event.createPost, Post::class.java) {
            mItems.add(0, it)
            mAdapter.notifyItemInserted(0)
            mBinding.recyclerView.post {
                mBinding.recyclerView.invalidateItemDecorations();
            }
            checkEmptyStatus()
        }
        // 监听 Post 屏蔽事件
        LDEventBus.observe(this, Constants.Event.shieldPost, Post::class.java) {
            if (it == currPost) {
                mItems.removeAt(currPosition)
                mAdapter.notifyItemRemoved(currPosition)
            }
        }
    }

    override fun initData() {
        user = SignManager.getCurrUser() ?: User()

        mViewModel.postList()
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemPostDelegate(object : ItemPostDelegate.PostItemListener {
            override fun onClick(v: View, data: Post, position: Int) {
                currPost = data
                currPosition = position
                CRouter.go(AppRouter.appPostDetails, obj0 = data)
            }

            override fun onLikeClick(item: Post, position: Int) {
                likePost(item, position)
            }
        }, object : BItemDelegate.BItemLongListener<Post> {
            override fun onLongClick(v: View, event: MotionEvent, data: Post, position: Int): Boolean {
                if (data.owner.id == user.id) return true

                currPost = data
                currPosition = position
                reportPostDialog()
                return true
            }
        }))
        mAdapter.register(ADSItemDelegate())

        mAdapter.items = mItems

        mBinding.recyclerView.layoutManager = mLayoutManager
        mBinding.recyclerView.addItemDecoration(StaggeredItemDecoration(VMDimen.dp2px(4)))
        mBinding.recyclerView.adapter = mAdapter
        // 设置下拉刷新
        mBinding.refreshLayout.setOnRefreshListener {
            mBinding.refreshLayout.setNoMoreData(false)
            page = CConstants.defaultPage
            mViewModel.postList()
        }
        mBinding.refreshLayout.setOnLoadMoreListener { mViewModel.postList(page++) }
    }

    private fun refresh(paging: RPaging<Post>) {
        if (paging.page == CConstants.defaultPage) {
            mItems.clear()
            mItems.addAll(paging.data)
            mAdapter.notifyDataSetChanged()
            if (ConfigManager.clientConfig.adsEntry.exploreEntry) {
                // 加载广告，这里只有在第一页数据结束后加载
                loadNativeAD()
            }
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

        checkGuide()
    }

    /**
     * 检查引导
     */
    private fun checkGuide() {
        if (!isFirst || !CSPManager.isNeedGuide(this@ExploreFragment::class.java.simpleName)) return

        mBinding.recyclerView.post {
            val list = mutableListOf<GuideItem>()
            val guideView = mBinding.recyclerView.getChildAt(0).findViewById<View>(R.id.itemGuideView)
            val likeIV = mBinding.recyclerView.getChildAt(0).findViewById<View>(R.id.itemLikeIV)
            list.add(GuideItem(guideView, VMStr.byRes(R.string.guide_explore_report), shape = VMShape.guideShapeCircle, offY = VMDimen.dp2px(16)))
            list.add(GuideItem(likeIV, VMStr.byRes(R.string.guide_post_like), shape = VMShape.guideShapeCircle, offY = VMDimen.dp2px(16)))
            list.add(GuideItem(mBinding.postAddIV, VMStr.byRes(R.string.guide_explore_publish), shape = VMShape.guideShapeCircle, offX = VMDimen.dp2px(96), offY = VMDimen.dp2px(24)))
            VMGuide.Builder(requireActivity()).setOneByOne(true).setGuideViews(list).setGuideListener(object : VMGuideView.GuideListener {
                override fun onFinish() {
                    CSPManager.setNeedGuide(this@ExploreFragment::class.java.simpleName, false)
                }

                override fun onNext(index: Int) {}
            }).build().show()
        }
    }

    /**
     * 加载广告
     */
    private fun loadNativeAD() {
        ADSManager.loadNativeAD(requireActivity()) { status ->
            if (status == ADSConstants.Status.loaded) {
                val position = mItems.size
                mItems.add(ADSItem(""))
                mAdapter.notifyItemInserted(position)
            }
        }
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
        if (isFirst) {
            if (model.isLoading) showLoading() else hideLoading()
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "postList") {
            refresh(model.data as RPaging<Post>)
            // 因为为了快速展示数据，第一次从本地取，后边会再刷新一次
            if (isFirst) {
                isFirst = false
//                mBinding.refreshLayout.autoRefresh((CConstants.timeSecond / 2).toInt())
            }
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
     * 处理喜欢点击
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