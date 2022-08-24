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

import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.BVMFragment
import com.vmloft.develop.library.base.BViewModel
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
import com.vmloft.develop.library.im.bean.IMConversation
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.chat.msg.*
import com.vmloft.develop.library.im.common.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.common.IMConversationManager
import com.vmloft.develop.library.im.common.IMViewModel
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
import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2019/05/09 10:11
 * 描述：IM 可自定义加载的聊天界面
 */
class IMChatFragment : BVMFragment<ImFragmentChatBinding, IMViewModel>() {

    private val limit = CConstants.defaultLimit

    // 输入面板控制类
    private lateinit var keyboardController: VMKeyboardController

    private var isFirst = true // 记录是否是首次加载

    // 列表适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private lateinit var layoutManager: LinearLayoutManager

    // 记录上次发送输入状态的时间
    private var lastTimeInputStatus: Long = 0

    // 消息长按弹出菜单
    private lateinit var floatMenu: VMFloatMenu
    private lateinit var currMsg: IMMessage
    private var currPosition: Int = 0

    // 会话相关
    private var chatId: String = ""
    private var chatType: Int = 0
    private var chatExtend: String = ""
    private lateinit var conversation: IMConversation

    private lateinit var selfUser: User

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

    override fun initVM(): IMViewModel = getViewModel()

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
        LDEventBus.observe(this, IMConstants.Common.newMsgEvent, IMMessage::class.java) {
            refreshNewMsg(it)
        }

        // 监听消息状态刷新，这里肯定是发给自己的消息，在发送事件时已经过滤
        LDEventBus.observe(this, IMConstants.Common.updateMsgEvent, IMMessage::class.java) {
            refreshUpdateMsg(it)
        }

        // 监听消息撤回
        LDEventBus.observe(this, IMConstants.Common.signalRecallMessage, IMMessage::class.java) {
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

        mBinding.imChatRefreshLayout.autoRefresh()
    }

    override fun onResume() {
        super.onResume()

        IMChatManager.setCurrChatId(chatId)

        // 检查是否有草稿没有发出

        if (!conversation.draft.isNullOrEmpty()) {
            mBinding.imChatMessageET.setText(conversation.draft)
        }
    }

