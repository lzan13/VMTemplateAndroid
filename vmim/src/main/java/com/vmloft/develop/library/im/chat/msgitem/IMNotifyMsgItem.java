package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;

/**
 * Create by lzan13 on 2019/5/23 22:17
 *
 * 无方向类型消息
 */
public abstract class IMNotifyMsgItem extends IMMsgItem {

    public IMNotifyMsgItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    protected void loadContainer() {
        mInflater.inflate(R.layout.im_chat_item_common_container, this);
    }

    @Override
    protected boolean isReceive() {
        return false;
    }
}
