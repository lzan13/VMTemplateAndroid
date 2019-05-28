package com.vmloft.develop.library.im.bean;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.utils.IMChatUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/21 15:00
 *
 * 自定义 IMConversation 会话对象
 */
public class IMConversation implements Serializable {

    // 内部会话对象
    private EMConversation mConversation;

    // 会话 id
    private String mId;
    // 草稿
    private String mDraft;
    // 是否置顶
    private boolean isTop;
    // 未读状态
    private boolean isUnread;
    // 最后一次更新时间
    private long mLastTime;
    // 最后一条消息
    private IMMessage mLastMessage;

    /**
     * 空构造
     */
    public IMConversation() {
    }

    /**
     * 根据传入内部需要的会话对象构造 IMConversation
     */
    public IMConversation(EMConversation conversation) {
        mConversation = conversation;
    }

    public void setConversation(EMConversation mConversation) {
        this.mConversation = mConversation;
    }

    public String getId() {
        mId = mConversation.conversationId();
        return mId;
    }

    public String getDraft() {
        mDraft = IMChatUtils.getConversationDraft(mConversation);
        return mDraft;
    }

    public void setDraft(String draft) {
        this.mDraft = draft;
        IMChatUtils.setConversationDraft(mConversation, draft);
    }

    public boolean isTop() {
        isTop = IMChatUtils.getConversationTop(mConversation);
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
        IMChatUtils.setConversationTop(mConversation, top);
    }

    public boolean isUnread() {
        isUnread = IMChatUtils.getConversationUnread(mConversation);
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
        IMChatUtils.setConversationUnread(mConversation, unread);
    }

    public long getLastTime() {
        mLastTime = IMChatUtils.getConversationLastTime(mConversation);
        return mLastTime;
    }

    public void setLastTime(long lastTime) {
        this.mLastTime = lastTime;
        IMChatUtils.setConversationLastTime(mConversation, lastTime);
    }

    public IMMessage getLastMessage() {
        if (mConversation.getAllMsgCount() == 0) {
            mLastMessage = new IMMessage();
        }else{
            mLastMessage = new IMMessage(mConversation.getLastMessage());
        }
        return mLastMessage;
    }

    public void setLastMessage(IMMessage mLastMessage) {
        this.mLastMessage = mLastMessage;
    }

    /**
     * 获取会话的所有消息
     */
    public List<IMMessage> getAllMessage() {
        List<IMMessage> result = new ArrayList<>();
        List<EMMessage> list = mConversation.getAllMessages();
        for (EMMessage message : list) {
            result.add(new IMMessage(message));
        }
        return result;
    }

    /**
     * 加载更多消息
     *
     * @param msgId 开始加载的消息 Id
     * @param limit 加载条数
     */
    public List<IMMessage> loadMoreMessage(String msgId, int limit) {
        List<IMMessage> result = new ArrayList<>();
        List<EMMessage> list = mConversation.loadMoreMsgFromDB(msgId, limit);
        for (EMMessage message : list) {
            result.add(new IMMessage(message));
        }

        return result;
    }

    /**
     * 当会话 id 相同就认为两个会话相同
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof IMConversation) {
            IMConversation bean = (IMConversation) o;
            return mId.equals(bean.mId);
        }
        return super.equals(o);
    }

}