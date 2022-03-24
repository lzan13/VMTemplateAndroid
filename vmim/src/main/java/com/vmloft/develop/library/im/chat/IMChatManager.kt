package com.vmloft.develop.library.im.chat

import android.net.Uri

import com.hyphenate.EMCallBack
import com.hyphenate.chat.*
import com.hyphenate.chat.EMConversation.EMConversationType
import com.hyphenate.chat.EMMessage
import com.hyphenate.exceptions.HyphenateException

import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.tools.utils.VMDate
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.logger.VMLog

import org.json.JSONException
import org.json.JSONObject


/**
 * Create by lzan13 2021/5/20
 * 描述：聊天管理
 */
object IMChatManager {

    // 当前聊天对象 Id
    private var currChatId: String? = null


    fun init() {
        // 将会话加载到内存，因为这个必须要登录之后才能加载，这里只是登录过才有效
        EMClient.getInstance().chatManager().loadAllConversations()
        // 添加消息监听
        EMClient.getInstance().chatManager().addMessageListener(IMChatListener())
    }

    /**
     * 判断是否正在和当前用户聊天
     */
    fun isCurrChat(chatId: String): Boolean {
        return chatId == currChatId
    }

    fun setCurrChatId(chatId: String) {
        currChatId = chatId
    }

    /**
     * 获取全部会话，并进行排序
     */
    fun getAllConversation(): List<EMConversation> {
        val map = EMClient.getInstance().chatManager().allConversations
        val sortList = mutableListOf<EMConversation>()
        sortList.addAll(map.values)
        // 排序
        sortList.sortByDescending { getConversationTime(it) }

        // 排序之后，重新将置顶的条目设置到顶部
        val resultList = mutableListOf<EMConversation>()
        var count = 0
        for (item in sortList) {
            if (getConversationTop(item)) {
                resultList.add(count, item)
                count++
            } else {
                resultList.add(item)
            }
        }
        return resultList
    }

    /**
     * 根据会话 id 获取会话
     *
     * @param id 会话 id
     */
    fun getConversation(id: String): EMConversation? {
        return EMClient.getInstance().chatManager().getConversation(id)
    }

    /**
     * 根据会话 id 获取会话
     *
     * @param id       会话 id
     * @param chatType 会话类型
     */
    fun getConversation(id: String, chatType: Int): EMConversation {
        val conversationType: EMConversationType = wrapConversationType(chatType)
        // 为空时创建会话
        return EMClient.getInstance().chatManager().getConversation(id, conversationType, true)
    }

    /**
     * 删除会话
     * @param id 会话 id
     */
    fun deleteConversation(id: String) {
        EMClient.getInstance().chatManager().deleteConversation(id, true)
    }

    /**
     * 清空会话，这里会删除内存和数据库的数据
     *
     * @param id 会话 id
     * @param db 是否删除数据库数据
     */
    fun clearConversation(id: String, db: Boolean = true) {
        val conversation = getConversation(id) ?: return
        if (db) {
            conversation.clearAllMessages()
        } else {
            conversation.clear()
        }
    }

    /**
     * 清空全部未读数
     */
    fun clearAllUnread() {
        val conversations = getAllConversation()
        conversations.forEach { conversation ->
            setConversationUnread(conversation, false)
        }
    }

    /**
     * 获取当前会话总消息数量
     */
    fun getMessagesCount(id: String, chatType: Int): Int {
        val conversation = getConversation(id, chatType)
        return conversation.allMsgCount
    }

    /**
     * 获取会话已加载所有消息
     */
    fun getCacheMessages(id: String, chatType: Int): List<EMMessage> {
        val conversation = getConversation(id, chatType)
        return conversation.allMessages
    }

    /**
     * 获取缓存中的图片消息，主要用来预览图片
     */
    fun getCachePictureMessage(id: String, chatType: Int): List<EMMessage> {
        val result = mutableListOf<EMMessage>()
        for (msg in getCacheMessages(id, chatType)) {
            if (msg.type == EMMessage.Type.IMAGE) {
                result.add(msg)
            }
        }
        return result
    }

