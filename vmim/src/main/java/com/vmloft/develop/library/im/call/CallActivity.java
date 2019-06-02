package com.vmloft.develop.library.im.call;

import android.os.Bundle;
import android.view.WindowManager;
import com.vmloft.develop.library.im.base.IMBaseActivity;

/**
 * Created by lzan13 on 2016/8/8.
 *
 * 通话界面的父类，做一些音视频通话的通用操作
 */
public abstract class CallActivity extends IMBaseActivity {

    // 呼叫方名字
    protected String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置通话界面属性，保持屏幕常亮，关闭输入法，以及解锁
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    /**
     * 接听通话
     */
    protected void answerCall() {
        CallManager.getInstance().answerCall();
    }

    /**
     * 挂断通话
     */
    protected void endCall() {
        if (CallManager.getInstance().isInComingCall() && CallManager.getInstance().getCallStatus() != CallManager.CallStatus.ACCEPTED) {
            CallManager.getInstance().rejectCall();
        } else {
            CallManager.getInstance().endCall();
        }
        onFinish();
    }
}
