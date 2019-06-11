package com.vmloft.develop.library.im.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.view.WindowManager;
import com.vmloft.develop.library.im.base.IMBaseActivity;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.utils.IMUtils;

/**
 * Created by lzan13 on 2016/8/8.
 *
 * 通话界面的父类，做一些音视频通话的通用操作
 */
public abstract class IMCallActivity extends IMBaseActivity {

    // 对方 Id
    protected String mId;
    protected IMContact mContact;
    protected IMContact mSelfContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置通话界面属性，保持屏幕常亮，关闭输入法，以及解锁
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initReceiver();
        if (IMCallManager.getInstance().getCallStatus() != IMCallManager.CallStatus.DISCONNECTED) {
            IMCallManager.getInstance().fillCall();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    /**
     * 接听通话
     */
    protected void answerCall() {
        IMCallManager.getInstance().answerCall();
    }

    /**
     * 挂断通话
     */
    protected void endCall() {
        if (IMCallManager.getInstance().isInComingCall() && IMCallManager.getInstance()
            .getCallStatus() != IMCallManager.CallStatus.ACCEPTED) {
            IMCallManager.getInstance().rejectCall();
        } else {
            IMCallManager.getInstance().endCall();
        }
        onFinish();
    }

    @Override
    public void onBackPressed() {
        miniCall();
    }

    /**
     * 监听通话界面是否隐藏
     */
    @Override
    protected void onUserLeaveHint() {
        miniCall();
    }

    /**
     * 最小化通话界面
     */
    private void miniCall() {
        //权限判断
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //启动 Activity 让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
                return;
            }
        }
        IMCallManager.getInstance().miniCall();
        onFinish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(mActivity)) {
                    IMCallManager.getInstance().miniCall();
                    onFinish();
                }
            }
        }
    }

    /**
     * 通话状态改变
     */
    protected abstract void onStatusChange();

    /**
     * 刷新通话时间
     */
    protected abstract void onRefreshCallTime();

    /**
     * ------------------------------- 广播接收器部分 -------------------------------
     */
    private CallStatusReceiver mReceiver = new CallStatusReceiver();

    /**
     * 初始化注册广播接收器
     */
    protected void initReceiver() {
        // 注册广播接收器
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter filter = new IntentFilter(IMUtils.Action.getCallStatusChangeAction());
        lbm.registerReceiver(mReceiver, filter);
    }

    /**
     * 取消注册广播接收器
     */
    protected void unregisterReceiver() {
        // 取消新消息广播接收器
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mReceiver);
    }

    /**
     * 定义广播接收器
     */
    private class CallStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean refreshTime = intent.getBooleanExtra(IMConstants.IM_CHAT_CALL_TIME, false);
            if (refreshTime) {
                onRefreshCallTime();
            } else {
                onStatusChange();
            }
            if (IMCallManager.getInstance().getCallStatus() == IMCallManager.CallStatus.DISCONNECTED) {
                onFinish();
            }
        }
    }
}
