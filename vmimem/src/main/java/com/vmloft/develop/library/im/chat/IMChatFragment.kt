package com.vmloft.develop.library.im.chat

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager

import com.drakeet.multitype.MultiTypeAdapter

import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMImageMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody

import com.vmloft.develop.library.base.BFragment
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.common.PermissionManager
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.data.bean.Gift
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.DConstants
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.gift.GiftFragment
import com.vmloft.develop.library.gift.GiftRouter
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.msg.*
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImFragmentChatBinding
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.image.IMGChoose
import com.vmloft.develop.library.tools.utils.VMDate
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMSystem
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap
import com.vmloft.develop.library.tools.widget.VMFloatMenu
import com.vmloft.develop.library.tools.widget.VMKeyboardController
import com.vmloft.develop.library.tools.widget.record.VMRecordView


/**
 * Create by lzan13 on 2019/05/09 10:11
 * 描述：IM 可自定义加载的聊天界面
 */
class IMChatFragment : BFragment<ImFragmentChatBinding>() {

    private val limit = CConstants.defaultLimit

    // 输入面板控制类
    private lateinit var keyboardController: VMKeyboardController

    // 列表适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private lateinit var layoutManager: LinearLayoutManager

    // 记录上次发送输入状态的时间
    private var lastTimeInputStatus: Long = 0

    // 消息长按弹出菜单
    private lateinit var floatMenu: VMFloatMenu
    private lateinit var currMsg: EMMessage
    private var currPosition: Int = 0

    // 会话相关
    private var chatId: String = ""
    private var chatType: Int = 0
    private var chatExtend: String = ""
    private lateinit var conversation: EMConversation

    private lateinit var selfUser: User

    private var receiveMsgCount = 0 // 接收消息数
    private var sendMsgCount = 0 // 发送消息数

    companion object {
        private val argChatId = "argChatId"
        private val argChatType = "argChatType"
        private val argChatExtend = "argChatExtend"

        /**
         * Fragment 的工厂方法，方便创建并设置参数
         */
        fun newInstance(chatId: String, chatType: Int, extend: String): IMChatFragment {
            val fragment = IMChatFragment()
            val args = Bundle()
            args.putString(argChatId, chatId)
            args.putInt(argChatType, chatType)
            args.putString(argChatExtend, extend)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = ImFragmentChatBinding.inflate(inflater, parent, false)

    override fun initUI() {
        super.initUI()

        initView()
        // 初始化长按菜单
        initFloatMenu()

        // 监听礼物赠送成功，这里要发送一条消息
        LDEventBus.observe(this, DConstants.Event.giftGive, Gift::class.java) {
            sendGift(it)
        }

        // 监听新消息过来，这里肯定是发给自己的消息，在发送事件时已经过滤
        LDEventBus.observe(this, IMConstants.Common.newMsgEvent, EMMessage::class.java) {
            it?.let {
                if (it.direct() == EMMessage.Direct.RECEIVE) {
                    receiveMsgCount++
                } else {
                    sendMsgCount++
                }
            }
            refreshNewMsg(it)
        }

        // 监听消息状态刷新，这里肯定是发给自己的消息，在发送事件时已经过滤
        LDEventBus.observe(this, IMConstants.Common.updateMsgEvent, EMMessage::class.java) {
            refreshUpdateMsg(it)
        }

        // 监听消息撤回
        LDEventBus.observe(this, IMConstants.Common.cmdRecallAction, EMMessage::class.java) {
            refreshUpdateMsg(it)
        }

    }

    override fun initData() {
        chatId = requireArguments().getString(argChatId) ?: ""
        chatType = requireArguments().getInt(argChatType, 0)
        chatExtend = requireArguments().getString(argChatExtend, "")

        selfUser = SignManager.getSignUser()

        initConversation()
        setupGiftFragment()
    }

    override fun onResume() {
        super.onResume()

        IMChatManager.setCurrChatId(chatId)

        // 检查是否有草稿没有发出
        val draft = IMChatManager.getConversationDraft(conversation)
        if (!draft.isNullOrEmpty()) {
            mBinding.imChatMessageET.setText(draft)
        }
    }

    override fun onPause() {
        super.onPause()
        IMChatManager.setCurrChatId("")
        // 清空未读
        IMChatManager.setConversationUnread(conversation, false)
        /**
         * 判断聊天输入框内容是否为空，不为空就保存输入框内容到[EMConversation]的扩展中
         * 调用[IMChatManager.setConversationDraft]方法
         */
        val draft = mBinding.imChatMessageET.text.toString().trim()
        // 将输入框的内容保存为草稿
        IMChatManager.setConversationDraft(conversation, draft)
    }

    private fun initView() {
        mBinding.imChatVoiceIV.visibility = if (ConfigManager.appConfig.chatConfig.voiceEntry) View.VISIBLE else View.GONE
        mBinding.imChatPictureIV.visibility = if (ConfigManager.appConfig.chatConfig.pictureEntry) View.VISIBLE else View.GONE
        mBinding.imChatCallIV.visibility = if (ConfigManager.appConfig.chatConfig.callEntry) View.VISIBLE else View.GONE
        mBinding.imChatGiftIV.visibility = if (ConfigManager.appConfig.chatConfig.giftEntry) View.VISIBLE else View.GONE

        // 选择图片
        mBinding.imChatPictureIV.setOnClickListener { openAlbum() }
        // 开启通话申请
        mBinding.imChatCallIV.setOnClickListener { requestCall() }
        // 点击发送
        mBinding.imChatSendIV.setOnClickListener { sendText() }

        // 初始化输入面板
        initInputPanel()
        // 初始化列表
        initRecyclerView()
    }


    /**
     * 设置输入框内容的监听
     */
    private fun initInputPanel() {
        mBinding.imChatRecordView.setRecordListener(object : VMRecordView.RecordListener() {
            override fun onStart() {}

            override fun onCancel() {
//                showBar("录音取消")
            }

            override fun onError(code: Int, desc: String) {
                errorBar("录音失败 $code $desc")
            }

            override fun onComplete(path: String, time: Int) {
//                showBar("录音完成 $path $time")
                sendVoice(path, time)
            }
        })

        mBinding.imChatMessageET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                changeInputStatus(true)
                // 发送正在输入，变为空则不发送输入状态
                if (!s.toString().isEmpty()) {
                    // 输入状态的展示时间是 3 秒，检查一下，如果间隔不足两秒，则不重新发送
                    val time = System.currentTimeMillis()
                    if (time - lastTimeInputStatus > CConstants.timeSecond * 2) {
                        lastTimeInputStatus = time
                        IMChatManager.sendInputStatus(chatId)
                    }
                }
                mBinding.imChatSendIV.visibility = if (s.toString().isEmpty()) View.GONE else View.VISIBLE
            }
        })

