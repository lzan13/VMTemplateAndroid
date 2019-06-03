package com.vmloft.develop.library.im.chat;

import android.content.Intent;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;

import java.util.List;

/**
 * Created by lzan13 on 2019/05/21.
 *
 * 消息监听实现类
 */
public class IMChatListener implements EMMessageListener {
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
            // 先获取联系人信息再通知消息刷新
            IMContact contact = IM.getInstance().getIMContact(msg.conversationId());
            if (VMStr.isEmpty(contact.mNickname) && VMStr.isEmpty(contact.mAvatar)) {
                IM.getInstance().getIMContact(msg.conversationId(), new IMCallback<IMContact>() {
                    @Override
                    public void onSuccess(IMContact imContact) {
                        // 通知有新消息来了，每条消息都需要单独通知，接收方根据自己需要判断后续操作
                        sendBroadcast(IMUtils.Action.getNewMessageAction(), msg);
                        // 会话也需要刷新
                        IMUtils.sendLocalBroadcast(IMUtils.Action.getRefreshConversationAction());
                    }
                });
            } else {
                // 通知有新消息来了，每条消息都需要单独通知，接收方根据自己需要判断后续操作
                sendBroadcast(IMUtils.Action.getNewMessageAction(), msg);
                // 会话也需要刷新
                IMUtils.sendLocalBroadcast(IMUtils.Action.getRefreshConversationAction());
            }
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
            // 通知有新 CMD 消息来了，每条消息都需要单独通知，接收方根据自己需要判断后续操作
            sendBroadcast(IMUtils.Action.getCMDMessageAction(), msg);
        }
    }

    /**
     * 收到新的已读回执
     *
     * @param list 收到已读回执消息集合
     */
    @Override
    public void onMessageRead(List<EMMessage> list) {
        for (EMMessage msg : list) {
            // 通知有消息更新了，每条消息都需要单独通知，接收方根据自己需要判断后续操作
            sendBroadcast(IMUtils.Action.getUpdateMessageAction(), msg);
        }
    }

    /**
     * 收到新的发送回执
     *
     * @param list 收到发送回执的消息集合
     */
    @Override
    public void onMessageDelivered(List<EMMessage> list) {
        for (EMMessage msg : list) {
            // 通知有消息更新了，每条消息都需要单独通知，接收方根据自己需要判断后续操作
            sendBroadcast(IMUtils.Action.getUpdateMessageAction(), msg);
        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {
        for (EMMessage msg : list) {
            // 通知有消息更新了，每条消息都需要单独通知，接收方根据自己需要判断后续操作
            sendBroadcast(IMUtils.Action.getUpdateMessageAction(), msg);
        }
    }

    /**
     * 消息的状态改变
     *
     * @param message 发生改变的消息
     * @param object  包含改变的消息
     */
    @Override
    public void onMessageChanged(EMMessage message, Object object) {
        // 通知有消息更新了，接收方根据自己需要判断后续操作
        sendBroadcast(IMUtils.Action.getUpdateMessageAction(), message);
    }

    /**
     * 统一发送广播方法
     *
     * @param action  广播的 action
     * @param message 消息
     */
    private void sendBroadcast(String action, EMMessage message) {
        Intent intent = new Intent(action);
        intent.putExtra(IMConstants.IM_CHAT_ID, message.conversationId());
        intent.putExtra(IMConstants.IM_CHAT_MSG, message);
        IMUtils.sendLocalBroadcast(intent);
    }
}