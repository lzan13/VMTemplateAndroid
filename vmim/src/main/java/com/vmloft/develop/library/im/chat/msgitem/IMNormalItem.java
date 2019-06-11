package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.chat.IMChatManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.utils.IMDialog;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.widget.VMFloatMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create by lzan13 on 2019/6/2 22:38
 *
 * 普通消息基类
 */
public abstract class IMNormalItem extends IMBaseItem {

    // 头像
    protected ImageView mAvatarView;
    // 消息状态
    protected View mStatusView;
    // 失败图标
    protected ImageView mErrorView;
    // 进度圈
    protected ProgressBar mSendPB;

    protected static final int ID_COPY = 0;
    protected static final int ID_VOICE = 1;
    protected static final int ID_FORWARD = 3;
    protected static final int ID_REMOVE = 10;

    protected VMFloatMenu mFloatMenu;
    protected List<VMFloatMenu.ItemBean> mFloatMenuList = new ArrayList<>();

    // 长按坐标
    protected int touchX;
    protected int touchY;

    public IMNormalItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    /**
     * 初始化部分
     */
    @Override
    protected void init() {
        super.init();

        mAvatarView = findViewById(R.id.im_msg_avatar_iv);
        mStatusView = findViewById(R.id.im_msg_status_view);
        mErrorView = findViewById(R.id.im_msg_error_iv);
        mSendPB = findViewById(R.id.im_msg_send_pb);

        mContainerView.setOnTouchListener((View v, MotionEvent event) -> {
            touchX = (int) event.getRawX();
            touchY = (int) event.getRawY();
            return false;
        });
    }

    /**
     * 加载容器，这里默认根据子类的判断加载发送或者接收的消息容器，子类可以重写此方法实现加载不同的容器
     */
    protected void loadContainer() {
        mInflater.inflate(isReceiveMessage() ? R.layout.im_msg_item_container_receive : R.layout.im_msg_item_container_send, this);
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
        // 处理头像
        if (mAvatarView != null) {
            loadContactInfo();
        }
        onUpdate(mMessage);
    }

    /**
     * 加载联系人信息
     */
    private void loadContactInfo() {
        mAvatarView.setOnClickListener((View v) -> {
            IM.getInstance().onHeadClick(mContext, mContact);
        });
        IPictureLoader.Options options = new IPictureLoader.Options(mContact.mAvatar);
        if (IM.getInstance().isCircleAvatar()) {
            options.isCircle = true;
        } else {
            options.isRadius = true;
            options.radiusSize = VMDimen.dp2px(4);
        }
        IM.getInstance().getPictureLoader().load(mContext, options, mAvatarView);
    }

    /**
     * 更新消息，这里主要是更新发送消息的状态
     */
    @Override
    public void onUpdate(EMMessage message) {
        if (!message.equals(mMessage)) {
            return;
        }
        // 处理发送结果
        if (mMessage.direct() == EMMessage.Direct.SEND && mErrorView != null && mSendPB != null) {
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
                break;
            }
        }
        checkACKStatus();
    }

    /**
     * 检查消息已读 ACK 状态
     */
    private void checkACKStatus() {
        if (mStatusView != null) {
            mStatusView.setVisibility(GONE);
            if (isReceiveMessage()) {
                if (!mMessage.isAcked()) {
                    // 接收方发送已读 ACK
                    IMChatManager.getInstance().sendReadACK(mMessage);
                }
            } else {
                // 发送方处理已读 ACK 状态
                if (mMessage.isAcked()) {
                    mStatusView.setVisibility(VISIBLE);
                    mStatusView.setSelected(true);
                } else if (mMessage.isDelivered()) {
                    mStatusView.setSelected(false);
                    mStatusView.setVisibility(VISIBLE);
                } else {
                    mStatusView.setVisibility(GONE);
                }
            }
        }
    }

    /**
     * 触发消息长按事件
     */
    public void onLongClick() {
        if (mFloatMenu == null) {
            mFloatMenu = new VMFloatMenu(getContext());
        }

        loadFloatMenu();

        // 排序
        Collections.sort(mFloatMenuList, (VMFloatMenu.ItemBean bean1, VMFloatMenu.ItemBean bean2) -> {
            if (bean1.itemId > bean2.itemId) {
                return 1;
            } else if (bean1.itemId < bean2.itemId) {
                return -1;
            }
            return 0;
        });

        mFloatMenu.clearAllItem();
        mFloatMenu.addItemList(mFloatMenuList);
        mFloatMenu.setItemClickListener((int id) -> {
            if (id == ID_FORWARD) {
                forwardMessage();
            } else if (id == ID_REMOVE) {
                removeMessage();
            } else {
                onFloatClick(id);
            }
        });
        mFloatMenu.showAtLocation(mContainerView, touchX, touchY);
    }

    /**
     * 加载悬浮菜单
     */
    public void loadFloatMenu() {
        mFloatMenuList.clear();

        //mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_FORWARD, VMStr.byRes(R.string.im_msg_forward)));
        mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_REMOVE, VMStr.byRes(R.string.im_msg_remove)));
    }

    /**
     * TODO 转发消息
     */
    private void forwardMessage() {
    }

    /**
     * 删除消息
     */
    private void removeMessage() {
        String title = VMStr.byRes(R.string.im_remove_hint_title);
        String content = VMStr.byRes(R.string.im_hint_content);
        String cancel = VMStr.byRes(R.string.im_cancel);
        String ok = VMStr.byRes(R.string.im_ok);
        IMDialog.showAlertDialog(mContext, title, content, cancel, ok, (DialogInterface dialog, int which) -> {
            IMChatManager.getInstance().removeMessage(mMessage);
            mAdapter.updateRemove(mPosition);
        });
    }

    /**
     * 悬浮菜单点击事件
     */
    protected void onFloatClick(int id) {

    }
}
