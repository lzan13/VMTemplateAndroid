package com.vmloft.develop.library.im.conversation

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMFragment
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.data.viewmodel.UserViewModel
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMConversation
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.common.IMConversationManager
import com.vmloft.develop.library.im.databinding.ImFragmentConversationBinding
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.logger.VMLog
import com.vmloft.develop.library.tools.widget.VMFloatMenu

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2019/5/9 10:34
 * 描述：IM 最近会话界面，可加载到自己的容器
 */
class IMConversationFragment : BVMFragment<ImFragmentConversationBinding, UserViewModel>() {

    // 列表适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<IMConversation>()

    private lateinit var floatMenu: VMFloatMenu

    private lateinit var currConversation: IMConversation


    private var refreshTime: Long = 0 // 上次刷新时间 ms
    private var refreshDelay: Long = 200 // 刷新延迟时间 ms
    private val refreshWhat: Int = 100

    /**
     * 处理延迟刷新
     */
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            refresh()
        }
    }


    companion object {
        /**
         * Fragment 的工厂方法，方便创建并设置参数
         */
        fun newInstance(): IMConversationFragment {
            val fragment = IMConversationFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initVM(): UserViewModel = getViewModel()

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = ImFragmentConversationBinding.inflate(inflater, parent, false)

    override fun initUI() {
        super.initUI()

        initRecyclerView()

        initFloatMenu()

        // 监听链接状态变化
        LDEventBus.observe(this, IMConstants.Common.connectStatusEvent, Boolean::class.java) {
            mBinding.imConversationConnectionLL.visibility = if (it) View.GONE else View.VISIBLE
        }
        // 监听新消息
        LDEventBus.observe(this, IMConstants.Common.newMsgEvent, IMMessage::class.java) {
            prepareRefresh()
        }
        // 监听消息改变
        LDEventBus.observe(this, IMConstants.Common.updateMsgEvent, IMMessage::class.java) {
            prepareRefresh()
        }
        // 监听未读变化
        LDEventBus.observe(this, IMConstants.Common.changeUnreadCount, Int::class.java) {
            prepareRefresh()
        }
    }

    override fun initData() {
        loadConversation()
    }

    /**
     * 初始化会话列表
     */
    private fun initRecyclerView() {
        // 刷新监听
        mBinding.imConversationRefreshLayout.setOnRefreshListener {
            loadConversation()
        }

        // 注册各类消息
        mAdapter.register(
            IMItemConversationDelegate(
                object : BItemDelegate.BItemListener<IMConversation> {
                    override fun onClick(v: View, data: IMConversation, position: Int) {
                        // 点击去聊天
                        IMRouter.goChat(data.chatId)
                    }
                },
                object : BItemDelegate.BItemLongListener<IMConversation> {
                    override fun onLongClick(v: View, event: MotionEvent, data: IMConversation, position: Int): Boolean {
                        currConversation = data
                        showFloatMenu(v, event)
                        return true
                    }
                })
        )

        mAdapter.items = mItems

        mBinding.imConversationRecyclerView.adapter = mAdapter
    }

    /**
     * 初始化悬浮菜单
     */
    private fun initFloatMenu() {
        floatMenu = VMFloatMenu(requireActivity())
        floatMenu.setMenuBackground(R.drawable.shape_card_common_bg)
        floatMenu.setItemClickListener(object : VMFloatMenu.IItemClickListener() {
            override fun onItemClick(id: Int) {
                when (id) {
                    0 -> changeUnread()
                    1 -> changeTop()
                    2 -> remove()
                    3 -> clear()
                }
                refresh()
            }
        })
    }

    private fun loadConversation() {
        val list = IMConversationManager.getAllConversation()

        // 请求列表用户信息
        val ids = list.map { it.chatId }
        mViewModel.userList(ids)
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        if (model.type == "userList" && !model.isLoading) {
            mBinding.imConversationRefreshLayout.finishRefresh()
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "userList") {
            prepareRefresh()
        }
    }

    /**
     * 改变未读
     */
    private fun changeUnread() {
        if (currConversation.unread > 0) {
            IMConversationManager.setConversationUnread(currConversation, false)
        } else {
            IMConversationManager.setConversationUnread(currConversation, true)
        }
    }

    /**
     * 改变置顶状态
     */
    private fun changeTop() {
        IMConversationManager.setConversationTop(currConversation, !currConversation.top)
    }

    /**
     * 删除会话
     */
    private fun remove() {
        IMConversationManager.deleteConversation(currConversation)
    }

    /**
     * 清空会话消息
     */
    private fun clear() {
        IMConversationManager.clearMsg(currConversation)
    }

    /**
     * 弹出菜单
     */
    private fun showFloatMenu(view: View, event: MotionEvent) {
        val unread = if (currConversation.unread > 0) {
            VMStr.byRes(R.string.im_conversation_read)
        } else {
            VMStr.byRes(R.string.im_conversation_unread)
        }

        val top = if (currConversation.top) {
            VMStr.byRes(R.string.im_conversation_untop)
        } else {
            VMStr.byRes(R.string.im_conversation_top)
        }

        floatMenu.clearAllItem()
        floatMenu.addItem(VMFloatMenu.ItemBean(0, unread))
        floatMenu.addItem(VMFloatMenu.ItemBean(1, top))
        floatMenu.addItem(VMFloatMenu.ItemBean(2, VMStr.byRes(R.string.im_conversation_remove)))
        floatMenu.addItem(VMFloatMenu.ItemBean(3, VMStr.byRes(R.string.im_conversation_clear)))

        floatMenu.showAtLocation(view, event.x.toInt(), event.y.toInt())
    }

    /**
     * 准备刷新
     */
    private fun prepareRefresh() {
        val currTime = System.currentTimeMillis()
        // 先检查下是否有等待刷新的任务
        if (handler.hasMessages(refreshWhat)) {
            // 在检查下距离刷新还有多久
            if (currTime - refreshTime >= refreshDelay) {
                refreshTime = currTime
                handler.removeMessages(refreshWhat)
                handler.sendEmptyMessageDelayed(refreshWhat, refreshDelay)
            }
        } else {
            refreshTime = currTime
            handler.sendEmptyMessageDelayed(refreshWhat, refreshDelay)
        }
    }

    /**
     * 刷新界面
     */
    private fun refresh() {
        VMLog.i("-lz-refresh- 3 ")
        val list = IMConversationManager.getAllConversation()

//        val result = DiffUtil.calculateDiff(ConversationDiff(mItems, list), true)
//
//        result.dispatchUpdatesTo(mAdapter)

        mItems.clear()
        mItems.addAll(list)
        mAdapter.notifyDataSetChanged()

        checkEmpty()
    }

    private fun checkEmpty() {
        if (mItems.isEmpty()) {
            showEmptyNoData()
        } else {
            hideEmptyView()
        }
    }
}
