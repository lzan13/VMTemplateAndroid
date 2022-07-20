package com.vmloft.develop.app.template.ui.main.home

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivityCommonListBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.common.PermissionManager
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.widget.decoration.StaggeredItemDecoration
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.bean.Applet
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.viewmodel.AppletViewModel
import com.vmloft.develop.library.mp.MPManager
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMFile

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2022/5/23 23:13
 * 描述：程序界面
 */
@Route(path = AppRouter.appAppletList)
class AppletListActivity : BVMActivity<ActivityCommonListBinding, AppletViewModel>() {

    override var isDarkStatusBar: Boolean = true

    private lateinit var user: User

    // 适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Applet>()
    private val mLayoutManager by lazy(LazyThreadSafetyMode.NONE) { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }


    private var page = CConstants.defaultPage

    override fun initVM(): AppletViewModel = getViewModel()

    override fun initVB() = ActivityCommonListBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.relaxation_world)

        initRecyclerView()
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        user = SignManager.getCurrUser()

        mBinding.refreshLayout.autoRefresh()
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        if (model.type == "appletList") {
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
        }
        if (model.type == "download") {
            super.onModelLoading(model)
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "appletList") {
            refresh(model.data as RPaging<Applet>)
        } else if (model.type == "download") {
            startMP(model.data as Applet)
        }
    }

    override fun onModelError(model: BViewModel.UIModel) {
        super.onModelError(model)
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemAppletDelegate(object : BItemDelegate.BItemListener<Applet> {
            override fun onClick(v: View, data: Applet, position: Int) {
                itemClick(data, true)
            }
        }, object : BItemDelegate.BItemLongListener<Applet> {
            override fun onLongClick(v: View, event: MotionEvent, data: Applet, position: Int): Boolean {
                // 长按直接跳转详情页
                itemClick(data, true)
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
            mViewModel.appletList()
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.appletList(page++)
        }
    }

    private fun refresh(paging: RPaging<Applet>) {
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

    /**
     * 点击处理
     */
    private fun itemClick(applet: Applet, detail: Boolean = false) {
        if (detail || (ConfigManager.clientConfig.tradeConfig.vipEntry && applet.isNeedVIP && user.role.identity < 100)) {
            // 开启了 VIP 入口 & 需要 VIP 资格 & 当前用户非 VIP 则跳转详情
            CRouter.go(AppRouter.appAppletDetail, obj0 = applet)
        } else {
            if (applet.type == 0) {
                CRouter.goWeb("${applet.url}&version=${applet.versionName}")
            } else {
                if (PermissionManager.storagePermission(this)) {
                    mViewModel.download(applet)
                }
            }
        }
    }

    /**
     * 启动小程序
     */
    private fun startMP(applet: Applet) {
        val filePath = VMFile.filesPath("applet") + applet.body.id + applet.body.extname
        MPManager.unpackMP(this, applet.appId, filePath)
    }
}