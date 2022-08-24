package com.vmloft.develop.library.im.common

import android.os.Bundle
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.notify.NotifyManager
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.im.bean.IMConversation
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.db.IMDatabase
import com.vmloft.develop.library.tools.utils.logger.VMLog

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * Create by lzan13 2022/8/12
 * 描述：会话管理类
 */
object IMConversationManager {

    private val conversationMap = mutableMapOf<String, IMConversation>()

    /**
     * 创建会话
     * @param chatId 会话 id
     * @param chatType 会话类型
     */
    fun createConversation(chatId: String, chatType: Int = IMConstants.ChatType.imSingle): IMConversation {
        val conversation = IMConversation(chatId, chatType)
        conversation.time = System.currentTimeMillis()
        conversationMap[chatId] = conversation
        VMLog.i("-lz-saveConversation-0")
        // 这里使用协程操作数据
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            VMLog.i("-lz-saveConversation-launch-0")
            IMDatabase.getInstance().conversationDao().insert(conversation)
            VMLog.i("-lz-saveConversation-launch-1")
        }
        VMLog.i("-lz-saveConversation-1")
        return conversation
    }

    /**
     * 删除会话，仅删除内存
     * @param chatId 会话 id
     */
    fun deleteConversation(chatId: String) {
        conversationMap.remove(chatId)
    }

    /**
     * 删除会话
     * @param conversation 会话
     */
    fun deleteConversation(conversation: IMConversation) {
        conversationMap.remove(conversation.chatId)
        VMLog.i("-lz-deleteConversation-0")
        // 这里使用协程操作数据
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            VMLog.i("-lz-deleteConversation-launch-0")
            IMDatabase.getInstance().conversationDao().delete(conversation)
            VMLog.i("-lz-deleteConversation-launch-1")
        }
        VMLog.i("-lz-deleteConversation-1")
    }

    /**
     * 更新会话
     * @param conversation 会话
     */
    fun updateConversation(conversation: IMConversation) {
        VMLog.i("-lz-updateConversation-0")
        // 这里使用协程操作数据
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            VMLog.i("-lz-updateConversation-launch-0")
            IMDatabase.getInstance().conversationDao().update(conversation)
            VMLog.i("-lz-updateConversation-launch-1")
        }
        VMLog.i("-lz-updateConversation-1")
    }

    /**
     * 设置当前会话草稿
     *
     * @param conversation 需要设置的会话对象
     * @param draft 需要设置的草稿内容
     */
    fun setConversationDraft(conversation: IMConversation, draft: String) {
        if (conversation.draft != draft) {
            conversation.draft = draft
            // 更新数据库
            updateConversation(conversation)
        }
    }

    /**
     * 设置会话置顶状态
     *
     * @param conversation 要置顶的会话对象
     * @param top 设置会话是否置顶
     */
    fun setConversationTop(conversation: IMConversation, top: Boolean) {
        conversation.top = top
        // 更新数据库
        updateConversation(conversation)
    }

    /**
     * 标记会话为未读状态
     *
     * @param conversation 需要标记的会话
     * @param unread 设置未读状态
     */
    fun setConversationUnread(conversation: IMConversation, unread: Boolean) {
        // 设置为已读的时候清空下原有未读数据
        if (unread) {
            conversation.unread = 1
        } else {
            conversation.unread = 0
        }
        // 更新数据库
        updateConversation(conversation)
        // 通知未读更新
        LDEventBus.post(IMConstants.Common.changeUnreadCount, getAllUnread())
    }

    /**
     * 清空全部未读数
     */
    fun clearAllUnread() {
        val conversations = getAllConversation()
        val list = conversations.filter { it.unread > 0 }.map {
            it.unread = 0
            it
        }
        VMLog.i("-lz-clearAllUnread-0")
        // 这里使用协程操作数据
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            VMLog.i("-lz-clearAllUnread-launch-0")
            IMDatabase.getInstance().conversationDao().update(*list.toTypedArray())
            VMLog.i("-lz-clearAllUnread-launch-1")
        }
        VMLog.i("-lz-clearAllUnread-0")
    }

    /**
     * 根据会话 id 获取会话
     *
     * @param id 会话 id
     */
    fun getConversation(id: String): IMConversation? {
        return conversationMap[id]
    }

    /**
     * 根据会话 id 获取会话
     *
     * @param chatId 会话 id
     * @param chatType 会话类型
     */
    fun getConversation(chatId: String, chatType: Int = IMConstants.ChatType.imSingle): IMConversation {
        var conversation = conversationMap[chatId]
        // 为空时创建会话
        if (conversation == null) {
            conversation = createConversation(chatId, chatType)
        }
        return conversation
    }

    /**
     * 获取全部会话，并进行排序
     */
    fun getAllConversation(): List<IMConversation> {
        // 排序
        val sortList = mutableListOf<IMConversation>()
        sortList.addAll(conversationMap.values)
        sortList.sortByDescending { it.time }

        // 排序之后，重新将置顶的条目设置到顶部
        val resultList = mutableListOf<IMConversation>()
        var count = 0
        for (item in sortList) {
            if (item.top) {
                resultList.add(count, item)
                count++
            } else {
                resultList.add(item)
            }
        }
        return resultList
    }

    /**
     * 从本地加载会话
     */
    fun loadAllConversationFromDB() {
        VMLog.i("-lz-loadAllConversationFromDB-0")
        // 这里使用协程操作数据
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            VMLog.i("-lz-loadAllConversationFromDB-launch-0")
            val conversations = IMDatabase.getInstance().conversationDao().all()
            VMLog.i("-lz-loadAllConversationFromDB-launch-1")
            conversations.forEach {
                conversationMap[it.chatId] = it
            }
            VMLog.i("-lz-loadAllConversationFromDB-launch-2")
        }
        VMLog.i("-lz-loadAllConversationFromDB-1")
    }

    /**
     * 获取全部未读数，包括标记未读
     */
    fun getAllUnread(): Int {
        return conversationMap.values.sumOf { it.unread }
    }

    /**
     * 添加消息
     */
    fun addMessage(message: IMMessage) {
        val conversation = getConversation(message.chatId, message.chatType)
        if (IMChatManager.isCurrChat(message.chatId)) {
            conversation.addMessage(message)
        } else {
            val user = CacheManager.getUser(message.chatId)
            val content = IMChatManager.getSummary(message)
            val title = user.nickname
            val bundle = Bundle()
            bundle.putString(CConstants.Notify.notifyBName, CConstants.Notify.notifyBNameIM)
            bundle.putString(CConstants.Notify.notifyChatId, message.chatId)
            NotifyManager.sendNotify(content, title, bundle)
        }
        val mstType = IMChatManager.getMsgType(message)
        // 消息数+1
        if (!message.isLocal && mstType != IMConstants.MsgType.imSystemRecall) {
            conversation.msgCountAdd(message.isSend)
        }
        // 当前会话消息不增加未读
        if (!IMChatManager.isCurrChat(message.chatId) && !message.isSend && !message.isLocal && mstType != IMConstants.MsgType.imSystemRecall) {
            conversation.unread++

            // 通知未读更新
            LDEventBus.post(IMConstants.Common.changeUnreadCount, getAllUnread())
        }
        if (conversation.time <= message.time) {
            conversation.time = message.time
            conversation.content = IMChatManager.getSummary(message)
        }

        // 更新数据库
        updateConversation(conversation)

        // 通知聊天界面与会话列表刷新
        if (mstType == IMConstants.MsgType.imSystemRecall){
            LDEventBus.post(IMConstants.Common.updateMsgEvent, message)
        }else{
            LDEventBus.post(IMConstants.Common.newMsgEvent, message)
        }

        VMLog.i("-lz-addMessage-0")
        // 这里使用协程操作数据
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            VMLog.i("-lz-addMessage-launch-0")
            IMDatabase.getInstance().messageDao().insert(message)
            VMLog.i("-lz-addMessage-launch-1")
        }
        VMLog.i("-lz-addMessage-1")
    }

    /**
     * 删除消息
     */
    fun removeMessage(message: IMMessage) {
        val conversation = getConversation(message.chatId, message.chatType)
        conversation.removeMessage(message)
        VMLog.i("-lz-removeMessage-0")
        // 这里使用协程操作数据
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            VMLog.i("-lz-removeMessage-launch-0")
            IMDatabase.getInstance().messageDao().delete(message)
            VMLog.i("-lz-removeMessage-launch-1")
        }
        VMLog.i("-lz-removeMessage-1")
    }

    /**
     * 清空会话，这里会删除内存和数据库的数据
     *
     * @param chatId 会话 id
     * @param db 是否同时删除数据库消息
     */
    fun clearMsg(conversation: IMConversation, db: Boolean = false) {
        conversation.clearMsg()
        if (db) {
            VMLog.i("-lz-clearMsg-0")
            // 这里使用协程操作数据
            val scope = CoroutineScope(Job() + Dispatchers.IO)
            scope.launch {
                VMLog.i("-lz-clearMsg-launch-0")
                IMDatabase.getInstance().messageDao().clearMsg(conversation.chatId)
                VMLog.i("-lz-clearMsg-launch-1")
            }
            VMLog.i("-lz-clearMsg-1")
        }
    }

    /**
     * 更新消息
     */
    fun updateMessage(message: IMMessage) {
        val conversation = getConversation(message.chatId, message.chatType)
        conversation.updateMessage(message)
        VMLog.i("-lz-updateMessage-0")
        // 这里使用协程操作数据
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            VMLog.i("-lz-updateMessage-launch-0")
            IMDatabase.getInstance().messageDao().update(message)
            VMLog.i("-lz-updateMessage-launch-1")
        }
        VMLog.i("-lz-updateMessage-1")

        LDEventBus.post(IMConstants.Common.updateMsgEvent, message)
    }

    /**
     * 获取当前会话总消息数量
     */
    fun getMessagesCount(id: String, chatType: Int): Int {
        val conversation = getConversation(id, chatType)
        return conversation.receiveCount + conversation.sendCount
    }

    /**
     * 获取会话已加载所有消息
     */
    fun getCacheMessages(id: String, chatType: Int): List<IMMessage> {
        val conversation = getConversation(id, chatType)
        return conversation.msgList
    }

    /**
     * 获取缓存中的图片消息，主要用来预览图片
     */
    fun getCachePictureMessage(id: String, chatType: Int): List<IMMessage> {
        val result = mutableListOf<IMMessage>()
        for (message in getCacheMessages(id, chatType)) {
            if (message.type == IMConstants.MsgType.imPicture) {
                result.add(message)
            }
        }
        return result
    }

    /**
     * 获取当前会话的所有消息
     */
    suspend fun loadMoreMessage(conversation: IMConversation): List<IMMessage> {
        // 这里是降序排列，所以需要做个倒序
        val time = if (conversation.msgList.isEmpty()) System.currentTimeMillis() else conversation.msgList.first().time
        val list = IMDatabase.getInstance().messageDao().query(conversation.chatId, time).reversed()
        if (list.isNotEmpty()) {
            conversation.addMessages(list, true)
        }
        return list
    }

    /**
     * 获取消息位置
     */
    fun getPosition(message: IMMessage): Int {
        val conversation = getConversation(message.chatId, message.chatType)
        return conversation.msgList.indexOf(message)
    }

    /**
     * 退出登录需要清空下内存数据
     */
    fun reset(){
        conversationMap.clear()
    }

}