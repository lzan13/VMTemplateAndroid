package com.vmloft.develop.library.im.room

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager

import com.drakeet.multitype.MultiTypeAdapter

import com.vmloft.develop.library.base.BFragment
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.data.bean.Gift
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.data.common.DConstants
import com.vmloft.develop.library.gift.GiftFragment
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMConversation
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.common.IMChatManager
import com.vmloft.develop.library.im.chat.msg.*
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.common.IMConversationManager
import com.vmloft.develop.library.im.databinding.ImFragmentChatRoomBinding
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.widget.VMKeyboardController

import java.util.*

/**
 * Create by lzan13 on 2019/05/09 10:11
 * IM 可自定义加载的聊天界面
 */
class IMChatRoomFragment : BFragment<ImFragmentChatRoomBinding>() {

    private val limit = CConstants.defaultLimit

    // 输入面板控制类
    private lateinit var keyboardController: VMKeyboardController

    // 列表适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private lateinit var layoutManager: LinearLayoutManager

    // 会话相关
    private lateinit var chatId: String
    private var chatType: Int = 0

    lateinit var token: String // 加入频道所需 token
    lateinit var channel: String // 频道名

    private lateinit var conversation: IMConversation

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

        // 礼物按钮
        mBinding.imChatGiftIV.setOnClickListener { chooseGift() }
        // 点击发送
        mBinding.imChatSendIV.setOnClickListener { sendText() }

        // 初始化输入面板
        initInputPanel()
        // 初始化列表
        initRecyclerView()

