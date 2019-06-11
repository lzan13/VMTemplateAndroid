package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;

import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.utils.VMTheme;

/**
 * Create by lzan13 on 2019/6/10 20:49
 *
 * 卡片类消息基类
 */
public abstract class IMCardItem extends IMBaseItem {

    public IMCardItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    protected void loadContainer() {
        mInflater.inflate(R.layout.im_msg_item_container_card, this);
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
    public void onBind(int position, EMMessage message) {
        super.onBind(position, message);

        VMTheme.changeShadow(mContainerView);

    }
}
