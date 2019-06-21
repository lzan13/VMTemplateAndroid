package com.vmloft.develop.library.im.conversation;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.chat.IMChatManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.router.IMRouter;
import com.vmloft.develop.library.im.utils.IMChatUtils;
import com.vmloft.develop.library.im.utils.IMDialog;
import com.vmloft.develop.library.im.widget.IMEmotionTextView;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.utils.VMColor;
import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.widget.VMFloatMenu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/27 21:42
 *
 * 会话列表项
 */
public class IMConversationItem extends RelativeLayout {

    protected static final int ID_READ = 0;
    protected static final int ID_UNREAD = 1;
    protected static final int ID_TOP = 2;
    protected static final int ID_UNTOP = 3;
    protected static final int ID_REMOVE = 4;
    protected static final int ID_CLEAR = 5;

    protected Context mContext;
    protected IMConversationAdapter mAdapter;
    protected EMConversation mConversation;
    protected IMContact mContact;

    protected View mRootView;
    protected ImageView mAvatarView;
    protected TextView mRedDotView;
    protected TextView mTimeView;
    protected TextView mTitleView;
    protected IMEmotionTextView mContentView;

    // 长按坐标
    protected int touchX;
    protected int touchY;
    protected VMFloatMenu mFloatMenu;
    protected List<VMFloatMenu.ItemBean> mFloatMenuList = new ArrayList<>();

    public IMConversationItem(Context context, IMConversationAdapter adapter) {
        super(context);
        mContext = context;
        mAdapter = adapter;

        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.im_conversation_list_item, this);
        setClickable(true);

        mRootView = findViewById(R.id.im_conversation_root_cl);
        mAvatarView = findViewById(R.id.im_conversation_avatar_iv);
        mRedDotView = findViewById(R.id.im_conversation_red_dot_tv);
        mTimeView = findViewById(R.id.im_conversation_time_tv);
        mTitleView = findViewById(R.id.im_conversation_title_tv);
        mContentView = findViewById(R.id.im_conversation_content_etv);

