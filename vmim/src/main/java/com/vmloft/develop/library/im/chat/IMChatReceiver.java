package com.vmloft.develop.library.im.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.call.multi.IMCallManager;
import com.vmloft.develop.library.im.common.IMConstants;
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
                // 如果正在通话中，拒绝对方通话，这里不更新状态，只是简单的高速对方忙碌中
                IMCallManager.getInstance().busyCall(message.conversationId());
            } else {
                IMCallManager.getInstance().setCallStatus(IMConstants.CallStatus.IM_INCOMING_CALL);
                String conferenceId = message.getStringAttribute(IMConstants.IM_CHAT_CONFERENCE_ID, "");
                // 否则跳转到通话界面
                IMRouter.goIMCall(context, message.conversationId(), conferenceId, true);
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
