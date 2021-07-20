package com.vmloft.develop.library.im.chat

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

import com.drakeet.multitype.MultiTypeAdapter

import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMImageMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody

import com.scwang.smart.refresh.header.ClassicsHeader

import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.base.BaseFragment
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.common.PermissionManager
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.image.IMGChoose
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.common.widget.CommonDialog
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.msg.*
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.VMDate
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMSystem
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap
import com.vmloft.develop.library.tools.widget.VMFloatMenu

import kotlinx.android.synthetic.main.im_fragment_chat.*


/**
 * Create by lzan13 on 2019/05/09 10:11
 *
 * IM 可自定义加载的聊天界面
 */
class IMChatFragment : BaseFragment() {

    private val limit = CConstants.defaultLimit

    // 列表适配器
    private val mAdapter by lazy { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private lateinit var layoutManager: LinearLayoutManager

    // 记录上次发送输入状态的时间
    private var lastTimeInputStatus: Long = 0

    // 消息长按弹出菜单
    private lateinit var floatMenu: VMFloatMenu
    private lateinit var currMsg: EMMessage
    private var currPosition: Int = 0

    // 会话相关
    private lateinit var chatId: String
    private var chatType: Int = 0
    private lateinit var conversation: EMConversation

    private var receiveMsgCount = 0
    private var sendMsgCount = 0

    companion object {
        private val argChatId = "argChatId"
        private val argChatType = "argChatType"

        /**
         * Fragment 的工厂方法，方便创建并设置参数
         */
        fun newInstance(chatId: String, chatType: Int): IMChatFragment {
            val fragment = IMChatFragment()
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
    override fun layoutId(): Int = R.layout.im_fragment_chat

    override fun initUI() {
        super.initUI()

        // 选择图片
        imChatPictureIV.setOnClickListener { openAlbum() }
        imChatPictureLockIV.setOnClickListener { showPictureLimitDialog() }
        // 开启通话申请
        imChatCallIV.setOnClickListener { requestCall() }
        imChatCallLockIV.setOnClickListener { showCallLimitDialog() }
        // 点击发送
        imChatSendIV.setOnClickListener { sendText() }

        // 初始化输入框监听
        initInputWatcher()
        // 初始化列表
        initRecyclerView()
        // 初始化长按菜单
        initFloatMenu()

        // 监听新消息过来，这里肯定是发给自己的消息，在发送事件时已经过滤
        LDEventBus.observe(this, IMConstants.Common.newMsgEvent, EMMessage::class.java) {
            checkLockStatus()
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

        initConversation()

        checkLockStatus()
    }

    /**
     * 初始化会话
     */
    private fun initConversation() {
        // 获取会话对象
        conversation = IMChatManager.getConversation(chatId, chatType)

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
    }


    override fun onResume() {
        super.onResume()

        IMChatManager.setCurrChatId(chatId)

        // 检查是否有草稿没有发出
        val draft = IMChatManager.getConversationDraft(conversation)
        if (!draft.isNullOrEmpty()) {
            imChatMessageET.setText(draft)
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
        val draft = imChatMessageET.text.toString().trim()
        // 将输入框的内容保存为草稿
        IMChatManager.setConversationDraft(conversation, draft)
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

        // 消息点击监听
        val listener = object : BItemDelegate.BItemListener<EMMessage> {
            override fun onClick(v: View, data: EMMessage, position: Int) {
                val body = data.body as EMImageMessageBody
                var originalRemoteUrl = body.remoteUrl
                CRouter.goDisplaySingle(originalRemoteUrl)
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
            MsgPictureReceiveDelegate(listener, longListener),
            MsgPictureSendDelegate(listener, longListener),
            MsgTextReceiveDelegate(longListener),
            MsgTextSendDelegate(longListener),
        ).withKotlinClassLinker { _, data ->
            // 根据消息类型返回不同的 View 展示
            when (IMChatManager.getMsgType(data)) {
                IMConstants.MsgType.imSystem -> MsgSystemDelegate::class
                IMConstants.MsgType.imRecall -> MsgSystemDelegate::class
                IMConstants.MsgType.imCallReceive -> MsgCallReceiveDelegate::class
                IMConstants.MsgType.imCallSend -> MsgCallSendDelegate::class
                IMConstants.MsgType.imPictureReceive -> MsgPictureReceiveDelegate::class
                IMConstants.MsgType.imPictureSend -> MsgPictureSendDelegate::class
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
                // 发送正在输入，变为空则不发送输入状态
                if (!s.toString().isNullOrEmpty()) {
                    // 输入状态的展示时间是 3 秒，检查一下，如果间隔不足两秒，则不重新发送
                    val time = System.currentTimeMillis()
                    if (time - lastTimeInputStatus > CConstants.timeSecond * 2) {
                        lastTimeInputStatus = time
                        IMChatManager.sendInputStatus(chatId)
                    }
                }
                imChatSendIV.visibility = if (s.toString().isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })
    }

    /**
     * 检查锁状态
     */
    private fun checkLockStatus(msg: EMMessage? = null) {
        msg?.let {
            if (msg.direct() == EMMessage.Direct.RECEIVE) {
                receiveMsgCount++
            } else {
                sendMsgCount++
            }
        }
        if (receiveMsgCount < 10 || sendMsgCount < 10) {
            imChatPictureLockIV.visibility = View.VISIBLE
        } else {
            imChatPictureLockIV.visibility = View.GONE
        }
        if (receiveMsgCount < 20 || sendMsgCount < 20) {
            imChatCallLockIV.visibility = View.VISIBLE
        } else {
            imChatCallLockIV.visibility = View.GONE
        }
    }

    /**
     * 打开相册选择图片发送
     */
    private fun openAlbum() {
        IMGChoose.singlePicture(requireActivity()) { sendPicture(it as Uri) }
    }

    /**
     * 显示发送图片限制对话框
     */
    private fun showPictureLimitDialog() {
        mDialog = CommonDialog(requireActivity())
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(VMStr.byResArgs(R.string.im_chat_picture_limit, 10, 10))
            dialog.setNegative("")
            dialog.setPositive(VMStr.byRes(R.string.btn_i_known))
            dialog.show()
        }
    }

    /**
     * 请求通话
     */
    private fun requestCall() {
        // 必须有录音权限才能进行通话
        if (PermissionManager.recordPermission(requireContext())) {
            IMRouter.goSingleCall(chatId)
        }
    }

    /**
     * 显示通话限制对话框
     */
    private fun showCallLimitDialog() {
        mDialog = CommonDialog(requireActivity())
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(VMStr.byResArgs(R.string.im_chat_call_limit, 20, 20))
            dialog.setNegative("")
            dialog.setPositive(VMStr.byRes(R.string.btn_i_known))
            dialog.show()
        }
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
        imChatRefreshLayout.finishRefresh()

        val list = IMChatManager.loadMoreMessages(conversation, limit)
        mItems.addAll(0, list)
        mAdapter.notifyItemRangeInserted(0, list.size)
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
        lastTimeInputStatus = 0
        // 创建消息
        sendMessage(IMChatManager.createTextMessage(content, chatId))
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
     * 滚动到底部
     */
    private fun scrollToBottom() {
        imChatRecyclerView.smoothScrollToPosition(mAdapter.itemCount - 1)
    }

    /**
     * 判断是否能向上滚动
     */
    private fun canScrollToUp(): Boolean {
        return imChatRecyclerView.canScrollVertically(1)
    }


    /**
     * ----------------------------------------------
     * 初始化悬浮菜单
     */
    private fun initFloatMenu() {
        floatMenu = VMFloatMenu(requireActivity())
        floatMenu.setItemClickListener(object : VMFloatMenu.IItemClickListener() {
            override fun onItemClick(id: Int) {
                when (id) {
                    0 -> copyMsg()
                    1 -> recallMsg()
                    2 -> deleteMsg()
                    else -> {
                    }
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
        if (currTime > msgTime && currTime - msgTime < CConstants.timeMinute * 5) {
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
     * 删除消息，都会触发
     */
    private fun deleteMsg() {
        IMChatManager.removeMessage(currMsg)
        refreshDeleteMsg(currMsg, currPosition)
    }

}