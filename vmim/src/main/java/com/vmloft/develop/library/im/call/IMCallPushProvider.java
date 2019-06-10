package com.vmloft.develop.library.im.call;

import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.chat.IMChatManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.utils.VMStr;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzan13 on 2019/06/02 19:25
 *
 * 通话推送信息回掉接口，主要是用来实现当对方不在线时，发送一条消息，推送给对方，让对方上线后能继续收到呼叫
 */
public class IMCallPushProvider implements EMCallManager.EMCallPushProvider {
    @Override
    public void onRemoteOffline(String id) {
//        EMMessage message = IMChatManager.getInstance().createTextMessage(VMStr.byRes(R.string.im_call_push), id, true);
//        boolean isVideoCall = IMCallManager.getInstance().getCallType() == IMCallManager.CallType.VIDEO;
//        message.setAttribute(IMConstants.IM_MSG_EXT_NOTIFY, false);
//        message.setAttribute(IMConstants.IM_MSG_EXT_TYPE, IMConstants.MsgExtType.IM_CALL);
//        message.setAttribute(IMConstants.IM_MSG_EXT_VIDEO_CALL, isVideoCall);
//
//        // 设置强制推送
//        message.setAttribute("em_force_notification", "true");
//        // 设置自定义推送提示
//        JSONObject extObj = new JSONObject();
//        try {
//            extObj.put("em_push_title", VMStr.byRes(R.string.im_call_push));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        message.setAttribute("em_apns_ext", extObj);
//        IMChatManager.getInstance().sendMessage(message, new IMCallback<EMMessage>() {
//            @Override
//            public void onSuccess(EMMessage message) {
//                IMChatManager.getInstance().removeMessage(message);
//            }
//        });
    }
}
