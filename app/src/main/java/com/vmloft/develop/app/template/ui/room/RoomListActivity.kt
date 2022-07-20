package com.vmloft.develop.app.template.ui.room

import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivityRoomListBinding
import com.vmloft.develop.app.template.im.IMManager
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.base.widget.decoration.StaggeredItemDecoration
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.bean.Room
import com.vmloft.develop.library.data.viewmodel.RoomViewModel
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.tools.utils.VMDimen

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create on lzan13 on 2021/05/09 10:10
 * 房间列表
 */
@Route(path = AppRouter.appRoomList)
class RoomListActivity : BVMActivity<ActivityRoomListBinding, RoomViewModel>() {

    private var page: Int = CConstants.defaultPage
    private var limit: Int = CConstants.defaultLimit

    // 适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private val mLayoutManager by lazy(LazyThreadSafetyMode.NONE) { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }

    override fun initVB() = ActivityRoomListBinding.inflate(layoutInflater)

    override fun initVM(): RoomViewModel = getViewModel()

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.room_list)

        mBinding.roomListAddIV.setOnClickListener { CRouter.go(AppRouter.appRoomCreate) }

        initRecyclerView()
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        mBinding.refreshLayout.autoRefresh()
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemRoomDelegate(object : BItemDelegate.BItemListener<Room> {
            override fun onClick(v: View, data: Room, position: Int) {
                // 进入房间，进行下记录，这里是加入别人创建的
                CacheManager.setLastRoom(data)
                IMManager.goChatRoom(data.id)
            }
        }))

        mAdapter.items = mItems

        mBinding.recyclerView.layoutManager = mLayoutManager
        mBinding.recyclerView.addItemDecoration(StaggeredItemDecoration(VMDimen.dp2px(8)))
        mBinding.recyclerView.adapter = mAdapter
        // 设置下拉刷新
        mBinding.refreshLayout.setOnRefreshListener {
            mBinding.refreshLayout.setNoMoreData(false)
            page = CConstants.defaultPage
            mViewModel.roomList()
        }
        // 设置加载更多
        mBinding.refreshLayout.setOnLoadMoreListener {
            page++
            mViewModel.roomList(page)
        }
    }

    /**
     * 数据刷新
     */
    private fun refresh(paging: RPaging<Room>) {
        if (page == CConstants.defaultPage) {
            checkLastRoom()

            // 将房间信息缓存起来
            CacheManager.resetRoom(paging.data)

            mItems.clear()
            mItems.addAll(paging.data)
            mAdapter.notifyDataSetChanged()
        } else {
            // 将房间信息缓存起来
            CacheManager.resetRoom(paging.data)

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
        if (model.type == "destroyRoom") {
            // 1.销毁的话把缓存里的房间信息也要删掉
            CacheManager.setLastRoom(null, true)
        } else if (model.type == "roomList") {
            refresh(model.data as RPaging<Room>)
        }
    }

    override fun onModelError(model: BViewModel.UIModel) {
        super.onModelError(model)
        checkEmptyStatus(1)
    }

    /**
     * 检查最后一次加入的房间，如果有给用户一个提示，继续进入，或者取消，取消的话如果是自己创建的进行销毁
     */
    private fun checkLastRoom() {
        val room = CacheManager.getLastRoom() ?: return

        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.backDismissSwitch = false
            dialog.touchDismissSwitch = false
            dialog.setContent(R.string.room_last_crash_hint)
            dialog.setNegative(listener = {
                exitRoom()
            })
            dialog.setPositive(listener = {
                IMManager.goChatRoom(room.id)
            })
            dialog.show()
        }
    }

    /**
     * 退出房间
     */
    private fun exitRoom() {
        val room = CacheManager.getLastRoom() ?: return
        val user = SignManager.getCurrUser()
        // 检查下是不是自己创建的房间
        if (room.owner.id == user.id) {
            mViewModel.destroyRoom(room.id)
        } else {
            // 2.只是退出，置空下最后加入的房间就好
            CacheManager.setLastRoom(null)
        }
    }

}