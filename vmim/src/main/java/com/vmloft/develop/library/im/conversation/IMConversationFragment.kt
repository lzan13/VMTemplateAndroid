package com.vmloft.develop.library.im.conversation

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import com.drakeet.multitype.MultiTypeAdapter

import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.base.BFragment
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImFragmentConversationBinding
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.logger.VMLog
import com.vmloft.develop.library.tools.widget.VMFloatMenu

/**
 * Create by lzan13 on 2019/5/9 10:34
 * IM 最近会话界面，可加载到自己的容器
 */
class IMConversationFragment : BFragment<ImFragmentConversationBinding>() {

    // 列表适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<EMConversation>()

    private lateinit var floatMenu: VMFloatMenu

    private lateinit var currConversation: EMConversation


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

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = ImFragmentConversationBinding.inflate(inflater, parent, false)

    override fun initUI() {
        super.initUI()

        initRecyclerView()

        initFloatMenu()

        // 监听链接状态变化
        LDEventBus.observe(this, IMConstants.ConnectStatus.connectStatusEvent, Int::class.java, {
            mBinding.imConversationConnectionLL.visibility = if (it == IMConstants.ConnectStatus.connected) View.GONE else View.VISIBLE
        })
        // 监听新消息
        LDEventBus.observe(this, IMConstants.Common.newMsgEvent, EMMessage::class.java, {
            prepareRefresh()
        })
        // 监听消息撤回
        LDEventBus.observe(this, IMConstants.Common.cmdRecallAction, EMMessage::class.java) {
            prepareRefresh()
        }
        // 监听新消息过来，这里主要更新Tab未读
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
                object : BItemDelegate.BItemListener<EMConversation> {
                    override fun onClick(v: View, data: EMConversation, position: Int) {
                        // 点击去聊天
                        IMRouter.goChat(data.conversationId())
                    }
                },
                object : BItemDelegate.BItemLongListener<EMConversation> {
                    override fun onLongClick(v: View, event: MotionEvent, data: EMConversation, position: Int): Boolean {
                        currConversation = data
                        showFloatMenu(v, event)
                        return true
                    }
                })
        )

        mAdapter.items = mItems
        mAdapter.notifyDataSetChanged()

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
                    0 -> setUnread()
                    1 -> setTop()
                    2 -> remove()
                    3 -> clear()
                }
                refresh()
            }
        })
    }

    /**
     * 设置标为未读
     */
    private fun setUnread() {
        if (IMChatManager.getConversationUnread(currConversation) > 0) {
            IMChatManager.setConversationUnread(currConversation, false)
        } else {
            IMChatManager.setConversationUnread(currConversation, true)
        }
    }

    /**
     * 设置指定状态
     */
    private fun setTop() {
        if (IMChatManager.getConversationTop(currConversation)) {
            IMChatManager.setConversationTop(currConversation, false)
        } else {
            IMChatManager.setConversationTop(currConversation, true)
        }
    }

    /**
     * 删除会话
     */
    private fun remove() {
        IMChatManager.deleteConversation(currConversation.conversationId())
    }

    /**
     * 清空会话
     */
    private fun clear() {
        IMChatManager.clearConversation(currConversation.conversationId())
    }

    /**
     * 弹出菜单
     */
    private fun showFloatMenu(view: View, event: MotionEvent) {
        val unread = if (IMChatManager.getConversationUnread(currConversation) > 0) {
            VMStr.byRes(R.string.im_conversation_read)
        } else {
            VMStr.byRes(R.string.im_conversation_unread)
        }

        val top = if (IMChatManager.getConversationTop(currConversation)) {
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


    private fun loadConversation() {
        val list = IMChatManager.getAllConversation()
        val ids = list.map { it.conversationId() }

        IM.imListener.getUserList(ids) {
            // 刷新结束
            mBinding.imConversationRefreshLayout.finishRefresh()
            refresh()
        }
    }


    private var refreshTime: Long = 0 // 上次刷新时间 ms
    private var refreshDelay: Long = 500 // 刷新延迟时间 ms
    private val refreshWhat: Int = 100

    /**
     *
     */
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            refresh()
        }
    }

    /**
     * 准备刷新
     */
    private fun prepareRefresh() {
        val currTime = System.currentTimeMillis()
        // 先检查下是否有等待刷新的人物
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
        val list = IMChatManager.getAllConversation()

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
