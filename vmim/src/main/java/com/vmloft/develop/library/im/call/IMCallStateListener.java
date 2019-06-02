package com.vmloft.develop.library.im.call;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.vmloft.develop.library.tools.utils.VMLog;

/**
 * Created by lzan13 on 2016/10/18.
 *
 * 通话状态监听类，用来监听通话过程中状态的变化
 */

public class IMCallStateListener implements EMCallStateChangeListener {

    @Override public void onCallStateChanged(CallState callState, CallError callError) {
        switch (callState) {
            case CONNECTING: // 正在呼叫对方，TODO 没见回调过
                VMLog.i("正在呼叫对方" + callError);
                CallManager.getInstance().setCallStatus(CallManager.CallStatus.CONNECTING);
                break;
            case CONNECTED: // 正在等待对方接受呼叫申请（对方申请与你进行通话）
                VMLog.i("正在连接" + callError);
                CallManager.getInstance().setCallStatus(CallManager.CallStatus.CONNECTED);
                break;
            case ACCEPTED: // 通话已接通
                VMLog.i("通话已接通");
                CallManager.getInstance().stopCallSound();
                CallManager.getInstance().startCallTime();
                CallManager.getInstance().setEndType(CallManager.CallEndType.NORMAL);
                CallManager.getInstance().setCallStatus(CallManager.CallStatus.ACCEPTED);
                break;
            case DISCONNECTED: // 通话已中断
                VMLog.i("通话已结束" + callError);
                // 通话结束，重置通话状态
                if (callError == CallError.ERROR_UNAVAILABLE) {
                    VMLog.i("对方不在线" + callError);
                    CallManager.getInstance().setEndType(CallManager.CallEndType.OFFLINE);
                } else if (callError == CallError.ERROR_BUSY) {
                    VMLog.i("对方正忙" + callError);
                    CallManager.getInstance().setEndType(CallManager.CallEndType.BUSY);
                } else if (callError == CallError.REJECTED) {
                    VMLog.i("对方已拒绝" + callError);
                    CallManager.getInstance().setEndType(CallManager.CallEndType.REJECTED);
                } else if (callError == CallError.ERROR_NORESPONSE) {
                    VMLog.i("对方未响应，可能手机不在身边" + callError);
                    CallManager.getInstance().setEndType(CallManager.CallEndType.NORESPONSE);
                } else if (callError == CallError.ERROR_TRANSPORT) {
                    VMLog.i("连接建立失败" + callError);
                    CallManager.getInstance().setEndType(CallManager.CallEndType.TRANSPORT);
                } else if (callError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED) {
                    VMLog.i("双方通讯协议不同" + callError);
                    CallManager.getInstance().setEndType(CallManager.CallEndType.DIFFERENT);
                } else if (callError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
                    VMLog.i("双方通讯协议不同" + callError);
                    CallManager.getInstance().setEndType(CallManager.CallEndType.DIFFERENT);
                } else {
                    VMLog.i("通话已结束 %s", callError);
                    if (CallManager.getInstance().getEndType() == CallManager.CallEndType.CANCEL) {
                        CallManager.getInstance().setEndType(CallManager.CallEndType.CANCELLED);
                    }
                }
                // 通话结束，保存消息
                CallManager.getInstance().saveCallMessage();
                CallManager.getInstance().reset();
                break;
            case NETWORK_UNSTABLE:
                if (callError == EMCallStateChangeListener.CallError.ERROR_NO_DATA) {
                    VMLog.i("没有通话数据" + callError);
                } else {
                    VMLog.i("网络不稳定" + callError);
                }
                break;
            case NETWORK_NORMAL:
                VMLog.i("网络正常");
                break;
            // TODO 3.3.0版本 SDK 下边四个暂时都没有回调
            case VIDEO_PAUSE:
                VMLog.i("视频传输已暂停");
                break;
            case VIDEO_RESUME:
                VMLog.i("视频传输已恢复");
                break;
            case VOICE_PAUSE:
                VMLog.i("语音传输已暂停");
                break;
            case VOICE_RESUME:
                VMLog.i("语音传输已恢复");
                break;
            default:
                break;
        }
    }
}
