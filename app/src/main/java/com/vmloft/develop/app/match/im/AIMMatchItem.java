package com.vmloft.develop.app.match.im;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.chat.msgitem.IMCardItem;
import com.vmloft.develop.library.im.common.IMConstants;

/**
 * Create by lzan13 on 2019/6/10 21:21
 */
public class AIMMatchItem extends IMCardItem {

    public AIMMatchItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    protected boolean isReceiveMessage() {
        return mType == IMConstants.MsgType.IM_TEXT_RECEIVE;
    }

    @Override
    protected View layoutView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.im_msg_item_text, null);
        return view;
    }

    @Override
    public void onBind(int position, EMMessage message) {
        super.onBind(position, message);
    }
}