    /**
     * 获取当前会话的所有消息
     */
    fun loadMoreMessages(conversation: EMConversation, limit: Int = CConstants.defaultLimit): List<EMMessage> {
        // 获取已经在列表中的最上边的一条消息id
        val msgId = if (conversation.allMsgCount == 0 || conversation.allMessages.size == 0) {
            ""
        } else {
            conversation.allMessages.first().msgId
        }
        return conversation.loadMoreMsgFromDB(msgId, limit)
    }

    /**
     * 获取指定消息，此操作不会将消息加入到内存列表
     *
     * @param id    会话 id
     * @param msgId 消息 id
     * @return
     */
    fun getMessage(id: String, msgId: String): EMMessage? {
        val conversation = getConversation(id) ?: return null
        return conversation.getMessage(msgId, false)
    }

    /**
     * 获取最后一条消息，这回将这条消息加入到内存
     *
     * @param id       会话 id
     * @param chatType 会话类型
     * @return
     */
    fun getLastMessage(id: String, chatType: Int): EMMessage? {
        val conversation = getConversation(id, chatType)
        return conversation.lastMessage
    }

    /**
     * 获取第一条消息，这回将这条消息加入到内存
     *
     * @param id       会话 id
     * @param chatType 会话类型
     * @return
     */
    fun getFirstMessage(conversation: EMConversation): EMMessage? {
        conversation.allMessages
        return conversation.lastMessage
    }

    /**
     * 获取消息位置
     */
    fun getPosition(message: EMMessage): Int {
        val conversation = getConversation(message.conversationId(), message.chatType.ordinal)
        return conversation.allMessages.indexOf(message)
    }

    /**
     * 包装会话类型
     */
    fun wrapConversationType(chatType: Int): EMConversationType {
        return when (chatType) {
            IMConstants.ChatType.imChatGroup -> EMConversationType.GroupChat
            IMConstants.ChatType.imChatRoom -> EMConversationType.ChatRoom
            else -> EMConversationType.Chat
        }
    }

    /**
     * 包装聊天类型
     */
    fun wrapChatType(chatType: Int): EMMessage.ChatType {
        return when (chatType) {
            IMConstants.ChatType.imChatGroup -> EMMessage.ChatType.GroupChat
            IMConstants.ChatType.imChatRoom -> EMMessage.ChatType.ChatRoom
            else -> EMMessage.ChatType.Chat
        }
    }

    /**
     * -------------------------------------- 会话扩展 --------------------------------------
     */
    private fun getExtObject(conversation: EMConversation): JSONObject {
        // 获取当前会话对象的扩展
        val ext = conversation.extField
        return if (ext.isNullOrEmpty()) {
            JSONObject()
        } else {
            JSONObject(ext)
        }
    }