    override fun onPause() {
        super.onPause()
        IMChatManager.setCurrChatId("")
        /**
         * 判断聊天输入框内容是否为空，不为空就保存输入框内容到[EMConversation]的扩展中
         * 调用[IMConversationManager.setConversationDraft]方法
         */
        val draft = mBinding.imChatMessageET.text.toString().trim()
        // 将输入框的内容保存为草稿
        IMConversationManager.setConversationDraft(conversation, draft)
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
                        IMChatManager.sendInputStatusSignal(chatId)
                    }
                }
                mBinding.imChatSendIV.visibility = if (s.toString().isNullOrEmpty()) View.GONE else View.VISIBLE
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
        mBinding.imChatRefreshLayout.setOnRefreshListener { mViewModel.loadMoreMessage(conversation) }

        // 消息点击监听
        val listener = object : BItemDelegate.BItemListener<IMMessage> {
            override fun onClick(v: View, data: IMMessage, position: Int) {
                clickMsg(data, position)
            }
        }
        // 消息长按监听
        val longListener = object : BItemDelegate.BItemLongListener<IMMessage> {
            override fun onLongClick(v: View, event: MotionEvent, data: IMMessage, position: Int): Boolean {
                currMsg = data
                currPosition = position
                showFloatMenu(v, event)
                return true
            }
        }
        // 注册各类消息
        mAdapter.register(IMMessage::class).to(
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
                IMConstants.MsgType.imText -> {
                    if (data.isSend) {
                        MsgTextSendDelegate::class
                    } else {
                        MsgTextReceiveDelegate::class
                    }
                }
                IMConstants.MsgType.imCall -> {
                    if (data.isSend) {
                        MsgCallSendDelegate::class
                    } else {
                        MsgCallReceiveDelegate::class
                    }
                }
                IMConstants.MsgType.imSystemDefault,
                IMConstants.MsgType.imSystemRecall,
                IMConstants.MsgType.imSystemWelcome,
                -> MsgSystemDelegate::class
                IMConstants.MsgType.imPicture -> {
                    if (data.isSend) {
                        MsgPictureSendDelegate::class
                    } else {
                        MsgPictureReceiveDelegate::class
                    }
                }
                IMConstants.MsgType.imVoice -> {
                    if (data.isSend) {
                        MsgVoiceSendDelegate::class
                    } else {
                        MsgVoiceReceiveDelegate::class
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
     * 初始化会话
     */
    private fun initConversation() {
        // 获取会话对象
        conversation = IMConversationManager.getConversation(chatId, chatType)
        // 保存扩展信息
        saveExtendMsg()

        // 清空未读
        IMConversationManager.setConversationUnread(conversation, false)

//        val cacheCount = IMConversationManager.getCacheMessages(chatId, chatType).size
//        val sumCount = IMConversationManager.getMessagesCount(chatId, chatType)
//        if (cacheCount < limit && cacheCount < sumCount) {
//            // 加载更多消息，填充满一页
////            IMConversationManager.loadMoreMessage(conversation, limit)
//            mViewModel.loadMoreMessage(conversation)
//        }
//        mItems.clear()
//        mItems.addAll(IMConversationManager.getCacheMessages(chatId, chatType))
//        mAdapter.notifyDataSetChanged()
        // 第一页内容加载完成需要滚动到底部
        mBinding.imChatRecyclerView.post {
            scrollToBottom()
        }
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        if (model.type == "loadMoreMessage" && !model.isLoading) {
            mBinding.imChatRefreshLayout.finishRefresh()
            mBinding.imChatRefreshLayout.finishLoadMore()
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "loadMoreMessage") {
            loadMoreSuccess(model.data as List<IMMessage>)
        }
    }

    private fun loadMoreSuccess(list: List<IMMessage>) {
        mItems.addAll(0, list)
        mAdapter.notifyItemRangeInserted(0, list.size)
        if (isFirst) {
            isFirst = false
            mBinding.imChatRecyclerView.post { scrollToBottom() }
        }
    }

    /**
     * 根据传入参数判断需不需要保存扩展信息
     */
    private fun saveExtendMsg() {
        if (chatExtend.isEmpty() || IMConversationManager.getMessagesCount(chatId, chatType) > 0) return

        val message = IMChatManager.createTextMessage(chatId, chatExtend, false)
        message.isLocal = true
        IMConversationManager.addMessage(message)
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
        if (selfUser.role.identity in 100..199) {
            return true
        }
        return conversation.receiveCount > ConfigManager.appConfig.chatConfig.voiceLimit && conversation.sendCount > ConfigManager.appConfig.chatConfig.voiceLimit
    }

    /**
     * 检查锁状态
     */
    private fun checkPictureLockStatus(): Boolean {
        if (selfUser.role.identity in 100..199) {
            return true
        }
        return conversation.receiveCount > ConfigManager.appConfig.chatConfig.pictureLimit && conversation.sendCount > ConfigManager.appConfig.chatConfig.pictureLimit
    }

    /**
     * 检查锁状态
     */
    private fun checkCallLockStatus(): Boolean {
        if (selfUser.role.identity in 100..199) {
            return true
        }
        return conversation.receiveCount >= ConfigManager.appConfig.chatConfig.callLimit && conversation.sendCount >= ConfigManager.appConfig.chatConfig.callLimit
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
    private fun refreshNewMsg(message: IMMessage) {
        val msgType = IMChatManager.getMsgType(message)
        if (msgType == IMConstants.MsgType.imGift) {
            playGiftAnim(message)
        }
        mItems.add(message)
        mAdapter.notifyItemInserted(mAdapter.itemCount)
        mBinding.imChatRecyclerView.post { scrollToBottom() }
    }


    /**
     * 刷新消息删除
     */
    private fun refreshDeleteMsg(message: IMMessage, position: Int) {
        mItems.remove(message)
        mAdapter.notifyItemRemoved(position)
    }

    /**
     * 刷新消息更新
     */
    private fun refreshUpdateMsg(message: IMMessage) {
        val position = IMConversationManager.getPosition(message)
        mItems.removeAt(position)
        mItems.add(position, message)
        mAdapter.notifyItemChanged(position)
    }

    /**
     * 发送文本消息
     */
    private fun sendText() {
        val content: String = mBinding.imChatMessageET.text.toString().trim()
        if (content.isEmpty()) {
            return errorBar(R.string.im_chat_send_notnull)
        }
        mBinding.imChatMessageET.setText("")
        lastTimeInputStatus = 0
        // 发送消息
        sendMessage(IMChatManager.createTextMessage(chatId, content))

        changeInputStatus()
    }

    /**
     * 发送语音消息
     */
    private fun sendVoice(path: String, time: Int) {
        val uri = Uri.parse(path)
        sendMessage(IMChatManager.createVoiceMessage(chatId, uri, time))
    }

    /**
     * 发送图片消息
     */
    private fun sendPicture(uri: Uri) {
        // 临时压缩下图片，这里压缩到默认的分辨率
        val tempPath = VMBitmap.compressTempImage(uri, 2048, 256)
        sendMessage(IMChatManager.createPictureMessage(chatId, Uri.parse(tempPath)))
    }

    /**
     * 发送礼物消息
     */
    private fun sendGift(gift: Gift) {
        val message = IMChatManager.createGiftMessage(chatId, gift)
        sendMessage(message)
    }

    /**
     * 发送消息统一收口
     */
    private fun sendMessage(message: IMMessage) {
        IMChatManager.sendMessage(message)
    }

    /**
     * 点击消息事件
     */
    private fun clickMsg(message: IMMessage, position: Int) {
        val msgType = IMChatManager.getMsgType(message)
        if (msgType == IMConstants.MsgType.imGift) {
            playGiftAnim(message)
        } else if (msgType == IMConstants.MsgType.imPicture) {
            // 跳转图片预览
            val attachment = message.attachments[0]
            CRouter.goDisplaySingle(attachment.path)
        } else if (msgType == IMConstants.MsgType.imVoice) {

        }
    }

    /**
     * 播放礼物动画
     */
    private fun playGiftAnim(message: IMMessage) {
        // 播放礼物动效，这里直接在新的Activity界面打开
        if (message.attachments.size == 2) {
            CRouter.go(GiftRouter.giftAnim, what = 1, obj0 = message.attachments[0], obj1 = message.attachments[1])
        } else {
            CRouter.go(GiftRouter.giftAnim, what = 0, obj0 = message.attachments[0])
        }
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
        if (msgType == IMConstants.MsgType.imText) {
            floatMenu.addItem(VMFloatMenu.ItemBean(0, VMStr.byRes(R.string.im_msg_copy)))
        }

        // 判断当前消息的时间是否已经超过了限制时间，如果超过，则不可撤回消息
        val currTime = VMDate.currentMilli()
        if (currMsg.isSend && currTime > currMsg.time && currTime - currMsg.time < CConstants.timeMinute * 5) {
            floatMenu.addItem(VMFloatMenu.ItemBean(1, VMStr.byRes(R.string.im_msg_recall)))
        }

        floatMenu.addItem(VMFloatMenu.ItemBean(2, VMStr.byRes(R.string.im_msg_delete)))

        floatMenu.showAtLocation(view, event.x.toInt(), event.y.toInt())
    }

    /**
     * 复制消息，只有文本才会触发
     */
    private fun copyMsg() {
        if (VMSystem.copyToClipboard(currMsg.body)) {
            showBar(R.string.im_msg_copy_success)
        }
    }

    /**
     * 撤回消息，特定时间内才会触发
     */
    private fun recallMsg() {
        IMChatManager.sendRecallSignal(currMsg, error = { code, obj ->
            VMSystem.runInUIThread({ errorBar("$code $obj") })
        })
    }

    /**
     * 删除消息，都会触发l
     */
    private fun deleteMsg() {
        IMConversationManager.removeMessage(currMsg)
        refreshDeleteMsg(currMsg, currPosition)
    }

    override fun onDestroy() {
        IMConversationManager.clearMsg(conversation)
        super.onDestroy()
    }
}