        // 监听礼物赠送成功，这里要发送一条消息
        LDEventBus.observe(this, DConstants.Event.giftGive, Gift::class.java) {
            sendGift(it)
        }
        // 监听新消息过来，这里肯定是发给自己的消息，在发送事件时已经过滤
        LDEventBus.observe(this, IMConstants.Common.newMsgEvent, IMMessage::class.java) {
            refreshNewMsg(it)
        }
        // 监听消息状态刷新，这里肯定是发给自己的消息，在发送事件时已经过滤
        LDEventBus.observe(this, IMConstants.Common.updateMsgEvent, IMMessage::class.java) {
            refreshUpdateMsg(it)
        }
    }

    override fun initData() {
        chatId = requireArguments().getString(argChatId) ?: ""
        chatType = requireArguments().getInt(argChatType, IMConstants.ChatType.imRoom)

        token = ""
        channel = chatId

        initConversation()
        setupGiftFragment()
    }

    /**
     * 初始化会话
     */
    private fun initConversation() {
        // 获取会话对象
        conversation = IMConversationManager.getConversation(chatId, chatType)

        // 清空未读
        IMConversationManager.setConversationUnread(conversation, false)
        val cacheCount = IMConversationManager.getCacheMessages(chatId, chatType).size
        val sumCount = IMConversationManager.getMessagesCount(chatId, chatType)
        if (cacheCount in 1 until sumCount && cacheCount < limit) {
            // 加载更多消息，填充满一页
//            IMChatManager.loadMoreMessages(conversation, limit)
        }
        mItems.clear()
        mItems.addAll(IMConversationManager.getCacheMessages(chatId, chatType))
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

        // 消息点击监听
        val listener = object : BItemDelegate.BItemListener<IMMessage> {
            override fun onClick(v: View, data: IMMessage, position: Int) {
                clickMsg(data, position)
            }
        }

        // 注册各类消息
        mAdapter.register(IMMessage::class).to(
            MsgTextReceiveDelegate(),
            MsgTextSendDelegate(),
            MsgGiftReceiveDelegate(listener),
            MsgGiftSendDelegate(listener),
        ).withKotlinClassLinker { _, data ->
            // 根据消息类型返回不同的 View 展示
            when (IMChatManager.getMsgType(data)) {
                IMConstants.MsgType.imText -> {
                    if (data.isSend) {
                        MsgTextSendDelegate::class
                    } else {
                        MsgTextReceiveDelegate::class
                    }
                }
                IMConstants.MsgType.imGift -> {
                    if (data.isSend) {
                        MsgGiftSendDelegate::class
                    } else {
                        MsgGiftReceiveDelegate::class
                    }
                }
                else -> MsgTextReceiveDelegate::class
            }
        }

        mAdapter.items = mItems

        layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = false
        mBinding.imChatRecyclerView.layoutManager = layoutManager
        mBinding.imChatRecyclerView.adapter = mAdapter

        mBinding.imChatRecyclerView.setOnTouchListener { _, _ ->
            keyboardController.hideKeyboard()
            keyboardController.hideExtendContainer(false, false)
            false
        }
    }

    /**
     * 初始化输入面板
     */
    private fun initInputPanel() {
        mBinding.imChatMessageET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                mBinding.imChatSendIV.visibility = if (s.toString().isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })

        keyboardController = VMKeyboardController(requireActivity())
            .bindExtendContainer(mBinding.imChatExtendContainer)
            .bindContentContainer(mBinding.imChatRefreshLayout)
            .bindEditText(mBinding.imChatMessageET)

        mBinding.imChatGiftIV.setOnClickListener {
            mBinding.imChatEmotionLL.visibility = View.GONE
            if (mBinding.imChatExtendContainer.isShown) {
                if (mBinding.imChatGiftLL.isVisible) {
                    keyboardController.hideExtendContainer(true, true) //隐藏表情布局，显示软件盘
                    mBinding.imChatGiftLL.visibility = View.GONE
                } else {
                    mBinding.imChatGiftLL.visibility = View.VISIBLE
                }
            } else {
                mBinding.imChatGiftLL.visibility = View.VISIBLE
                if (keyboardController.isShowKeyboard()) {
                    keyboardController.showExtendContainer(true)
                } else {
                    keyboardController.showExtendContainer(false) //两者都没显示，直接显示表情布局
                }
                scrollToBottom()
            }
        }
        mBinding.imChatEmotionIV.setOnClickListener {
            mBinding.imChatGiftLL.visibility = View.GONE
            if (mBinding.imChatExtendContainer.isShown) {
                if (mBinding.imChatEmotionLL.isVisible) {
                    keyboardController.hideExtendContainer(true, true) //隐藏表情布局，显示软件盘
                    mBinding.imChatEmotionLL.visibility = View.GONE
                } else {
                    mBinding.imChatEmotionLL.visibility = View.VISIBLE
                }
            } else {
                mBinding.imChatEmotionLL.visibility = View.VISIBLE
                if (keyboardController.isShowKeyboard()) {
                    keyboardController.showExtendContainer(true)
                } else {
                    keyboardController.showExtendContainer(false) //两者都没显示，直接显示表情布局
                }
                scrollToBottom()
            }
        }
    }

    /**
     * 装载礼物内容
     */
    private fun setupGiftFragment() {
        val room = CacheManager.getRoom(chatId)
        // 加载表情界面
        val fragment = GiftFragment.newInstance(room.owner.id)
        val manager: FragmentManager = childFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.replace(R.id.imChatGiftLL, fragment)
        ft.commit()
    }


    /**
     * 新消息刷新
     */
    private fun refreshNewMsg(msg: IMMessage) {
        val msgType = IMChatManager.getMsgType(msg)
        if (msgType == IMConstants.MsgType.imGift) {
            playGiftAnim(msg)
        }
        mItems.add(msg)
        mAdapter.notifyItemInserted(mAdapter.itemCount)
        mBinding.imChatRecyclerView.post { scrollToBottom() }
    }

    /**
     * 刷新消息更新
     */
    private fun refreshUpdateMsg(msg: IMMessage) {
        val position = IMConversationManager.getPosition(msg)
        mAdapter.notifyItemChanged(position)
    }

    /**
     * 加载更多消息
     */
    private fun loadMoreMsg() {
        mBinding.imChatRefreshLayout.finishRefresh()

//        val list = IMChatManager.loadMoreMessages(conversation, limit)
//        mItems.addAll(0, list)
//        mAdapter.notifyItemRangeInserted(0, list.size)
    }

    /**
     * 选择礼物
     */
    private fun chooseGift() {

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
        sendMessage(IMChatManager.createTextMessage(chatId,content))
    }

    /**
     * 发送礼物消息
     */
    private fun sendGift(gift: Gift) {
        val message = IMChatManager.createGiftMessage(chatId,gift )
        sendMessage(message)
    }


    /**
     * 发送消息统一收口
     */
    private fun sendMessage(message: IMMessage) {
        message.chatType = chatType

        IMChatManager.sendMessage(message)
    }

    /**
     * 点击消息事件
     */
    private fun clickMsg(msg: IMMessage, position: Int) {
        val msgType = IMChatManager.getMsgType(msg)
        if (msgType == IMConstants.MsgType.imGift) {
            playGiftAnim(msg)
        } else if (msgType == IMConstants.MsgType.imPicture) {
            // 跳转图片预览
            val attachment = msg.attachments[0]
            CRouter.goDisplaySingle(attachment.path)
        } else if (msgType == IMConstants.MsgType.imVoice) {

        }
    }

    /**
     * 播放礼物动画
     */
    private fun playGiftAnim(msg: IMMessage) {
        // 播放礼物动效，这里直接在新的Activity界面打开
        // 获取礼物消息扩展内容
        val giftStr = msg.getStringAttribute(IMConstants.Common.extGift, "")
        val gift = JsonUtils.fromJson<Gift>(giftStr)
        CRouter.go(IMRouter.imGiftAnim, obj0 = gift)
    }

    /**
     * 滚动到底部
     */
    private fun scrollToBottom() {
        mBinding.imChatRecyclerView.post {
            if (mAdapter.itemCount > 0) {
                mBinding.imChatRecyclerView.smoothScrollToPosition(mAdapter.itemCount - 1)
            }
        }
    }

    /**
     * 添加一个鼓励动画
     */
    private fun addEncourageAnim() {
//        val minSize = VMDimen.dp2px(24)
//        val maxSize = VMDimen.dp2px(36)
//        val randomSize = VMUtils.random(minSize, maxSize)
//        val imageView = ImageView(context)
//        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//        val lp = FrameLayout.LayoutParams(randomSize, randomSize)
//        val x: Int = CUtils.random(mBinding.imRoomEncourageFL.width - randomSize)
//        val y: Int = CUtils.random(mBinding.imRoomEncourageFL.height - randomSize)
//        imageView.x = x.toFloat()
//        imageView.y = y.toFloat()
//        imageView.alpha = 0.0f
//        imageView.setImageResource(R.drawable.im_ic_emotion_flower)
//        mBinding.imRoomEncourageFL.addView(imageView, lp)
//
//        // 动画出现
//        val options = VMAnimator.AnimOptions(imageView, floatArrayOf(0.0f, 1.0f), VMAnimator.alpha, 1000, 3)
//        VMAnimator.createAnimator().play(options).start()
//        VMSystem.runInUIThread({
//            mBinding.imRoomEncourageFL.removeView(imageView)
//        }, 3000)
    }
}