    /**
     * 设置会话扩展
     */
    fun setConversationExt(conversation: EMConversation, key: String?, value: Any?) {
        try {
            val extObject = getExtObject(conversation)
            extObject.put(key, value)
            // 将扩展信息保存到 Conversation 对象的扩展中去
            conversation.extField = extObject.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * 获取会话扩展
     *
     * @param conversation 需要获取的会话对象
     */
    fun getConversationExt(conversation: EMConversation, key: String?): Any? {
        try {
            val extObject = getExtObject(conversation)
            // 根据扩展的key获取扩展的值
            return extObject.opt(key)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 设置当前会话草稿
     *
     * @param conversation 需要设置的会话对象
     * @param draft        需要设置的草稿内容
     */
    fun setConversationDraft(conversation: EMConversation, draft: String?) {
        setConversationExt(conversation, IMConstants.Common.conversationDraft, draft)
    }

    /**
     * 获取当前会话的草稿内容
     *
     * @param conversation 当前会话
     * @return 返回草稿内容
     */
    fun getConversationDraft(conversation: EMConversation): String {
        return getConversationExt(conversation, IMConstants.Common.conversationDraft) as? String ?: ""
    }

    /**
     * 设置会话置顶状态
     *
     * @param conversation 要置顶的会话对象
     * @param top          设置会话是否置顶
     */
    fun setConversationTop(conversation: EMConversation, top: Boolean) {
        setConversationExt(conversation, IMConstants.Common.conversationTop, top)
    }

    /**
     * 获取当前会话是否置顶
     *
     * @param conversation 需要操作的会话对象
     * @return 返回当前会话是否置顶
     */
    fun getConversationTop(conversation: EMConversation): Boolean {
        return getConversationExt(conversation, IMConstants.Common.conversationTop) as? Boolean ?: false
    }

    /**
     * 标记会话为未读状态
     *
     * @param conversation 需要标记的会话
     * @param unread       设置未读状态
     */
    fun setConversationUnread(conversation: EMConversation, unread: Boolean) {
        // 设置为已读的时候清空下原有未读数据
        if (!unread) {
            conversation.markAllMessagesAsRead()
        }
        setConversationExt(conversation, IMConstants.Common.conversationUnread, unread)
    }

    /**
     * 获取 conversation 对象扩展中的未读状态
     *
     * @param conversation 当前会话
     * @return 返回未读状态
     */
    fun getConversationUnread(conversation: EMConversation): Int {
        var count = conversation.unreadMsgCount
        if (count > 0) {
            return count
        }
        val unread = getConversationExt(conversation, IMConstants.Common.conversationUnread) as? Boolean ?: false
        count = if (unread) 1 else 0
        return count
    }

    /**
     * 设置会话的最后时间
     *
     * @param conversation 要设置的会话对象
     */
    fun setConversationTime(conversation: EMConversation, lastTime: Long) {
        setConversationExt(conversation, IMConstants.Common.conversationTime, lastTime)
    }

    /**
     * 获取会话的最后时间
     *
     * @param conversation 需要获取的会话对象
     * @return 返回此会话最后的时间
     */
    fun getConversationTime(conversation: EMConversation): Long {
        return getConversationExt(conversation, IMConstants.Common.conversationTime) as? Long ?: VMDate.currentMilli()
    }

    /**
     * 设置会话发送消息数+1
     *
     * @param conversation 要设置的会话对象
     */
    fun setConversationMsgSendCountAdd(conversation: EMConversation) {
        var count = getConversationMsgSendCount(conversation)
        count++
        setConversationExt(conversation, IMConstants.Common.conversationMsgSendCount, count)
    }

    /**
     * 获取会话发送消息数
     *
     * @param conversation 需要获取的会话对象
     * @return 返回此会话发送消息数
     */
    fun getConversationMsgSendCount(conversation: EMConversation): Int {
        return getConversationExt(conversation, IMConstants.Common.conversationMsgSendCount) as? Int ?: 0
    }

    /**
     * 设置会话接收消息数+1
     *
     * @param conversation 要设置的会话对象
     */
    fun setConversationMsgReceiveCountAdd(conversation: EMConversation) {
        var count = getConversationMsgReceiveCount(conversation)
        count++
        setConversationExt(conversation, IMConstants.Common.conversationMsgReceiveCount, count)
    }

    /**
     * 获取会话接收消息数
     *
     * @param conversation 需要获取的会话对象
     * @return 返回此会话发送消息数
     */
    fun getConversationMsgReceiveCount(conversation: EMConversation): Int {
        return getConversationExt(conversation, IMConstants.Common.conversationMsgReceiveCount) as? Int ?: 0
    }

    // ---------------------------------------- 会话扩展结束 ----------------------------------------

    /**
     * ------------------------------------------ 消息相关 ------------------------------------------
     * 创建消息，默认支持以下类型
     * TXT, IMAGE, VIDEO, LOCATION, VOICE, FILE, CMD
     */
    /**
     * 创建一条文本消息
     *
     * @param content 消息内容
     * @param id      消息对方
     * @param isSend  是否为发送消息
     */
    fun createTextMessage(content: String, id: String, isSend: Boolean = true): EMMessage {
        val message: EMMessage
        if (isSend) {
            message = EMMessage.createTxtSendMessage(content, id)
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.Common.msgAttrNotifyEnable, true)
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.TXT)
            message.addBody(EMTextMessageBody(content))
            message.from = id
        }
        return message
    }

    /**
     * 创建一条图片消息
     *
     * @param uri   图片路径
     * @param id     接收者
     * @param isSend 是否为发送消息
     */
    fun createPictureMessage(uri: Uri, id: String, isSend: Boolean = true): EMMessage {
        val message: EMMessage
        if (isSend) {
            message = EMMessage.createImageSendMessage(uri, true, id)
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.Common.msgAttrNotifyEnable, true)
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.IMAGE)
            message.addBody(EMImageMessageBody(uri))
            message.from = id
        }
        return message
    }

