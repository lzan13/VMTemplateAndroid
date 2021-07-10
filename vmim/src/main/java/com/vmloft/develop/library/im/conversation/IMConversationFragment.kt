package com.vmloft.develop.library.im.conversation

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.drakeet.multitype.MultiTypeAdapter
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.base.BaseFragment

import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.logger.VMLog
import com.vmloft.develop.library.tools.widget.VMFloatMenu

import kotlinx.android.synthetic.main.im_fragment_conversation.*

/**
 * Create by lzan13 on 2019/5/9 10:34
 *
 * IM 最近会话界面，可加载到自己的容器
 */
class IMConversationFragment : BaseFragment() {

    // 列表适配器
    private val mAdapter by lazy { MultiTypeAdapter() }
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

    override fun layoutId(): Int = R.layout.im_fragment_conversation

    override fun initUI() {
        super.initUI()

        initRecyclerView()

        initFloatMenu()

        // 监听链接状态变化
        LDEventBus.observe(this, IMConstants.ConnectStatus.connectStatusEvent, Int::class.java, {
            imConversationConnectionLL.visibility = if (it == IMConstants.ConnectStatus.connected) View.GONE else View.VISIBLE
        })
        // 监听新消息
        LDEventBus.observe(this, IMConstants.Common.newMsgEvent, EMMessage::class.java, {
            refresh()
        })
        // 监听消息撤回
        LDEventBus.observe(this, IMConstants.Common.cmdRecallAction, EMMessage::class.java) {
            refresh()
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
        imConversationRefreshLayout.setOnRefreshListener {
            loadConversation()
        }

        // 注册各类消息
        mAdapter.register(
            IMItemConversationDelegate(
                object : BItemDelegate.BItemListener<EMConversation> {
                    override fun onClick(v: View, data: EMConversation, position: Int) {
                        // 点击去聊天
                        IMRouter.goChat(data.conversationId(), data.type.ordinal)
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

        imConversationRecyclerView.adapter = mAdapter
    }

    /**
     * 初始化悬浮菜单
     */
    private fun initFloatMenu() {
        floatMenu = VMFloatMenu(requireActivity())
        floatMenu.setItemClickListener(object : VMFloatMenu.IItemClickListener() {
            override fun onItemClick(id: Int) {
                when (id) {
                    0 -> setUnread()
                    1 -> setTop()
                    2 -> remove()
                    3 -> clear()
                    else -> {
                    }
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

        VMLog.d("-lz-0 获取指定用户信息")
        IM.imListener.getUserList(ids) {
            // 刷新结束
            imConversationRefreshLayout.finishRefresh()
            VMLog.d("-lz-5 获取指定用户信息")
            refresh()
        }
    }

    /**
     * 刷新界面
     */
    private fun refresh() {
        val list = IMChatManager.getAllConversation()

//        val result = DiffUtil.calculateDiff(ConversationDiff(mItems, list), true)
//
//        result.dispatchUpdatesTo(mAdapter)

        mItems.clear()
        mItems.addAll(list)
        mAdapter.notifyDataSetChanged()

    }


}
