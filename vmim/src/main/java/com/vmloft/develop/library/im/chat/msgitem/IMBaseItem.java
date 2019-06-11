package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.chat.IMChatAdapter;

/**
 * Create by lzan13 on 2019/5/23 20:08
 *
 * IM 消息基类，处理一些公共属性
 */
public abstract class IMBaseItem extends RelativeLayout {

    protected Context mContext;
    protected IMChatAdapter mAdapter;
    protected int mType;
    protected LayoutInflater mInflater;

    protected IMContact mContact;

    // 消息在内存中的位置
    protected int mPosition;
    protected EMMessage mMessage;
    // 显示时间
    protected TextView mTimeView;
    // 消息容器
    protected ViewGroup mContainerView;

    /**
     * @param context
     * @param adapter
     */
    public IMBaseItem(Context context, IMChatAdapter adapter, int type) {
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

        mTimeView = findViewById(R.id.im_msg_time_tv);
        mContainerView = findViewById(R.id.im_msg_container_rl);

        mContainerView.addView(layoutView());

        mContainerView.setOnClickListener((View v) -> {onClick();});
        mContainerView.setOnLongClickListener((View v) -> {
            onLongClick();
            return false;
        });
    }

    /**
     * 加载容器
     */
    protected abstract void loadContainer();

    /**
     * 是否为接收的，以此来判断加载哪种容器
     */
    protected abstract boolean isReceiveMessage();

    /**
     * 加载公共部分 UI
     */
    protected abstract void setupCommonView();

    /**
     * 加载内容布局，解析对应布局文件，填充当前布局
     */
    protected abstract View layoutView();

    /**
     * 绑定数据
     *
     * @param message 需要展示的消息对象
     */
    public void onBind(int position, EMMessage message) {
        mPosition = position;
        mMessage = message;
        mContact = IM.getInstance().getIMContact(mMessage.conversationId());
        // 装在通用部分控件
        setupCommonView();
    }

    /**
     * 更新消息，这里主要是更新发送消息的状态
     */
    public void onUpdate(EMMessage message) {}

    /**
     * 触发消息点击
     */
    public void onClick() {}

    /**
     * 触发消息长按事件
     */
    public void onLongClick() {}
}
