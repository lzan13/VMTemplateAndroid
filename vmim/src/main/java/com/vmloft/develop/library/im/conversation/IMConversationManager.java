package com.vmloft.develop.library.im.conversation;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.utils.IMConversationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Create by lzan13 on 2019/5/9 10:24
 *
 * IM 最近会话管理类
 */
public class IMConversationManager {

    /**
     * 私有的构造方法
     */
    private IMConversationManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMConversationManager INSTANCE = new IMConversationManager();
    }

    /**
     * 获取单例类实例
     */
    public static IMConversationManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    public void init(Context context) {
        // 将会话加载到内存
        EMClient.getInstance().chatManager().loadAllConversations();
    }

    /**
     * 获取全部会话，并进行排序
     */
    public List<EMConversation> getAllConversation() {
        Map<String, EMConversation> map = EMClient.getInstance().chatManager().getAllConversations();
        List<EMConversation> list = new ArrayList<>();
        list.addAll(map.values());
        // 排序
        Collections.sort(list, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation o1, EMConversation o2) {
                if (IMConversationUtils.getConversationLastTime(o1) > IMConversationUtils.getConversationLastTime(o2)) {
                    return -1;
                } else if (IMConversationUtils.getConversationLastTime(o1) < IMConversationUtils.getConversationLastTime(o2)) {
                    return 1;
                }
                return 0;
            }
        });

        // 排序之后，重新将置顶的条目设置到顶部
        List<EMConversation> result = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (IMConversationUtils.getConversationTop(list.get(i))) {
                result.add(count, list.get(i));
                count++;
            } else {
                result.add(list.get(i));
            }
        }
        return result;
    }

    /**
     * 根据会话 id 获取会话
     *
     * @param id 会话 id
     */
    public EMConversation getConversation(String id) {
        return EMClient.getInstance().chatManager().getConversation(id);
    }

    /**
     * 根据会话 id 获取会话
     *
     * @param id       会话 id
     * @param chatType 会话类型
     */
    public EMConversation getConversation(String id, int chatType) {
        EMConversation.EMConversationType conversationType = wrapConversationType(chatType);
        // 为空时创建会话
        return EMClient.getInstance().chatManager().getConversation(id, conversationType, true);
    }

    /**
     * 获取会话草稿
     */
    public String getDraft(String id, int chatType) {
        return IMConversationUtils.getConversationDraft(getConversation(id, chatType));
    }

    /**
     * 获取会话草稿
     */
    public void setDraft(String id, int chatType, String draft) {
        IMConversationUtils.setConversationDraft(getConversation(id, chatType), draft);
    }

    /**
     * 获取会话时间
     */
    public long getTime(String id, int chatType) {
        return IMConversationUtils.getConversationLastTime(getConversation(id, chatType));
    }

    /**
     * 获取会话草稿
     */
    public void setTime(String id, int chatType, long time) {
        IMConversationUtils.setConversationLastTime(getConversation(id, chatType), time);
    }
    /**
     * --------------------------- 消息相关 ---------------------------
     */
    /**
     * 清空未读数
     */
    public void clearUnreadCount(String id, int chatType) {
        EMConversation conversation = getConversation(id, chatType);
        conversation.markAllMessagesAsRead();
        IMConversationUtils.setConversationUnread(conversation, false);
    }

    /**
     * 获取当前会话总消息数量
     */
    public int getMessagesCount(String id, int chatType) {
        EMConversation conversation = getConversation(id, chatType);
        return conversation.getAllMsgCount();
    }

    /**
     * 获取会话已加载所有消息
     */
    public List<EMMessage> getCacheMessages(String id, int chatType) {
        EMConversation conversation = getConversation(id, chatType);
        return conversation.getAllMessages();
    }

    /**
     * 获取当前会话的所有消息
     */
    public List<EMMessage> loadMoreMessages(String id, int chatType, String msgId) {
        EMConversation conversation = getConversation(id, chatType);
        if (conversation != null) {
            return conversation.loadMoreMsgFromDB(msgId, IMConstants.IM_CHAT_MSG_LIMIT);
        }
        return null;
    }

    /**
     * 获取指定消息
     *
     * @param id    会话 id
     * @param msgId 消息 id
     * @return
     */
    public EMMessage getMessage(String id, String msgId) {
        EMConversation conversation = getConversation(id);
        if (conversation == null) {
            return null;
        }
        return conversation.getMessage(msgId, false);
    }

    /**
     * 获取消息位置
     */
    public int getPosition(EMMessage message) {
        EMConversation conversation = getConversation(message.conversationId(), message.getChatType().ordinal());
        return conversation.getAllMessages().indexOf(message);
    }

    /**
     * 包装会话类型
     */
    public EMConversation.EMConversationType wrapConversationType(int chatType) {
        if (chatType == IMConstants.ChatType.IM_SINGLE_CHAT) {
            return EMConversation.EMConversationType.Chat;
        } else if (chatType == IMConstants.ChatType.IM_GROUP_CHAT) {
            return EMConversation.EMConversationType.GroupChat;
        } else if (chatType == IMConstants.ChatType.IM_CHAT_ROOM) {
            return EMConversation.EMConversationType.ChatRoom;
        }
        return EMConversation.EMConversationType.Chat;
    }

    /**
     * 包装聊天类型
     */
    public EMMessage.ChatType wrapChatType(int chatType) {
        if (chatType == IMConstants.ChatType.IM_SINGLE_CHAT) {
            return EMMessage.ChatType.Chat;
        } else if (chatType == IMConstants.ChatType.IM_GROUP_CHAT) {
            return EMMessage.ChatType.GroupChat;
        } else if (chatType == IMConstants.ChatType.IM_CHAT_ROOM) {
            return EMMessage.ChatType.ChatRoom;
        }
        return EMMessage.ChatType.Chat;
    }
}
