package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.widget.IMEmojiTextView;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.widget.VMFloatMenu;

/**
 * Create by lzan13 on 2019/5/23 22:17
 *
 * 实现文本消息展示
 */
public class IMTextMsgItem extends IMNormalItem {

    private IMEmojiTextView mContentView;

    public IMTextMsgItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    protected boolean isReceiveMessage() {
        return mType == IMConstants.MsgType.IM_TEXT_RECEIVE;
    }

    @Override
    protected View layoutView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.im_chat_item_text, null);
        mContentView = view.findViewById(R.id.im_chat_msg_content_etv);
        return view;
    }

    @Override
    public void onBind(int position, EMMessage message) {
        mPosition = position;
        mMessage = message;
        // 装在通用部分控件
        setupCommonView();

        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        mContentView.setText(body.getMessage());
    }

    /**
     * 加载悬浮才难
     */
    @Override
    public void loadFloatMenu() {
        super.loadFloatMenu();
        mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_COPY, VMStr.byRes(R.string.im_msg_copy)));
    }

    @Override
    protected void onFloatClick(int id) {
        if (id == ID_COPY) {

        }
    }
}
