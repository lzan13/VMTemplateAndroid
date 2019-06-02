package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;

/**
 * Create by lzan13 on 2019/5/23 22:17
 *
 * 无方向类型通知类消息
 */
public abstract class IMNotifyItem extends IMBaseItem {

    protected TextView mContentView;

    public IMNotifyItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    protected void loadContainer() {
        mInflater.inflate(R.layout.im_chat_item_notify_container, this);
    }

    @Override
    protected boolean isReceiveMessage() {
        return false;
    }

    @Override
    protected View layoutView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.im_chat_item_notify_common, null);
        mContentView = view.findViewById(R.id.im_chat_msg_content_tv);
        return view;
    }

    @Override
    public void onBind(int position, EMMessage message) {
        mPosition = position;
        mMessage = message;
        // 装在通用部分控件
        setupCommonView();
    }
}
