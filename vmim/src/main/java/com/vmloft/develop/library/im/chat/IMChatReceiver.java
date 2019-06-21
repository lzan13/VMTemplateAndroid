package com.vmloft.develop.library.im.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.notify.IMNotifier;
import com.vmloft.develop.library.im.utils.IMUtils;

/**
 * Create by lzan13 on 2019/5/30 21:49
 *
 * 通话部分全局广播接收器，做一些统一的处理
 */
public class IMChatReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EMMessage message = intent.getParcelableExtra(IMConstants.IM_CHAT_MSG);
        if (intent.getAction().equals(IMUtils.Action.getCMDMessageAction())) {
            cmdAction(context, message);
        } else if (intent.getAction().equals(IMUtils.Action.getNewMessageAction())) {
            newMessageAction(context, message);
        }
    }

    /**
     * 处理 CMD 消息
     */
    private void cmdAction(Context context, EMMessage message) {
        EMCmdMessageBody body = (EMCmdMessageBody) message.getBody();
        if (body.action().equals(IMConstants.IM_MSG_ACTION_CONTACT_CHANGE)) {
            IM.getInstance().getIMContact(message.conversationId(),null);
        }
    }

    /**
     * 收到新消息
     */
    private void newMessageAction(Context context, EMMessage message) {
        String chatId = message.conversationId();
        if (IM.getInstance().isCurrChat(chatId)) {

        } else {
            // 只有需要发送通知的消息才发送通知栏提醒
            boolean notify = message.getBooleanAttribute(IMConstants.IM_MSG_EXT_NOTIFY, false);
            if (notify) {
                IMNotifier.getInstance().notifyMessage(message);
            }
        }
    }
}