    /**
     * 发送位置消息
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param address   位置
     * @param id        接收者
     * @param isSend    是否为发送消息
     */
    fun createLocationMessage(latitude: Double, longitude: Double, address: String, id: String, isSend: Boolean = true): EMMessage {
        val message: EMMessage
        if (isSend) {
            message = EMMessage.createLocationSendMessage(latitude, longitude, address, id)
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.Common.msgAttrNotifyEnable, true)
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.LOCATION)
            message.addBody(EMLocationMessageBody(address, latitude, longitude))
            message.from = id
        }
        return message
    }

    /**
     * 发送视频消息
     *
     * @param uri      视频文件地址
     * @param thumbPath 视频缩略图地址
     * @param time      视频时长
     * @param id        接收者
     * @param isSend    是否为发送消息
     */
    fun createVideoMessage(path: String, thumbPath: String, time: Int, id: String, isSend: Boolean = true): EMMessage {
        val message: EMMessage
        if (isSend) {
            message = EMMessage.createVideoSendMessage(path, thumbPath, time, id)
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.Common.msgAttrNotifyEnable, true)
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.LOCATION)
            message.addBody(EMVideoMessageBody(path, thumbPath, time, 0L))
            message.from = id
        }
        return message
    }

    /**
     * 发送语音消息
     *
     * @param uri   语音文件的路径
     * @param time   语音持续时间
     * @param id     接收者
     * @param isSend 是否为发送消息
     */
    fun createVoiceMessage(uri: Uri, time: Int, id: String, isSend: Boolean = true): EMMessage {
        val message: EMMessage
        if (isSend) {
            message = EMMessage.createVoiceSendMessage(uri, time, id)
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.Common.msgAttrNotifyEnable, true)
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.LOCATION)
            message.addBody(EMVoiceMessageBody(uri, time))
            message.from = id
        }
        return message
    }

    /**
     * 发送文件消息
     *
     * @param uri   要发送的文件的路径
     * @param id     接收者
     * @param isSend 是否为发送消息
     */
    fun createFileMessage(uri: Uri, id: String, isSend: Boolean = true): EMMessage {
        val message: EMMessage
        if (isSend) {
            message = EMMessage.createFileSendMessage(uri, id)
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.Common.msgAttrNotifyEnable, true)
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.LOCATION)
            message.addBody(EMNormalFileMessageBody(uri))
            message.from = id
        }
        return message
    }

    /**
     * 创建 CMD 透传消息
     *
     * @param cmd 要发送 cmd 命令
     * @param id     接收者
     */
    fun createCMDMessage(cmd: String, id: String): EMMessage {
        // 根据文件路径创建一条文件消息
        val message = EMMessage.createSendMessage(EMMessage.Type.CMD)
        message.to = id
        // 创建CMD 消息的消息体 并设置 action
        message.addBody(EMCmdMessageBody(cmd))
        return message
    }

    /**
     * 保存消息
     */
    fun saveMessage(msg: EMMessage) {
        EMClient.getInstance().chatManager().saveMessage(msg)
        // 保存会话时间
        setConversationTime(getConversation(msg.conversationId(), msg.chatType.ordinal), msg.localTime())
        LDEventBus.post(IMConstants.Common.newMsgEvent, msg)
    }

    /**
     * 删除消息
     */
    fun removeMessage(message: EMMessage) {
        getConversation(message.conversationId(), message.chatType.ordinal).removeMessage(message.msgId)
    }

    /**
     * 最终调用发送信息方法
     *
     * @param msg  需要发送的消息
     * @param callback 发送结果回调接口
     */
    fun sendMessage(
        msg: EMMessage,
        success: () -> Unit = {},
        error: (Int, String) -> Unit = { _: Int, _: String -> },
        progress: (Int, String) -> Unit = { _, _ -> },
    ) {
        if (!IM.isSignIn()) {
            error.invoke(-1, "未登录，无法发送消息")
            return
        }
        setConversationTime(getConversation(msg.conversationId(), msg.chatType.ordinal), msg.localTime())
        /**
         * 调用sdk的消息发送方法发送消息，发送消息时要尽早的设置消息监听，防止消息状态已经回调，
         * 但是自己没有注册监听，导致检测不到消息状态的变化
         * 所以这里在发送之前先设置消息的状态回调
         */
        msg.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                VMLog.i("消息发送成功 msgId ${msg.msgId} - $msg")
                LDEventBus.post(IMConstants.Common.updateMsgEvent, msg)
                success.invoke()
            }

            override fun onError(code: Int, desc: String) {
                VMLog.i("消息发送失败 $code, $desc")
                LDEventBus.post(IMConstants.Common.updateMsgEvent, msg)
                error.invoke(code, desc)
            }

            override fun onProgress(pro: Int, desc: String) {
                // 消息发送进度，这里不处理，留给消息Item自己去更新
                VMLog.i("消息发送中 $pro, $desc")
//                LDEventBus.post(IMConstants.Common.updateMsgEvent, msg)
                progress.invoke(pro, desc)
            }
        })
        // 发送消息
        EMClient.getInstance().chatManager().sendMessage(msg)
        // 发送一条新消息时插入新消息的位置，这里直接用插入新消息前的消息总数来作为新消息的位置
        //        int position = conversation.getAllMessages().indexOf(message);
    }

    /**
     * 发送消息已读 ACK
     */
    fun sendReadACK(message: EMMessage) {
        try {
            EMClient.getInstance().chatManager().ackMessageRead(message.from, message.msgId)
        } catch (e: HyphenateException) {
            e.printStackTrace()
        }
    }

    /**
     * --------------------------- 发送扩展类消息 ---------------------------
     */
    /**
     * 发送一条撤回消息的透传，这里需要和接收方协商定义，通过一个透传，并加上扩展去实现消息的撤回
     *
     * @param message  需要撤回的消息
     */
    fun sendRecallMessage(message: EMMessage, success: () -> Unit, error: (Int, String) -> Unit) {
        val result = false
        // 获取当前时间，用来判断后边撤回消息的时间点是否合法，这个判断不需要在接收方做，
        // 因为如果接收方之前不在线，很久之后才收到消息，将导致撤回失败
        val currTime = VMDate.currentMilli()
        val msgTime = message.msgTime
        // 判断当前消息的时间是否已经超过了限制时间，如果超过，则不可撤回消息
        if (currTime < msgTime || currTime - msgTime > CConstants.timeMinute * 5) {
            error.invoke(-1, VMStr.byRes(R.string.im_error_recall_limit_time))
            return
        }
        // 创建一个CMD 类型的消息，将需要撤回的消息通过这条 CMD 消息发送给对方
        val cmdMessage = createCMDMessage(IMConstants.Common.cmdRecallAction, message.to)
        // 同步消息类型
        cmdMessage.chatType = message.chatType
        // 设置消息的扩展为要撤回的 msgId
        cmdMessage.setAttribute(IMConstants.Common.msgAttrMsgId, message.msgId)
        // 准备工作完毕，发送消息
        sendMessage(cmdMessage, {
            // 设置扩展为撤回消息类型，是为了区分消息的显示
            message.setAttribute(IMConstants.Common.msgAttrExtType, IMConstants.MsgType.imRecall)
            // 更新消息
            EMClient.getInstance().chatManager().updateMessage(message)
            success.invoke()
        }, { code, desc ->
            error.invoke(code, desc)
        })
    }

    /**
     * 收到撤回消息，这里需要和发送方协商定义，通过一个透传，并加上扩展去实现消息的撤回
     *
     * @param cmdMessage 收到的透传消息，包含需要撤回的消息的 msgId
     * @return 返回撤回结果是否成功
     */
    fun receiveRecallMessage(cmdMessage: EMMessage): Boolean {
        var result = false
        // 从cmd扩展中获取要撤回消息的id
        val msgId = cmdMessage.getStringAttribute(IMConstants.Common.msgAttrMsgId, null)
        if (msgId == null) {
            VMLog.d("receive recall - $msgId")
            return result
        }
        // 根据得到的msgId 去本地查找这条消息，如果本地已经没有这条消息了，就不用撤回
        val message = EMClient.getInstance().chatManager().getMessage(msgId)
        if (message == null) {
            VMLog.d("receive recall - message is null $msgId")
            return result
        }

        // 设置扩展为撤回消息类型，是为了区分消息的显示
        message.setAttribute(IMConstants.Common.msgAttrExtType, IMConstants.MsgType.imRecall)
        // 如果未读，这里需要更新下未读数-1
        if (message.isUnread) {
            getConversation(cmdMessage.conversationId(), cmdMessage.chatType.ordinal).markMessageAsRead(msgId)
        }
        // 更新消息
        result = EMClient.getInstance().chatManager().updateMessage(message)

        // 更新成功，通知页面刷新
        if (result) LDEventBus.post(IMConstants.Common.cmdRecallAction, message)

        return result
    }

    /**
     * 发送鼓励消息
     */
    fun sendEncourage(id: String, chatType: Int) {
        // 创建CMD 消息的消息体 并设置 action 为输入状态
        val message = createCMDMessage(IMConstants.Common.cmdEncourageAction, id)
        message.chatType = wrapChatType(chatType)
        sendMessage(message)
    }

    /**
     * 发送通话信令消息
     */
    fun sendCallSignal(id: String, status: Int) {
        // 创建CMD 消息的消息体 并设置 action 为通话信令
        val message = createCMDMessage(IMConstants.Call.cmdCallAction, id)
        message.setAttribute(IMConstants.Call.msgAttrCallStatus, status)
        sendMessage(message)
    }

    /**
     * 发送输入状态，这里通过cmd消息来进行发送，告知对方自己正在输入
     */
    fun sendInputStatus(id: String) {
        // 创建CMD 消息的消息体 并设置 action 为输入状态
        sendMessage(createCMDMessage(IMConstants.Common.cmdInputStatusAction, id))
    }

    /**
     * 发送快速聊天信令消息
     */
    fun sendFastSignal(id: String, status: Int, content: String, len: Int) {
        // 创建CMD 消息的消息体 并设置 action 为通话信令
        val message = createCMDMessage(IMConstants.ChatFast.cmdFastInputAction, id)
        message.setAttribute(IMConstants.ChatFast.msgAttrFastInputStatus, status)
        message.setAttribute(IMConstants.ChatFast.msgAttrFastInputContent, content)
        message.setAttribute(IMConstants.ChatFast.msgAttrFastInputLen, len)
        sendMessage(message)
    }

    /**
     * 发送联系人信息修改消息
     */
    fun sendUserInfoChange() {
        val list = getAllConversation()
        val cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD)
        // 创建CMD 消息的消息体 并设置 action
        val body = EMCmdMessageBody(IMConstants.Common.cmdInfoChangeAction)
        cmdMessage.addBody(body)
        for (conversation in list) {
            cmdMessage.to = conversation.conversationId()
            EMClient.getInstance().chatManager().sendMessage(cmdMessage)
        }
    }

    /**
     * --------------------------- 消息类型获取 ---------------------------
     */
    /**
     * 获取消息类型
     */
    fun getMsgType(msg: EMMessage): Int {
        var extType: Int = IM.imListener.getMsgType(msg)
        if (extType > 0) {
            return extType
        }
        extType = msg.getIntAttribute(IMConstants.Common.msgAttrExtType, IMConstants.MsgType.imUnknown)
        return if (extType == IMConstants.MsgType.imSystem || extType == IMConstants.MsgType.imRecall) {
            extType
        } else if (extType == IMConstants.MsgType.imCall) {
            // 通话
            if (msg.direct() == EMMessage.Direct.RECEIVE) IMConstants.MsgType.imCallReceive else IMConstants.MsgType.imCallSend
        } else if (extType == IMConstants.MsgType.imBigEmotion) {
            // 大表情
            if (msg.direct() == EMMessage.Direct.RECEIVE) IMConstants.MsgType.imBigEmotionReceive else IMConstants.MsgType.imBigEmotionSend
        } else if (msg.type == EMMessage.Type.TXT) {
            // 文本
            if (msg.direct() == EMMessage.Direct.RECEIVE) IMConstants.MsgType.imTextReceive else IMConstants.MsgType.imTextSend
        } else if (msg.type == EMMessage.Type.IMAGE) {
            // 图片
            if (msg.direct() == EMMessage.Direct.RECEIVE) IMConstants.MsgType.imPictureReceive else IMConstants.MsgType.imPictureSend
        } else if (msg.type == EMMessage.Type.VIDEO) {
            // 视频
            if (msg.direct() == EMMessage.Direct.RECEIVE) IMConstants.MsgType.imVideoReceive else IMConstants.MsgType.imVideoSend
        } else if (msg.type == EMMessage.Type.LOCATION) {
            // 位置
            if (msg.direct() == EMMessage.Direct.RECEIVE) IMConstants.MsgType.imLocationReceive else IMConstants.MsgType.imLocationSend
        } else if (msg.type == EMMessage.Type.VOICE) {
            // 语音
            if (msg.direct() == EMMessage.Direct.RECEIVE) IMConstants.MsgType.imVoiceReceive else IMConstants.MsgType.imVoiceSend
        } else if (msg.type == EMMessage.Type.FILE) {
            // 文件
            if (msg.direct() == EMMessage.Direct.RECEIVE) IMConstants.MsgType.imFileReceive else IMConstants.MsgType.imFileSend
        } else {
            // 未知，显示提示文本
            IMConstants.MsgType.imUnknown
        }
    }

    /**
     * 获取消息摘要信息
     */
    fun getSummary(msg: EMMessage?): String {
        if (msg == null) return VMStr.byRes(R.string.im_empty)

        var content: String = IM.imListener.getMsgSummary(msg)
        if (!content.isNullOrEmpty()) {
            return content
        }
        val type: Int = getMsgType(msg)
        /**
         * 通知类消息
         */
        if (type == IMConstants.MsgType.imSystem) {
            // TODO 系统提醒
        } else if (type == IMConstants.MsgType.imRecall) {
            // 撤回消息
            content = "[" + VMStr.byRes(R.string.im_recall_already) + "]"
        } else if (type == IMConstants.MsgType.imCallReceive || type == IMConstants.MsgType.imCallSend) {
            // 通话消息
            content = "[" + VMStr.byRes(R.string.im_call) + " - " + (msg.body as EMTextMessageBody).message + "]"
        } else if (type == IMConstants.MsgType.imBigEmotionReceive || type == IMConstants.MsgType.imBigEmotionSend) {
            // 大表情
            content = (msg.body as EMTextMessageBody).message
        } else if (type == IMConstants.MsgType.imTextReceive || type == IMConstants.MsgType.imTextSend) {
            // 文本消息
            content = (msg.body as EMTextMessageBody).message
        } else if (type == IMConstants.MsgType.imPictureReceive || type == IMConstants.MsgType.imPictureSend) {
            // 图片消息
            content = "[" + VMStr.byRes(R.string.im_picture) + "]"
        } else if (type == IMConstants.MsgType.imVoiceReceive || type == IMConstants.MsgType.imVoiceSend) {
            content = "[" + VMStr.byRes(R.string.im_voice) + "]"
        } else {
            // 未知类型消息
            content = "[" + VMStr.byRes(R.string.im_unknown_msg) + "]"
        }
        return content
    }

    /**
     * 是否要显示时间戳
     */
    fun isShowTime(position: Int, msg: EMMessage): Boolean {
        if (position == 0) {
            return true
        }
        val list = getCacheMessages(msg.conversationId(), msg.chatType.ordinal)
        val preMsg = list[position - 1]
        return msg.localTime() - preMsg.localTime() > CConstants.timeMinute * 5
    }
}