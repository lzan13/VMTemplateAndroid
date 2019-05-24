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
 * 未知类型消息展示
 */
public class IMUnknownMsgItem extends IMMsgItem {

    private TextView mContentView;

    public IMUnknownMsgItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    protected View onLayoutView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.im_chat_item_text, null);
        mContentView = view.findViewById(R.id.im_chat_msg_text_content_tv);
        return view;
    }

    @Override
    public void onBind(EMMessage message) {
        // 装在通用部分控件
        setupCommonView(message);

        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        mContentView.setText(body.getMessage());
    }
}
