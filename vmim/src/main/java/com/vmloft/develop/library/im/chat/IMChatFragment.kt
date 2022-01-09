package com.vmloft.develop.library.im.chat

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.drakeet.multitype.MultiTypeAdapter

import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMImageMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody

import com.vmloft.develop.library.common.base.BFragment
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.common.PermissionManager
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.image.IMGChoose
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.ui.widget.CommonDialog
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.msg.*
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImFragmentChatBinding
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.VMDate
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMSystem
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap
import com.vmloft.develop.library.tools.widget.VMFloatMenu


/**
 * Create by lzan13 on 2019/05/09 10:11
 *
 * IM 可自定义加载的聊天界面
 */
class IMChatFragment : BFragment<ImFragmentChatBinding>() {

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
    private lateinit var chatExtend: String
    private lateinit var conversation: EMConversation

    private var receiveMsgCount = 0
    private var sendMsgCount = 0

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

        // 选择图片
        mBinding.imChatPictureIV.setOnClickListener { openAlbum() }
        // 开启通话申请
        mBinding.imChatCallIV.setOnClickListener { requestCall() }
        // 点击发送
        mBinding.imChatSendIV.setOnClickListener { sendText() }

        // 初始化输入框监听
        initInputWatcher()
        // 初始化列表
        initRecyclerView()
        // 初始化长按菜单
        initFloatMenu()

        // 监听新消息过来，这里肯定是发给自己的消息，在发送事件时已经过滤
        LDEventBus.observe(this, IMConstants.Common.newMsgEvent, EMMessage::class.java) {
            checkLockStatus(it)
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

        initConversation()

        checkLockStatus()
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
    }

    /**
     * 根据传入参数判断需不需要保存扩展信息
     */
    private fun saveExtendMsg() {
        if (chatExtend.isNullOrEmpty()) return

        val message = IMChatManager.createTextMessage(chatExtend, chatId, false)
        message.chatType = EMMessage.ChatType.Chat
        message.setStatus(EMMessage.Status.SUCCESS)

        IMChatManager.saveMessage(message)

        // 通知有新消息，这里主要是通知会话列表刷新
        LDEventBus.post(IMConstants.Common.newMsgEvent, message)
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

    private fun initRecyclerView() {
        mBinding.imChatRefreshLayout.setOnRefreshListener { loadMoreMsg() }

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
                // 发送正在输入，变为空则不发送输入状态
                if (!s.toString().isNullOrEmpty()) {
                    // 输入状态的展示时间是 3 秒，检查一下，如果间隔不足两秒，则不重新发送
                    val time = System.currentTimeMillis()
                    if (time - lastTimeInputStatus > CConstants.timeSecond * 2) {
                        lastTimeInputStatus = time
                        IMChatManager.sendInputStatus(chatId)
                    }
                }
                mBinding.imChatSendIV.visibility = if (s.toString().isNullOrEmpty()) View.GONE else View.VISIBLE
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
        if (receiveMsgCount > 2 && sendMsgCount > 2) {
            mBinding.imChatPictureIV.visibility = View.VISIBLE
        } else {
            mBinding.imChatPictureIV.visibility = View.GONE
        }
        if (receiveMsgCount >= 5 || sendMsgCount >= 5) {
            mBinding.imChatCallIV.visibility = View.VISIBLE
        } else {
            mBinding.imChatCallIV.visibility = View.GONE
        }
    }

    /**
     * 打开相册选择图片发送
     */
    private fun openAlbum() {
        // 必须有读写存储权限才能选择图片
        if (PermissionManager.storagePermission(requireContext())) {
            IMGChoose.singlePicture(requireActivity()) { sendPicture(it as Uri) }
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
     * 新消息刷新
     */
    private fun refreshNewMsg(msg: EMMessage) {
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
        mBinding.imChatRecyclerView.smoothScrollToPosition(mAdapter.itemCount - 1)
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