        keyboardController = VMKeyboardController(requireActivity())
            .bindExtendContainer(mBinding.imChatExtendContainer)
            .bindContentContainer(mBinding.imChatRefreshLayout)
            .bindEditText(mBinding.imChatMessageET)

        mBinding.imChatExtendArrowIV.setOnClickListener { changeInputStatus() }
        mBinding.imChatVoiceIV.setOnClickListener {
            if (!checkVoiceLockStatus()) {
                showLimitDialog(ConfigManager.appConfig.chatConfig.voiceLimit)
                return@setOnClickListener
            }
            mBinding.imChatGiftLL.visibility = View.GONE
            mBinding.imChatEmotionLL.visibility = View.GONE
            if (mBinding.imChatExtendContainer.isShown) {
                if (mBinding.imChatRecordView.isVisible) {
                    keyboardController.hideExtendContainer(true, true) //隐藏表情布局，显示软件盘
                    mBinding.imChatRecordView.visibility = View.GONE
                } else {
                    mBinding.imChatRecordView.visibility = View.VISIBLE
                }
            } else {
                mBinding.imChatRecordView.visibility = View.VISIBLE
                if (keyboardController.isShowKeyboard()) {
                    keyboardController.showExtendContainer(true)
                } else {
                    keyboardController.showExtendContainer(false) //两者都没显示，直接显示表情布局
                }
                scrollToBottom()
            }
        }
        mBinding.imChatGiftIV.setOnClickListener {
            mBinding.imChatRecordView.visibility = View.GONE
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
            mBinding.imChatRecordView.visibility = View.GONE
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
        // 加载表情界面
        val fragment = GiftFragment.newInstance(chatId)
        val manager: FragmentManager = childFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.replace(R.id.imChatGiftLL, fragment)
        ft.commit()
    }

