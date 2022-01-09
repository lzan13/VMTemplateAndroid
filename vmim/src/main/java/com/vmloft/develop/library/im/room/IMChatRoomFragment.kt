package com.vmloft.develop.library.im.room

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager

import com.drakeet.multitype.MultiTypeAdapter

import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.common.base.BFragment
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.chat.msg.*
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImFragmentChatRoomBinding
import com.vmloft.develop.library.tools.animator.VMAnimator
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMSystem
import com.vmloft.develop.library.tools.utils.VMUtils

import java.util.*

/**
 * Create by lzan13 on 2019/05/09 10:11
 * IM 可自定义加载的聊天界面
 */
class IMChatRoomFragment : BFragment<ImFragmentChatRoomBinding>() {

    private val limit = CConstants.defaultLimit

    // 列表适配器
    private val mAdapter by lazy { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private lateinit var layoutManager: LinearLayoutManager

    // 会话相关
    private lateinit var chatId: String
    private var chatType: Int = 0

    lateinit var token: String // 加入频道所需 token
    lateinit var channel: String // 频道名

    private lateinit var conversation: EMConversation

    companion object {
        private val argChatId = "argChatId"
        private val argChatType = "argChatType"

        /**
         * Fragment 的工厂方法，方便创建并设置参数
         */
        fun newInstance(chatId: String, chatType: Int): IMChatRoomFragment {
            val fragment = IMChatRoomFragment()
            val args = Bundle()
            args.putString(argChatId, chatId)
            args.putInt(argChatType, chatType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = ImFragmentChatRoomBinding.inflate(inflater, parent, false)

    override fun initUI() {
        super.initUI()

        // 发送鼓励
        mBinding.imRoomEncourageBtn.setOnClickListener { sendEncourage() }
        // 点击发送
        mBinding.imChatSendIV.setOnClickListener { sendText() }

        // 初始化输入框监听
        initInputWatcher()
        // 初始化列表
        initRecyclerView()

        // 监听新消息过来，这里肯定是发给自己的消息，在发送事件时已经过滤
        LDEventBus.observe(this, IMConstants.Common.newMsgEvent, EMMessage::class.java) {
            refreshNewMsg(it)
        }
        // 监听消息状态刷新，这里肯定是发给自己的消息，在发送事件时已经过滤
        LDEventBus.observe(this, IMConstants.Common.updateMsgEvent, EMMessage::class.java) {
            refreshUpdateMsg(it)
        }
        // 监听鼓励消息
        LDEventBus.observe(this, IMConstants.Common.cmdEncourageAction, EMMessage::class.java) {
            addEncourageAnim()
        }

    }

    override fun initData() {
        chatId = requireArguments().getString(argChatId) ?: ""
        chatType = requireArguments().getInt(argChatType, IMConstants.ChatType.imChatRoom)

        token = ""
        channel = chatId

        initConversation()
    }

    /**
     * 初始化会话
     */
    private fun initConversation() {
        // 获取会话对象
        conversation = IMChatManager.getConversation(chatId, chatType)

        // 清空未读
        IMChatManager.setConversationUnread(conversation, false)
        val cacheCount = IMChatManager.getCacheMessages(chatId, chatType).size
        val sumCount = IMChatManager.getMessagesCount(chatId, chatType)
        if (cacheCount in 1 until sumCount && cacheCount < limit) {
            // 加载更多消息，填充满一页
            IMChatManager.loadMoreMessages(conversation, limit)
        }
        mItems.clear()
        mItems.addAll(IMChatManager.getCacheMessages(chatId, chatType))
        mAdapter.notifyDataSetChanged()
    }


    override fun onResume() {
        super.onResume()
        IMChatManager.setCurrChatId(chatId)
    }

    override fun onPause() {
        super.onPause()
        IMChatManager.setCurrChatId("")
    }

    private fun initRecyclerView() {
        mBinding.imChatRefreshLayout.setOnRefreshListener { loadMoreMsg() }

        // 注册各类消息
        mAdapter.register(EMMessage::class).to(
            MsgUnsupportedDelegate(),
            MsgTextReceiveDelegate(),
            MsgTextSendDelegate(),
        ).withKotlinClassLinker { _, data ->
            // 根据消息类型返回不同的 View 展示
            when (IMChatManager.getMsgType(data)) {
                IMConstants.MsgType.imTextReceive -> MsgTextReceiveDelegate::class
                IMConstants.MsgType.imTextSend -> MsgTextSendDelegate::class
                else -> MsgUnsupportedDelegate::class
            }
        }

        mAdapter.items = mItems
        mAdapter.notifyDataSetChanged()
        layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        mBinding.imChatRecyclerView.layoutManager = layoutManager
        mBinding.imChatRecyclerView.adapter = mAdapter
    }

    /**
     * 设置输入框内容的监听
     */
    private fun initInputWatcher() {
        mBinding.imChatMessageET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                mBinding.imChatSendIV.visibility = if (s.toString().isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })
    }

    /**
     * 新消息刷新
     */
    private fun refreshNewMsg(msg: EMMessage) {
        mItems.add(msg)
        mAdapter.notifyItemInserted(mAdapter.itemCount)
        mBinding.imChatRecyclerView.post { scrollToBottom() }
    }

    /**
     * 刷新消息更新
     */
    private fun refreshUpdateMsg(msg: EMMessage) {
        val position = IMChatManager.getPosition(msg)
        mAdapter.notifyItemChanged(position)
    }

    /**
     * 加载更多消息
     */
    private fun loadMoreMsg() {
        mBinding.imChatRefreshLayout.finishRefresh()

        val list = IMChatManager.loadMoreMessages(conversation, limit)
        mItems.addAll(0, list)
        mAdapter.notifyItemRangeInserted(0, list.size)
    }

    /**
     * 发送鼓励
     */
    private fun sendEncourage() {
        // 发送消息
        IMChatManager.sendEncourage(chatId, chatType)
        // 本地播放鼓励动画
        addEncourageAnim()
    }

    /**
     * 发送文本消息
     */
    private fun sendText() {
        val content: String = mBinding.imChatMessageET.text.toString().trim()
        if (content.isNullOrEmpty()) {
            return errorBar(R.string.im_chat_send_notnull)
        }
        mBinding.imChatMessageET.setText("")
        // 发送消息
        sendMessage(IMChatManager.createTextMessage(content, chatId))
    }

    /**
     * 发送消息统一收口
     */
    private fun sendMessage(message: EMMessage) {
        message.chatType = IMChatManager.wrapChatType(chatType)

        IMChatManager.sendMessage(message)

        // 通知有新消息，这里主要是通知会话列表刷新
        LDEventBus.post(IMConstants.Common.newMsgEvent, message)
    }

    /**
     * 滚动到底部
     */
    private fun scrollToBottom() {
        mBinding.imChatRecyclerView.smoothScrollToPosition(mAdapter.itemCount - 1)
    }

    /**
     * 添加一个鼓励动画
     */
    private fun addEncourageAnim() {
        val minSize = VMDimen.dp2px(24)
        val maxSize = VMDimen.dp2px(36)
        val randomSize = VMUtils.random(minSize, maxSize)
        val imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        val lp = FrameLayout.LayoutParams(randomSize, randomSize)
        val x: Int = CUtils.random(mBinding.imRoomEncourageFL.width - randomSize)
        val y: Int = CUtils.random(mBinding.imRoomEncourageFL.height - randomSize)
        imageView.x = x.toFloat()
        imageView.y = y.toFloat()
        imageView.alpha = 0.0f
        imageView.setImageResource(R.drawable.im_ic_emotion_applaud)
        mBinding.imRoomEncourageFL.addView(imageView, lp)

        // 动画出现
        val options = VMAnimator.AnimOptions(imageView, floatArrayOf(0.0f, 1.0f), VMAnimator.alpha, 1000, 3)
        VMAnimator.createAnimator().play(options).start()
        VMSystem.runInUIThread({
            mBinding.imRoomEncourageFL.removeView(imageView)
        }, 3000)
    }
}