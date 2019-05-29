package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.common.IMConstants;

/**
 * Create by lzan13 on 2019/5/23 22:17
 *
 * 实现文本消息展示
 */
public class IMUnknownMsgItem extends IMNotifyMsgItem {

    private TextView mContentView;

    public IMUnknownMsgItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    protected View layoutView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.im_chat_item_unknown, null);
        mContentView = view.findViewById(R.id.im_chat_msg_content_tv);
        return view;
    }

    @Override
    public void onBind(int position, EMMessage message) {
        mPosition = position;
        mMessage = message;
        // 装在通用部分控件
        setupCommonView();

        mContentView.setText(R.string.im_unknown_msg);
    }
}
