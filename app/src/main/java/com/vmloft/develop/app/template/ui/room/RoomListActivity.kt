package com.vmloft.develop.app.template.ui.room

import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.CacheManager
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityRoomListBinding
import com.vmloft.develop.app.template.im.IMManager
import com.vmloft.develop.app.template.request.bean.Room
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.widget.CommonDialog
import com.vmloft.develop.library.common.widget.StaggeredItemDecoration
import com.vmloft.develop.library.tools.utils.VMDimen

import kotlinx.android.synthetic.main.activity_room_list.*

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create on lzan13 on 2021/05/09 10:10
 * 房间列表
 */
@Route(path = AppRouter.appRoomList)
class RoomListActivity : BVMActivity<RoomViewModel>() {

    private var page: Int = CConstants.defaultPage
    private var limit: Int = CConstants.defaultLimit

    // 适配器
    private val mAdapter by lazy { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private val mLayoutManager by lazy { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }

    override fun initVM(): RoomViewModel = getViewModel()

    override fun layoutId() = R.layout.activity_room_list

    override fun initUI() {
        super.initUI()
        (mBinding as ActivityRoomListBinding).viewModel = mViewModel

        roomListAddIV.setOnClickListener { CRouter.go(AppRouter.appRoomCreate) }

        initRecyclerView()
    }

    override fun initData() {
        ARouter.getInstance().inject(this)
        setTopTitle(R.string.room_list)

        mViewModel.getRoomList()
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemRoomDelegate(object : BItemDelegate.BItemListener<Room> {
            override fun onClick(v: View, data: Room, position: Int) {
                // 进入房间，进行下记录，这里是加入别人创建的
                CacheManager.instance.setLastRoom(data)
                IMManager.instance.goChatRoom(data.id)
            }
        }))

        mAdapter.items = mItems

        recyclerView.layoutManager = mLayoutManager
        recyclerView.addItemDecoration(StaggeredItemDecoration(VMDimen.dp2px(8)))
        recyclerView.adapter = mAdapter
        // 设置下拉刷新
        refreshLayout.setOnRefreshListener {
            refreshLayout.setNoMoreData(false)
            mViewModel.getRoomList()
        }
        // 设置加载更多
        refreshLayout.setOnLoadMoreListener {
            page++
            mViewModel.getRoomList(page)
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (!model.isLoading) {
            refreshLayout.finishRefresh()
            refreshLayout.finishLoadMore()
        }
        if (model.type == "destroyRoom") {
            // 1.销毁的话把缓存里的房间信息也要删掉
            CacheManager.instance.setLastRoom(null, true)
        } else if (model.type == "roomList") {
            refresh(model.data as RPaging<Room>)
        }
    }

    /**
     * 数据刷新
     */
    private fun refresh(paging: RPaging<Room>) {
        if (page == CConstants.defaultPage) {
            checkLastRoom()

            // 将房间信息缓存起来
            CacheManager.instance.resetRoom(paging.data)

            mItems.clear()
            mItems.addAll(paging.data)
            mAdapter.notifyDataSetChanged()
        } else {
            // 将房间信息缓存起来
            CacheManager.instance.resetRoom(paging.data)

            val position = mItems.size
            val count = paging.data.size
            mItems.addAll(paging.data)
            mAdapter.notifyItemRangeInserted(position, count)
        }
        if (paging.currentCount + paging.page * paging.limit >= paging.totalCount) {
            refreshLayout.setNoMoreData(true)
        }
        // 空数据展示
        roomListEmptyIV.visibility = if (mItems.isEmpty()) View.VISIBLE else View.GONE
    }

    /**
     * 检查最后一次加入的房间，如果有给用户一个提示，继续进入，或者取消，取消的话如果是自己创建的进行销毁
     */
    private fun checkLastRoom() {
        val room = CacheManager.instance.getLastRoom() ?: return

        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.backDismissSwitch = false
            dialog.touchDismissSwitch = false
            dialog.setContent(R.string.room_last_crash_hint)
            dialog.setNegative(listener = {
                exitRoom()
            })
            dialog.setPositive(listener = {
                IMManager.instance.goChatRoom(room.id)
            })
            dialog.show()
        }
    }

    /**
     * 退出房间
     */
    private fun exitRoom() {
        val room = CacheManager.instance.getLastRoom() ?: return
        val user = SignManager.instance.getCurrUser() ?: return
        // 检查下是不是自己创建的房间
        if (room.owner.id == user.id) {
            mViewModel.destroyRoom(room.id)
        } else {
            // 2.只是退出，置空下最后加入的房间就好
            CacheManager.instance.setLastRoom(null)
        }
    }

}