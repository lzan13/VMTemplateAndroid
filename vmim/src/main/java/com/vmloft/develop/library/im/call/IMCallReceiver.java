package com.vmloft.develop.library.im.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hyphenate.chat.EMClient;

/**
 * Created by lzan13 on 2016/10/18.
 *
 * 通话呼叫监听广播实现，用来监听其他账户对自己的呼叫
 */
public class IMCallReceiver extends BroadcastReceiver {

    public IMCallReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        // 判断环信是否登录成功
        if (!EMClient.getInstance().isLoggedInBefore()) {
            return;
        }
        // 呼叫方的 Id
        String fromId = intent.getStringExtra("from");
        // 呼叫类型，有语音和视频两种
        String callType = intent.getStringExtra("type");
        // 呼叫接收方
        String toId = intent.getStringExtra("to");

        // 判断下当前被呼叫的为自己的时候才启动通话界面 TODO 这个当不同 AppKey 下相同的 username 时就无效了
        if (toId.equals(EMClient.getInstance().getCurrentUser())) {
            int type = IMCallManager.CallType.VOICE;
            // 根据通话类型跳转到语音通话或视频通话界面
            if (callType.equals("video")) {
                // 设置当前通话类型为视频通话
                type = IMCallManager.CallType.VIDEO;
            } else if (callType.equals("voice")) {
                // 设置当前通话类型为语音通话
                type = IMCallManager.CallType.VOICE;
            }
            // 初始化通化管理类的一些属性
            IMCallManager.getInstance().inComingCall(fromId, type);
        }
    }
}
