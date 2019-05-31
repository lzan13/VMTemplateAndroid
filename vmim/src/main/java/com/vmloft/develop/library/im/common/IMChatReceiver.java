package com.vmloft.develop.library.im.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.call.IMCallManager;
import com.vmloft.develop.library.im.router.IMRouter;
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
        if (body.action().equals(IMConstants.IM_CHAT_ACTION_CALL)) {
            if (IMCallManager.getInstance().isCalling()) {
                // 如果正在通话中，拒绝对方通话
                IMCallManager.getInstance().callReject(message.conversationId());
            } else {
                IMCallManager.getInstance().setCallStatus(IMConstants.CallStatus.IM_INCOMING_CALL);
                // 否则跳转到通话界面
                IMRouter.goIMCall(context, message.conversationId(), true);
            }
        } else if (body.action().equals(IMConstants.IM_CHAT_ACTION_CALL_REJECT)) {
            IMCallManager.getInstance().setCallStatus(IMConstants.CallStatus.IM_REJECTED);
        } else if (body.action().equals(IMConstants.IM_CHAT_ACTION_CALL_END)) {
            IMCallManager.getInstance().setCallStatus(IMConstants.CallStatus.IM_END);
        }
    }

    /**
     * 收到新消息
     */
    private void newMessageAction(Context context, EMMessage message) {

    }
}
