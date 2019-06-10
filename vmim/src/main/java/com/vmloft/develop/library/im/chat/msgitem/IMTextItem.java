package com.vmloft.develop.library.im.chat.msgitem;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.widget.IMEmotionTextView;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.widget.VMFloatMenu;

/**
 * Create by lzan13 on 2019/5/23 22:17
 *
 * 实现文本消息展示
 */
public class IMTextItem extends IMNormalItem {

    private IMEmotionTextView mContentView;

    public IMTextItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    protected boolean isReceiveMessage() {
        return mType == IMConstants.MsgType.IM_TEXT_RECEIVE;
    }

    @Override
    protected View layoutView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.im_msg_item_text, null);
        mContentView = view.findViewById(R.id.im_msg_content_etv);
        return view;
    }

    @Override
    public void onBind(int position, EMMessage message) {
        super.onBind(position, message);

        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        mContentView.setText(body.getMessage());
    }

    /**
     * 加载悬浮菜单
     */
    @Override
    public void loadFloatMenu() {
        super.loadFloatMenu();
        mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_COPY, VMStr.byRes(R.string.im_msg_copy)));
    }

    @Override
    protected void onFloatClick(int id) {
        if (id == ID_COPY) {
            // 获取剪切板管理者
            ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建剪切板数据对象
            ClipData clipData = ClipData.newPlainText("im_message", ((EMTextMessageBody) mMessage.getBody()).getMessage());
            // 将刚创建的数据对象添加到剪切板
            clipboardManager.setPrimaryClip(clipData);
            // 弹出提醒
            Toast.makeText(mContext, R.string.im_msg_copy_success, Toast.LENGTH_SHORT).show();
        }
    }
}
