package com.vmloft.develop.library.im.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.common.IMConstants;
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

    }

    /**
     * 收到新消息
     */
    private void newMessageAction(Context context, EMMessage message) {

    }
}
