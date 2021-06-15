package com.vmloft.develop.library.im.room

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager

import com.drakeet.multitype.MultiTypeAdapter

import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage

import com.scwang.smart.refresh.header.ClassicsHeader

import com.vmloft.develop.library.common.base.BaseFragment
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.chat.msg.*
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.tools.animator.VMAnimator
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMSystem
import com.vmloft.develop.library.tools.utils.VMUtils

import kotlinx.android.synthetic.main.im_fragment_chat_room.*

import java.util.*

/**
 * Create by lzan13 on 2019/05/09 10:11
 *
 * IM 可自定义加载的聊天界面
 */
class IMChatRoomFragment : BaseFragment() {

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

    /**
     * 加载布局
     */
    override fun layoutId(): Int = R.layout.im_fragment_chat_room

    override fun initUI() {
        super.initUI()

        // 发送鼓励
        imRoomEncourageBtn.setOnClickListener { sendEncourage() }
        // 点击发送
        imChatSendBtn.setOnClickListener { sendText() }

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
        chatId = arguments!!.getString(argChatId) ?: ""
        chatType = arguments!!.getInt(argChatType, IMConstants.ChatType.imChatRoom)

        token = ""
        channel = chatId

        initConversation()
    }

    /**
     * 初始化会话
     */
    private fun initConversation() {
        // 获取会话对象
        conversation = IMChatManager.instance.getConversation(chatId, chatType)

        // 清空未读
        IMChatManager.instance.setConversationUnread(conversation, false)
        val cacheCount = IMChatManager.instance.getCacheMessages(chatId, chatType).size
        val sumCount = IMChatManager.instance.getMessagesCount(chatId, chatType)
        if (cacheCount in 1 until sumCount && cacheCount < limit) {
            // 加载更多消息，填充满一页
            IMChatManager.instance.loadMoreMessages(conversation, limit)
        }
        mItems.clear()
        mItems.addAll(IMChatManager.instance.getCacheMessages(chatId, chatType))
        mAdapter.notifyDataSetChanged()
    }


    override fun onResume() {
        super.onResume()
        IMChatManager.instance.setCurrChatId(chatId)
    }

    override fun onPause() {
        super.onPause()
        IMChatManager.instance.setCurrChatId("")
    }

    private fun initRecyclerView() {
        // 下拉监听
        ClassicsHeader.REFRESH_HEADER_PULLING = VMStr.byRes(R.string.im_chat_load_more_header_pulling);//"下拉加载更多";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = VMStr.byRes(R.string.im_chat_load_more_header_refreshing);//"正在刷新...";
        ClassicsHeader.REFRESH_HEADER_LOADING = VMStr.byRes(R.string.im_chat_load_more_header_loading);//"正在加载...";
        ClassicsHeader.REFRESH_HEADER_RELEASE = VMStr.byRes(R.string.im_chat_load_more_header_release);//"释放立即加载";
        ClassicsHeader.REFRESH_HEADER_FINISH = VMStr.byRes(R.string.im_chat_load_more_header_finish);//"加载完成";
        ClassicsHeader.REFRESH_HEADER_FAILED = VMStr.byRes(R.string.im_chat_load_more_header_failed);//"加载失败";
        ClassicsHeader.REFRESH_HEADER_SECONDARY = VMStr.byRes(R.string.im_chat_load_more_header_secondary);//"释放进入二楼";
        ClassicsHeader.REFRESH_HEADER_UPDATE = VMStr.byRes(R.string.im_chat_load_more_header_update);//"上次加载 M-d HH:mm";

        imChatRefreshLayout.setOnRefreshListener { loadMoreMsg() }

        // 注册各类消息
        mAdapter.register(EMMessage::class).to(
            MsgUnsupportedDelegate(),
            MsgTextReceiveDelegate(),
            MsgTextSendDelegate(),
        ).withKotlinClassLinker { _, data ->
            // 根据消息类型返回不同的 View 展示
            when (IMChatManager.instance.getMsgType(data)) {
                IMConstants.MsgType.imTextReceive -> MsgTextReceiveDelegate::class
                IMConstants.MsgType.imTextSend -> MsgTextSendDelegate::class
                else -> MsgUnsupportedDelegate::class
            }
        }

        mAdapter.items = mItems
        mAdapter.notifyDataSetChanged()
        layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        imChatRecyclerView.layoutManager = layoutManager
        imChatRecyclerView.adapter = mAdapter
    }

    /**
     * 设置输入框内容的监听
     */
    private fun initInputWatcher() {
        imChatMessageET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                imChatSendBtn.visibility = if (s.toString().isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })
    }

    /**
     * 新消息刷新
     */
    private fun refreshNewMsg(msg: EMMessage) {
        mItems.add(msg)
        mAdapter.notifyItemInserted(mAdapter.itemCount)
        imChatRecyclerView.post { scrollToBottom() }
    }

    /**
     * 刷新消息更新
     */
    private fun refreshUpdateMsg(msg: EMMessage) {
        val position = IMChatManager.instance.getPosition(msg)
        mAdapter.notifyItemChanged(position)
    }

    /**
     * 加载更多消息
     */
    private fun loadMoreMsg() {
        imChatRefreshLayout.finishRefresh()

        val list = IMChatManager.instance.loadMoreMessages(conversation, limit)
        mItems.addAll(0, list)
        mAdapter.notifyItemRangeInserted(0, list.size)
    }

    /**
     * 发送鼓励
     */
    private fun sendEncourage() {
        // 发送消息
        IMChatManager.instance.sendEncourage(chatId, chatType)
        // 本地播放鼓励动画
        addEncourageAnim()
    }

    /**
     * 发送文本消息
     */
    private fun sendText() {
        val content: String = imChatMessageET.text.toString().trim()
        if (content.isNullOrEmpty()) {
            return errorBar(R.string.im_chat_send_notnull)
        }
        imChatMessageET.setText("")
        // 发送消息
        sendMessage(IMChatManager.instance.createTextMessage(content, chatId))
    }

    /**
     * 发送消息统一收口
     */
    private fun sendMessage(message: EMMessage) {
        message.chatType = IMChatManager.instance.wrapChatType(chatType)

        IMChatManager.instance.sendMessage(message)

        // 通知有新消息，这里主要是通知会话列表刷新
        LDEventBus.post(IMConstants.Common.newMsgEvent, message)
    }

    /**
     * 滚动到底部
     */
    private fun scrollToBottom() {
        imChatRecyclerView.smoothScrollToPosition(mAdapter.itemCount - 1)
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
        val x: Int = CUtils.random(imRoomEncourageFL.width - randomSize)
        val y: Int = CUtils.random(imRoomEncourageFL.height - randomSize)
        imageView.x = x.toFloat()
        imageView.y = y.toFloat()
        imageView.alpha = 0.0f
        imageView.setImageResource(R.drawable.im_ic_emotion_applaud)
        imRoomEncourageFL.addView(imageView, lp)

        // 动画出现
        val options = VMAnimator.createOptions(imageView, VMAnimator.ALPHA, 1000, 3, 0.0f, 1.0f)
        VMAnimator.createAnimator().play(options).start()
        VMSystem.runInUIThread({
            imRoomEncourageFL.removeView(imageView)
        }, 3000)
    }
}