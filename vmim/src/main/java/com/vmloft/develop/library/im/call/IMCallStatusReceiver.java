package com.vmloft.develop.library.im.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hyphenate.chat.EMClient;
import com.vmloft.develop.library.im.common.IMConstants;

/**
 * Created by lzan13 on 2016/10/18.
 *
 * 通话呼叫监听广播实现，用来监听其他账户对自己的呼叫
 */
public class IMCallStatusReceiver extends BroadcastReceiver {

    public IMCallStatusReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean refreshTime = intent.getBooleanExtra(IMConstants.IM_CHAT_CALL_TIME, false);
        if (refreshTime) {
            IMCallFloatWindow.getInstance().refreshCallTime();
        }
    }
}
