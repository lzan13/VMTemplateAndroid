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
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMSystem;

/**
 * Create by lzan13 on 2019/5/23 20:08
 *
 * IM 消息基类，处理一些公共属性
 */
public abstract class IMMsgItem extends RelativeLayout {

    protected Context mContext;
    protected IMChatAdapter mAdapter;
    protected int mType;
    protected LayoutInflater mInflater;

    protected IMContact mContact;

    protected EMMessage mMessage;
    protected int mAvatarSize;
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
        mContext = context;
        mAdapter = adapter;
        mType = type;
        mInflater = LayoutInflater.from(context);

        init();
    }

    /**
     * 初始化部分
     */
    protected void init() {
        loadContainer();

        mAvatarSize = VMDimen.dp2px(48);

        mTimeView = findViewById(R.id.im_chat_msg_time_tv);
        mContainerView = findViewById(R.id.im_chat_msg_container_rl);
        mAvatarView = findViewById(R.id.im_chat_msg_avatar_iv);
        mStatusView = findViewById(R.id.im_chat_msg_status_view);
        mErrorView = findViewById(R.id.im_chat_msg_error_iv);
        mSendPB = findViewById(R.id.im_chat_msg_send_pb);

        mContainerView.addView(layoutView());
    }

    /**
     * 加载公共部分 UI
     */
    protected void setupCommonView() {
        // 处理时间戳
        if (mTimeView != null) {
            mTimeView.setText(VMDate.getRelativeTime(mMessage.getMsgTime()));
        }
        // 处理头像
        if (mAvatarView != null) {
            mAvatarView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    IM.getInstance().onHeadClick(mContext, mContact);
                }
            });
            IM.getInstance().getIMContact(mMessage.conversationId(), new IMCallback<IMContact>() {
                @Override
                public void onSuccess(IMContact contact) {
                    loadContactInfo(contact);
                }
            });
        }
        onUpdate(mMessage);
    }

    /**
     * 加载联系人信息
     */
    private void loadContactInfo(final IMContact contact) {
        mContact = contact;
        VMSystem.runInUIThread(new Runnable() {
            @Override
            public void run() {
                IM.getInstance().getPictureLoader().loadAvatar(mContext, contact.mAvatar, mAvatarView, mAvatarSize, mAvatarSize);
            }
        });
    }

    /**
     * 更新消息，这里主要是更新发送消息的状态
     */
    public void onUpdate(EMMessage message) {
        // 处理发送结果
        if (mMessage.direct() == EMMessage.Direct.SEND && mStatusView != null && mErrorView != null && mSendPB != null) {
            mStatusView.setVisibility(GONE);
            mErrorView.setVisibility(GONE);
            mSendPB.setVisibility(GONE);
            switch (mMessage.status()) {
                case CREATE:
                    break;
                case INPROGRESS:
                    mSendPB.setVisibility(VISIBLE);
                    break;
                case FAIL:
                    mErrorView.setVisibility(VISIBLE);
                    break;
                case SUCCESS:
                    if (mMessage.isAcked()) {
                        mStatusView.setVisibility(VISIBLE);
                        mStatusView.setSelected(true);
                    } else if (mMessage.isDelivered()) {
                        mStatusView.setSelected(false);
                        mStatusView.setVisibility(VISIBLE);
                    } else {
                        mStatusView.setVisibility(GONE);
                    }
                    break;
            }
        }
    }

    /**
     * 加载容器，这里默认根据子类的判断加载发送或者接收的消息容器，子类可以重写此方法实现加载不同的容器
     */
    protected void loadContainer() {
        mInflater.inflate(isReceive() ? R.layout.im_chat_item_receive_container : R.layout.im_chat_item_send_container, this);
    }

    /**
     * 是否为接收的，以此来判断加载哪种容器
     */
    protected abstract boolean isReceive();

    /**
     * 加载内容布局，解析对应布局文件，填充当前布局
     */
    protected abstract View layoutView();

    /**
     * 绑定数据
     *
     * @param message 需要展示的消息对象
     */
    public abstract void onBind(EMMessage message);

}
