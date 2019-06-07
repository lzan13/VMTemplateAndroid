package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;

import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;

/**
 * Create by lzan13 on 2019/5/23 22:17
 *
 * 未知类型消息统一展示提示语
 */
public class IMUnknownItem extends IMNotifyItem {

    public IMUnknownItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    public void onBind(int position, EMMessage message) {
        super.onBind(position, message);
        mContentView.setText(R.string.im_unknown_msg);
    }
}