        mRootView.setOnTouchListener((View v, MotionEvent event) -> {
            touchX = (int) event.getRawX();
            touchY = (int) event.getRawY();
            return false;
        });
        mRootView.setOnClickListener((View v) -> {
            IMRouter.goIMChat(mContext, mConversation.conversationId());
        });
        mRootView.setOnLongClickListener((View v) -> {
            itemLongClick();
            return true;
        });
    }

    /**
     * 绑定会话数据
     *
     * @param conversation 当前会话
     */
    public void onBind(EMConversation conversation) {
        mConversation = conversation;
        mContact = IM.getInstance().getIMContact(conversation.conversationId());

        /**
         * 设置当前会话的最后时间 获取当前会话最后时间，并转为 String 类型
         * 之前是获取最后一条消息的时间 conversation.getLastMessage().getMsgTime();
         * 这里改为通过给 EMConversation 对象添加了一个时间扩展，这样可以避免在会话没有消息时，无法显示时间的问题
         * 调用{@link IMConversationUtils#getConversationLastTime(EMConversation)}获取扩展里的时间
         */
        long timestamp = IMChatManager.getInstance().getTime(conversation);

        // 设置时间
        mTimeView.setText(VMDate.getRelativeTime(timestamp));

        /**
         * 根据当前 conversation 判断会话列表项要显示的内容
         * 判断的项目有两项：
         *  当前会话在本地是否有聊天记录，
         *  当前会话是否有草稿，
         */
        String content = "";
        String prefix = "";
        String draft = IMChatManager.getInstance().getDraft(mConversation);
        if (!VMStr.isEmpty(draft)) {
            // 表示草稿的前缀，TODO 草稿也要识别表情
            mContentView.setEnableEmotion(true);
            prefix = "[" + VMStr.byRes(R.string.im_draft) + "]";
            content = prefix + draft;
        } else if (conversation.getAllMessages().size() > 0) {
            EMMessage message = conversation.getLastMessage();
            int type = IMChatUtils.getMessageType(message);

            content = IMChatUtils.getSummary(message);

            // 只有文本才需要开启表情识别，默认都关闭
            mContentView.setEnableEmotion(false);
            if (type == IMConstants.MsgType.IM_TEXT_RECEIVE || type == IMConstants.MsgType.IM_TEXT_SEND) {
                mContentView.setEnableEmotion(true);
            }
            // 判断这条消息状态，如果失败加上失败前缀提示
            if (conversation.getLastMessage().status() == EMMessage.Status.FAIL) {
                prefix = "[失败]";
                content = prefix + content;
            }
        } else {
            // 当前会话没有聊天信息则设置显示内容为 空
            content = VMStr.byRes(R.string.im_empty);
        }
        // 根据不同的类型展示不同样式的消息
        if (!VMStr.isEmpty(draft)) {
            Spannable spannable = new SpannableString(content);
            spannable.setSpan(new ForegroundColorSpan(VMColor.byRes(R.color.vm_red_87)), 0, prefix.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mContentView.setText(spannable);
        } else if (conversation.getAllMsgCount() > 0 && conversation.getLastMessage().status() == EMMessage.Status.FAIL) {
            Spannable spannable = new SpannableString(content);
            spannable.setSpan(new ForegroundColorSpan(VMColor.byRes(R.color.vm_red_87)), 0, prefix.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mContentView.setText(spannable);
        } else {
            mContentView.setText(content);
        }

        // 设置当前会话未读数
        int unreadCount = conversation.getUnreadMsgCount();
        VMLog.i("conversation unread count %d", unreadCount);
        if (unreadCount == 0 && !IMChatManager.getInstance().isUnread(conversation)) {
            mRedDotView.setVisibility(GONE);

            mTitleView.setTypeface(Typeface.DEFAULT);
            mContentView.setTextColor(VMColor.byRes(R.color.vm_black_54));
        } else {
            mRedDotView.setVisibility(VISIBLE);
            mRedDotView.setText(String.valueOf(unreadCount == 0 ? 1 : unreadCount));

            mTitleView.setTypeface(Typeface.DEFAULT_BOLD);
            mContentView.setTextColor(VMColor.byRes(R.color.vm_black_87));
        }
        /**
         * 判断当前会话是否置顶
         */
        if (IMChatManager.getInstance().isTop(conversation)) {
            setSelected(true);
        } else {
            setSelected(false);
        }

        loadContactInfo();
        mAvatarView.setOnClickListener((View view) -> {
            IM.getInstance().onHeadClick(mContext, mContact);
        });
    }

    /**
     * 加载联系人信息
     */
    private void loadContactInfo() {
        if (VMStr.isEmpty(mContact.mNickname)) {
            mTitleView.setText(mContact.mUsername);
        } else {
            mTitleView.setText(mContact.mNickname);
        }
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
     * 加载悬浮菜单
     */
    public void loadFloatMenu() {
        mFloatMenuList.clear();
        // 未读操作
        if (mConversation.getUnreadMsgCount() == 0 && !IMChatManager.getInstance().isUnread(mConversation)) {
            mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_UNREAD, VMStr.byRes(R.string.im_conversation_unread)));
        } else {
            mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_READ, VMStr.byRes(R.string.im_conversation_read)));
        }
        // 置顶操作
        if (IMChatManager.getInstance().isTop(mConversation)) {
            mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_UNTOP, VMStr.byRes(R.string.im_conversation_untop)));
        } else {
            mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_TOP, VMStr.byRes(R.string.im_conversation_top)));
        }

        mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_REMOVE, VMStr.byRes(R.string.im_conversation_remove)));
        mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_CLEAR, VMStr.byRes(R.string.im_conversation_clear)));
    }

    /**
     * 触发长按事件
     */
    public void itemLongClick() {
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
            if (id == ID_READ) {
                setConversationUnread(false);
            } else if (id == ID_UNREAD) {
                setConversationUnread(true);
            } else if (id == ID_TOP) {
                setConversationTop(true);
            } else if (id == ID_UNTOP) {
                setConversationTop(false);
            } else if (id == ID_REMOVE) {
                removeConversation();
            } else if (id == ID_CLEAR) {
                clearConversation();
            }
        });
        mFloatMenu.showAtLocation(mRootView, touchX, touchY);
    }

    /**
     * 设置未读
     */
    private void setConversationUnread(boolean unread) {
        IMChatManager.getInstance().setUnread(mConversation, unread);
        List<EMConversation> list = IMChatManager.getInstance().getAllConversation();
        mAdapter.refresh(list);
    }

    /**
     * 设置置顶
     */
    private void setConversationTop(boolean top) {
        IMChatManager.getInstance().setTop(mConversation, top);
        List<EMConversation> list = IMChatManager.getInstance().getAllConversation();
        mAdapter.refresh(list);
    }

    /**
     * 移除会话，这里不会清空聊天记录
     */
    private void removeConversation() {
        String title = VMStr.byRes(R.string.im_remove_hint_title);
        String content = VMStr.byRes(R.string.im_hint_content);
        String cancel = VMStr.byRes(R.string.im_cancel);
        String ok = VMStr.byRes(R.string.im_ok);
        IMDialog.showAlertDialog(mContext, title, content, cancel, ok, (DialogInterface dialog, int which) -> {
            IMChatManager.getInstance().removeConversation(mConversation.conversationId());
            mAdapter.notifyDataSetChanged();
            List<EMConversation> list = IMChatManager.getInstance().getAllConversation();
            mAdapter.refresh(list);
        });
    }

    /**
     * 清空会话，这里清空聊天记录
     */
    private void clearConversation() {
        String title = VMStr.byRes(R.string.im_clear_hint_title);
        String content = VMStr.byRes(R.string.im_hint_content);
        String cancel = VMStr.byRes(R.string.im_cancel);
        String ok = VMStr.byRes(R.string.im_ok);
        IMDialog.showAlertDialog(mContext, title, content, cancel, ok, (DialogInterface dialog, int which) -> {
            IMChatManager.getInstance().clearConversation(mConversation.conversationId(), true);
            List<EMConversation> list = IMChatManager.getInstance().getAllConversation();
            mAdapter.refresh(list);
        });
    }
}