    /**
     * 初始化列表
     */
    private fun initRecyclerView() {
        mBinding.imChatRefreshLayout.setOnRefreshListener { loadMoreMsg() }

        // 消息点击监听
        val listener = object : BItemDelegate.BItemListener<EMMessage> {
            override fun onClick(v: View, data: EMMessage, position: Int) {
                clickMsg(data, position)
            }
        }
        // 消息长按监听
        val longListener = object : BItemDelegate.BItemLongListener<EMMessage> {
            override fun onLongClick(v: View, event: MotionEvent, data: EMMessage, position: Int): Boolean {
                currMsg = data
                currPosition = position
                showFloatMenu(v, event)
                return true
            }
        }
        // 注册各类消息
        mAdapter.register(EMMessage::class).to(
            MsgUnsupportedDelegate(),
            MsgSystemDelegate(),
            MsgCallReceiveDelegate(longListener),
            MsgCallSendDelegate(longListener),
            MsgGiftReceiveDelegate(listener, longListener),
            MsgGiftSendDelegate(listener, longListener),
            MsgPictureReceiveDelegate(listener, longListener),
            MsgPictureSendDelegate(listener, longListener),
            MsgTextReceiveDelegate(longListener),
            MsgTextSendDelegate(longListener),
            MsgVoiceReceiveDelegate(longListener),
            MsgVoiceSendDelegate(longListener),
        ).withKotlinClassLinker { _, data ->
            // 根据消息类型返回不同的 View 展示
            when (IMChatManager.getMsgType(data)) {
                IMConstants.MsgType.imSystem -> MsgSystemDelegate::class
                IMConstants.MsgType.imRecall -> MsgSystemDelegate::class
                IMConstants.MsgType.imCallReceive -> MsgCallReceiveDelegate::class
                IMConstants.MsgType.imCallSend -> MsgCallSendDelegate::class
                IMConstants.MsgType.imGiftReceive -> MsgGiftReceiveDelegate::class
                IMConstants.MsgType.imGiftSend -> MsgGiftSendDelegate::class
                IMConstants.MsgType.imPictureReceive -> MsgPictureReceiveDelegate::class
                IMConstants.MsgType.imPictureSend -> MsgPictureSendDelegate::class
                IMConstants.MsgType.imTextReceive -> MsgTextReceiveDelegate::class
                IMConstants.MsgType.imTextSend -> MsgTextSendDelegate::class
                IMConstants.MsgType.imVoiceReceive -> MsgVoiceReceiveDelegate::class
                IMConstants.MsgType.imVoiceSend -> MsgVoiceSendDelegate::class
                else -> MsgUnsupportedDelegate::class
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
     * 初始化会话
     */
    private fun initConversation() {
        // 获取会话对象
        conversation = IMChatManager.getConversation(chatId, chatType)
        // 保存扩展信息
        saveExtendMsg()

        receiveMsgCount = IMChatManager.getConversationMsgReceiveCount(conversation)
        sendMsgCount = IMChatManager.getConversationMsgSendCount(conversation)

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
        // 第一页内容加载完成需要滚动到底部
        mBinding.imChatRecyclerView.post {
            scrollToBottom()
        }
    }

    /**
     * 根据传入参数判断需不需要保存扩展信息
     */
    private fun saveExtendMsg() {
        if (chatExtend.isNullOrEmpty() || IMChatManager.getMessagesCount(chatId, chatType) > 0) return

        val message = IMChatManager.createTextMessage(chatExtend, chatId, false)
        message.chatType = EMMessage.ChatType.Chat
        message.setStatus(EMMessage.Status.SUCCESS)

        IMChatManager.saveMessage(message)
    }

    /**
     * 输入状态改变
     */
    private fun changeInputStatus(showArrow: Boolean = false) {
        if (showArrow) {
            mBinding.imChatExtendArrowIV.visibility = View.VISIBLE
            mBinding.imChatVoiceIV.visibility = View.GONE
            mBinding.imChatPictureIV.visibility = View.GONE
            mBinding.imChatCallIV.visibility = View.GONE
            mBinding.imChatGiftIV.visibility = View.GONE
        } else {
            mBinding.imChatExtendArrowIV.visibility = View.GONE
            mBinding.imChatVoiceIV.visibility = if (ConfigManager.appConfig.chatConfig.voiceEntry) View.VISIBLE else View.GONE
            mBinding.imChatPictureIV.visibility = if (ConfigManager.appConfig.chatConfig.pictureEntry) View.VISIBLE else View.GONE
            mBinding.imChatCallIV.visibility = if (ConfigManager.appConfig.chatConfig.callEntry) View.VISIBLE else View.GONE
            mBinding.imChatGiftIV.visibility = if (ConfigManager.appConfig.chatConfig.giftEntry) View.VISIBLE else View.GONE
        }
    }

    /**
     * 检查锁状态
     */
    private fun checkVoiceLockStatus(): Boolean {
        // VIP 不限制发送图片和语音通话
        if (selfUser.role.identity in 100..199) {
            return true
        }
        return receiveMsgCount > ConfigManager.appConfig.chatConfig.voiceLimit && sendMsgCount > ConfigManager.appConfig.chatConfig.voiceLimit
    }

    /**
     * 检查锁状态
     */
    private fun checkPictureLockStatus(): Boolean {
        // VIP 不限制发送图片和语音通话
        if (selfUser.role.identity in 100..199) {
            return true
        }
        return receiveMsgCount > ConfigManager.appConfig.chatConfig.pictureLimit && sendMsgCount > ConfigManager.appConfig.chatConfig.pictureLimit
    }

    /**
     * 检查锁状态
     */
    private fun checkCallLockStatus(): Boolean {
        // VIP 不限制发送图片和语音通话
        if (selfUser.role.identity in 100..199) {
            return true
        }
        return receiveMsgCount >= ConfigManager.appConfig.chatConfig.callLimit && sendMsgCount >= ConfigManager.appConfig.chatConfig.callLimit
    }

    /**
     * 打开相册选择图片发送
     */
    private fun openAlbum() {
        // 必须有读写存储权限才能选择图片
        if (PermissionManager.storagePermission(requireContext())) {
            if (!checkPictureLockStatus()) {
                showLimitDialog(ConfigManager.appConfig.chatConfig.pictureLimit)
            } else {
                IMGChoose.singlePicture(requireActivity()) { sendPicture(it as Uri) }
            }
        }
    }

    /**
     * 请求通话
     */
    private fun requestCall() {
        // 必须有录音权限才能进行通话
        if (PermissionManager.recordPermission(requireContext())) {
            if (!checkCallLockStatus()) {
                showLimitDialog(ConfigManager.appConfig.chatConfig.callLimit)
            } else {
                IMRouter.goSingleCall(chatId)
            }
        }
    }

    /**
     * 显示通话限制对话框
     */
    private fun showLimitDialog(limit: Int) {
        mDialog = CommonDialog(requireActivity())
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(VMStr.byResArgs(R.string.im_chat_extend_limit, limit))
            dialog.setNegative("")
            dialog.setPositive(VMStr.byRes(R.string.btn_i_known))
            dialog.show()
        }
    }

    /**
     * 新消息刷新
     */
    private fun refreshNewMsg(msg: EMMessage) {
        val msgType = IMChatManager.getMsgType(msg)
        if (msgType == IMConstants.MsgType.imGiftReceive) {
            playGiftAnim(msg)
        }
        mItems.add(msg)
        mAdapter.notifyItemInserted(mAdapter.itemCount)
        mBinding.imChatRecyclerView.post { scrollToBottom() }
    }


    /**
     * 刷新消息删除
     */
    private fun refreshDeleteMsg(msg: EMMessage, position: Int) {
        mItems.remove(msg)
        mAdapter.notifyItemRemoved(position)
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
     * 发送文本消息
     */
    private fun sendText() {
        val content: String = mBinding.imChatMessageET.text.toString().trim()
        if (content.isNullOrEmpty()) {
            return errorBar(R.string.im_chat_send_notnull)
        }
        mBinding.imChatMessageET.setText("")
        lastTimeInputStatus = 0
        // 发送消息
        sendMessage(IMChatManager.createTextMessage(content, chatId))

        changeInputStatus()
    }

    /**
     * 发送语音消息
     */
    private fun sendVoice(path: String, time: Int) {
        val uri = Uri.parse(path)
        sendMessage(IMChatManager.createVoiceMessage(uri, time, chatId))
    }

    /**
     * 发送图片消息
     */
    private fun sendPicture(uri: Uri) {
        // 临时压缩下图片，这里压缩到默认的分辨率
        val tempPath = VMBitmap.compressTempImage(uri, 2048, 256)
        sendMessage(IMChatManager.createPictureMessage(Uri.parse(tempPath), chatId))
    }

    /**
     * 发送礼物消息
     */
    private fun sendGift(gift: Gift) {
        val giftStr = JsonUtils.toJson(gift)
        val message = IMChatManager.createTextMessage(gift.title, chatId)
        message.setAttribute(IMConstants.Common.msgAttrExtType, IMConstants.MsgType.imGift)
        message.setAttribute(IMConstants.Common.msgAttrGift, giftStr)
        sendMessage(message)
    }

    /**
     * 发送消息统一收口
     */
    private fun sendMessage(message: EMMessage) {
        IMChatManager.sendMessage(message)

        // 发送消息数+1
        IMChatManager.setConversationMsgSendCountAdd(conversation)

        // 通知有新消息，这里主要是通知会话列表刷新
        LDEventBus.post(IMConstants.Common.newMsgEvent, message)
    }

    /**
     * 点击消息事件
     */
    private fun clickMsg(msg: EMMessage, position: Int) {
        val msgType = IMChatManager.getMsgType(msg)
        if (msgType == IMConstants.MsgType.imGiftSend || msgType == IMConstants.MsgType.imGiftReceive) {
            playGiftAnim(msg)
        } else if (msgType == IMConstants.MsgType.imPictureReceive || msgType == IMConstants.MsgType.imPictureSend) {
            // 跳转图片预览
            val body = msg.body as EMImageMessageBody
            var originalRemoteUrl = body.remoteUrl
            CRouter.goDisplaySingle(originalRemoteUrl)
        } else if (msgType == IMConstants.MsgType.imVoiceReceive || msgType == IMConstants.MsgType.imVoiceSend) {

        }
    }

    /**
     * 播放礼物动画
     */
    private fun playGiftAnim(msg: EMMessage) {
        // 播放礼物动效，这里直接在新的Activity界面打开
        // 获取礼物消息扩展内容
        val giftStr = msg.getStringAttribute(IMConstants.Common.msgAttrGift, "")
        val gift = JsonUtils.fromJson<Gift>(giftStr)
        // 播放礼物动效，这里直接在新的Activity界面打开
        CRouter.go(GiftRouter.giftAnim, what = gift.type, obj0 = gift.cover, obj1 = gift.animation)
    }

    /**
     * 滚动到底部
     */
    private fun scrollToBottom() {
        mBinding.imChatRecyclerView.postDelayed({
            if (mAdapter.itemCount > 0) {
                mBinding.imChatRecyclerView.smoothScrollToPosition(mAdapter.itemCount - 1)
            }
        }, 200)
    }

    /**
     * 判断是否能向上滚动
     */
    private fun canScrollToUp(): Boolean {
        return mBinding.imChatRecyclerView.canScrollVertically(1)
    }

    /**
     * ----------------------------------------------
     * 初始化悬浮菜单
     */
    private fun initFloatMenu() {
        floatMenu = VMFloatMenu(requireActivity())
        floatMenu.setMenuBackground(R.drawable.shape_card_common_bg)
        floatMenu.setItemClickListener(object : VMFloatMenu.IItemClickListener() {
            override fun onItemClick(id: Int) {
                when (id) {
                    0 -> copyMsg()
                    1 -> recallMsg()
                    2 -> deleteMsg()
                }
            }
        })
    }

    /**
     * 弹出菜单
     */
    private fun showFloatMenu(view: View, event: MotionEvent) {
        floatMenu.clearAllItem()

        val msgType = IMChatManager.getMsgType(currMsg)
        if (msgType == IMConstants.MsgType.imTextReceive || msgType == IMConstants.MsgType.imTextSend) {
            floatMenu.addItem(VMFloatMenu.ItemBean(0, VMStr.byRes(R.string.im_msg_copy)))
        }

        // 判断当前消息的时间是否已经超过了限制时间，如果超过，则不可撤回消息
        val currTime = VMDate.currentMilli()
        val msgTime = currMsg.msgTime
        if (currMsg.direct() == EMMessage.Direct.SEND && currTime > msgTime && currTime - msgTime < CConstants.timeMinute * 5) {
            floatMenu.addItem(VMFloatMenu.ItemBean(1, VMStr.byRes(R.string.im_msg_recall)))
        }

        floatMenu.addItem(VMFloatMenu.ItemBean(2, VMStr.byRes(R.string.im_msg_delete)))

        floatMenu.showAtLocation(view, event.x.toInt(), event.y.toInt())
    }

    /**
     * 复制消息，只有文本才会触发
     */
    private fun copyMsg() {
        val body = currMsg.body as EMTextMessageBody
        if (VMSystem.copyToClipboard(body.message)) {
            showBar(R.string.im_msg_copy_success)
        }
    }

    /**
     * 撤回消息，特定时间内才会触发
     */
    private fun recallMsg() {
        IMChatManager.sendRecallMessage(currMsg, {
            VMSystem.runInUIThread({ refreshUpdateMsg(currMsg) })
        }) { code, desc ->
            VMSystem.runInUIThread({ errorBar("$code $desc") })
        }
    }

    /**
     * 删除消息，都会触发l
     */
    private fun deleteMsg() {
        IMChatManager.removeMessage(currMsg)
        refreshDeleteMsg(currMsg, currPosition)
    }
}