package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;

/**
 * Create by lzan13 on 2019/5/23 20:08
 *
 * IM 消息基类，处理一些公共属性
 */
public abstract class IMMsgItem extends RelativeLayout {

    // 显示时间
    protected TextView mTimeView;
    // 消息容器
    protected ViewGroup mContainerView;
    // 头像
    protected ImageView mAvatarView;
    // 消息状态
    protected View mStatusView;
    // 失败图标
    protected ImageView mErrorView;
    // 进度圈
    protected ProgressBar mSendPB;

    /**
     * @param context
     * @param adapter
     */
    public IMMsgItem(Context context, IMChatAdapter adapter, int type) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.im_chat_item_send_container, this);

        mTimeView = findViewById(R.id.im_chat_msg_time_tv);
        mContainerView = findViewById(R.id.im_chat_msg_container_rl);
        mAvatarView = findViewById(R.id.im_chat_msg_avatar_iv);
        mStatusView = findViewById(R.id.im_chat_msg_status_view);
        mErrorView = findViewById(R.id.im_chat_msg_error_iv);
        mSendPB = findViewById(R.id.im_chat_msg_send_pb);

        mContainerView.addView(onLayoutView());
    }

    /**
     * 加载公共部分 UI
     */
    protected void setupCommonView(EMMessage message) {
        // 处理时间戳
        if (mTimeView != null) {

        }
        // 处理头像
        if (mAvatarView != null) {

        }
        // 处理状态
        if (mStatusView != null) {

        }
        // 处理发送结果
        if (mErrorView != null && mSendPB != null) {

        }

    }

    /**
     * 加载内容布局，解析对应布局文件，填充当前布局
     */
    protected abstract View onLayoutView();

    /**
     * 绑定数据
     *
     * @param message 需要展示的消息对象
     */
    public abstract void onBind(EMMessage message);
}
