package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.utils.VMDate;

/**
 * Create by lzan13 on 2019/5/23 22:17
 *
 * 无方向类型通知类消息基类
 */
public abstract class IMNotifyItem extends IMBaseItem {

    protected TextView mContentView;

    public IMNotifyItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    protected void loadContainer() {
        mInflater.inflate(R.layout.im_msg_item_container_notify, this);
    }

    @Override
    protected boolean isReceiveMessage() {
        return false;
    }

    /**
     * 加载公共部分 UI
     */
    @Override
    protected void setupCommonView() {
        // 处理时间戳
        if (mTimeView != null) {
            mTimeView.setVisibility(GONE);
            EMMessage prevMessage = mAdapter.getPrevMessage(mPosition);
            if (prevMessage == null || mMessage.localTime() - prevMessage.localTime() > IMConstants.IM_TIME_MINUTE * 2) {
                mTimeView.setText(VMDate.getRelativeTime(mMessage.getMsgTime()));
                mTimeView.setVisibility(VISIBLE);
            }
        }
        onUpdate(mMessage);
    }

    @Override
    protected View layoutView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.im_msg_item_notify_common, null);
        mContentView = view.findViewById(R.id.im_msg_content_tv);
        return view;
    }
}
