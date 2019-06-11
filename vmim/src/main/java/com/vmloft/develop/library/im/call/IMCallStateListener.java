package com.vmloft.develop.library.im.call;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Created by lzan13 on 2016/10/18.
 *
 * 通话状态监听类，用来监听通话过程中状态的变化
 */

public class IMCallStateListener implements EMCallStateChangeListener {

    @Override
    public void onCallStateChanged(CallState callState, CallError callError) {
        switch (callState) {
        case CONNECTING: // 正在呼叫对方，TODO 没见回调过
            VMLog.i("正在呼叫对方" + callError);
            IMCallManager.getInstance().setCallStatus(IMCallManager.CallStatus.CONNECTING);
            IMCallManager.getInstance().setCallStatusInfo(VMStr.byRes(R.string.im_call_out));
            break;
        case CONNECTED: // 正在等待对方接受呼叫申请（对方申请与你进行通话）
            VMLog.i("正在连接" + callError);
            IMCallManager.getInstance().setCallStatus(IMCallManager.CallStatus.CONNECTED);
            if (IMCallManager.getInstance().isInComingCall()) {
                IMCallManager.getInstance().setCallStatusInfo(VMStr.byRes(R.string.im_call_incoming));
            } else {
                IMCallManager.getInstance().setCallStatusInfo(VMStr.byRes(R.string.im_call_out_wait));
            }
            break;
        case ACCEPTED: // 通话已接通
            VMLog.i("通话已接通");
            IMCallManager.getInstance().setCallStatusInfo(VMStr.byRes(R.string.im_call_accepted));
            // 接通之后默认关闭扬声器，保护通话隐私
            IMCallManager.getInstance().openSpeaker(false);
            IMCallManager.getInstance().stopPlaySound();
            IMCallManager.getInstance().startCallTime();
            IMCallManager.getInstance().setEndType(IMCallManager.CallEndType.NORMAL);
            IMCallManager.getInstance().setCallStatus(IMCallManager.CallStatus.ACCEPTED);
            break;
        case DISCONNECTED: // 通话已中断
            VMLog.i("通话已结束" + callError);
            IMCallManager.getInstance().setCallStatusInfo(VMStr.byRes(R.string.im_call_disconnected));
            // 通话结束，重置通话状态
            if (callError == CallError.ERROR_UNAVAILABLE) {
                VMLog.i("对方不在线" + callError);
                IMCallManager.getInstance().setEndType(IMCallManager.CallEndType.OFFLINE);
            } else if (callError == CallError.ERROR_BUSY) {
                VMLog.i("对方正忙" + callError);
                IMCallManager.getInstance().setEndType(IMCallManager.CallEndType.BUSY);
            } else if (callError == CallError.REJECTED) {
                VMLog.i("对方已拒绝" + callError);
                IMCallManager.getInstance().setEndType(IMCallManager.CallEndType.REJECTED);
            } else if (callError == CallError.ERROR_NORESPONSE) {
                VMLog.i("对方未响应，可能手机不在身边" + callError);
                IMCallManager.getInstance().setEndType(IMCallManager.CallEndType.NORESPONSE);
            } else if (callError == CallError.ERROR_TRANSPORT) {
                VMLog.i("连接建立失败" + callError);
                IMCallManager.getInstance().setEndType(IMCallManager.CallEndType.TRANSPORT);
            } else if (callError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED) {
                VMLog.i("双方通讯协议不同" + callError);
                IMCallManager.getInstance().setEndType(IMCallManager.CallEndType.DIFFERENT);
            } else if (callError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
                VMLog.i("双方通讯协议不同" + callError);
                IMCallManager.getInstance().setEndType(IMCallManager.CallEndType.DIFFERENT);
            } else {
                VMLog.i("通话已结束 %s", callError);
                if (IMCallManager.getInstance().getEndType() == IMCallManager.CallEndType.CANCEL) {
                    IMCallManager.getInstance().setEndType(IMCallManager.CallEndType.CANCELLED);
                }
            }
            // 通话结束，保存消息
            IMCallManager.getInstance().saveCallMessage();
            IMCallManager.getInstance().reset();
            break;
        case NETWORK_UNSTABLE:
            if (callError == EMCallStateChangeListener.CallError.ERROR_NO_DATA) {
                VMLog.i("没有通话数据" + callError);
                IMCallManager.getInstance().setCallStatusInfo(VMStr.byRes(R.string.im_call_no_data));
            } else {
                VMLog.i("网络不稳定" + callError);
                IMCallManager.getInstance().setCallStatusInfo(VMStr.byRes(R.string.im_call_network_unstable));
            }
            break;
        case NETWORK_NORMAL:
            VMLog.i("网络正常");
            break;
        // TODO 3.3.0版本 SDK 下边四个暂时都没有回调
        case VIDEO_PAUSE:
            VMLog.i("视频传输已暂停");
            IMCallManager.getInstance().setCallStatusInfo(VMStr.byRes(R.string.im_call_video_pause));
            break;
        case VIDEO_RESUME:
            VMLog.i("视频传输已恢复");
            IMCallManager.getInstance().setCallStatusInfo(VMStr.byRes(R.string.im_call_video_resume));
            break;
        case VOICE_PAUSE:
            VMLog.i("语音传输已暂停");
            IMCallManager.getInstance().setCallStatusInfo(VMStr.byRes(R.string.im_call_voice_pause));
            break;
        case VOICE_RESUME:
            VMLog.i("语音传输已恢复");
            IMCallManager.getInstance().setCallStatusInfo(VMStr.byRes(R.string.im_call_voice_resume));
            break;
        default:
            break;
        }
        // 发送广播通知通话状态改变
        IMUtils.sendLocalBroadcast(IMUtils.Action.getCallStatusChangeAction());
    }
}
