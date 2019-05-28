package com.vmloft.develop.library.im.common;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.tools.utils.VMLog;
import java.util.List;

/**
 * Created by lzan13 on 2019/05/21.
 *
 * 消息监听实现类
 */
public class IMMessageListener implements EMMessageListener {
    /**
     * 收到新消息，离线消息也都是在这里获取
     * 这里在处理消息监听时根据收到的消息修改了会话对象的最后时间，是为了在会话列表中当清空了会话内容时，
     * 不用过滤掉空会话，并且能显示会话时间
     *
     * @param list 收到的新消息集合，离线和在线都是走这个监听
     */
    @Override
    public void onMessageReceived(List<EMMessage> list) {
        VMLog.i("收到新消息 " + list);
        // 遍历消息集合
        for (EMMessage msg : list) {
            // 更新会话时间
            EMConversation conversation = IMChatManager.getInstance().getConversation(msg.conversationId(), msg.getChatType().ordinal());
            IMChatManager.getInstance().setTime(conversation, msg.localTime());
        }
    }

    /**
     * 收到新的 CMD 消息
     *
     * @param list 收到的透传消息集合
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        for (EMMessage msg : list) {
        }
    }

    /**
     * 收到新的已读回执
     *
     * @param list 收到消息已读回执
     */
    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    /**
     * 收到新的发送回执
     *
     * @param list 收到发送回执的消息集合
     */
    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {

    }

    /**
     * 消息的状态改变
     *
     * @param message 发生改变的消息
     * @param object  包含改变的消息
     */
    @Override
    public void onMessageChanged(EMMessage message, Object object) {
    }
}