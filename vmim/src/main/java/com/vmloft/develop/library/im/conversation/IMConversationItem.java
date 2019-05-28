package com.vmloft.develop.library.im.conversation;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.common.IMChatManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.utils.IMChatUtils;
import com.vmloft.develop.library.tools.utils.VMColor;
import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.utils.VMSystem;

/**
 * Create by lzan13 on 2019/5/27 21:42
 *
 * 会话列表项
 */
public class IMConversationItem extends RelativeLayout {

    protected Context mContext;
    protected IMConversationAdapter mAdapter;
    protected EMConversation mConversation;
    protected IMContact mContact;

    protected ImageView mAvatarView;
    protected TextView mRedDotView;
    protected TextView mTimeView;
    protected TextView mTitleView;
    protected TextView mContentView;

    protected int mAvatarSize;

    public IMConversationItem(Context context, IMConversationAdapter adapter) {
        super(context);
        mContext = context;
        mAdapter = adapter;

        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.im_conversation_list_item, this);
        setClickable(true);

        mAvatarView = findViewById(R.id.im_conversation_avatar_iv);
        mRedDotView = findViewById(R.id.im_conversation_red_dot_tv);
        mTimeView = findViewById(R.id.im_conversation_time_tv);
        mTitleView = findViewById(R.id.im_conversation_title_tv);
        mContentView = findViewById(R.id.im_conversation_content_tv);

        mAvatarSize = VMDimen.dp2px(48);
    }

    /**
     * 绑定会话数据
     *
     * @param conversation 当前会话
     */
    public void onBind(EMConversation conversation) {
        mConversation = conversation;

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
            // 表示草稿的前缀
            prefix = "[" + VMStr.byRes(R.string.im_draft) + "]";
            content = prefix + draft;
        } else if (conversation.getAllMessages().size() > 0) {
            EMMessage message = conversation.getLastMessage();
            int type = IMChatUtils.getMessageType(message);
            if (type == IMConstants.IM_CHAT_TYPE_SYSTEM) {
            } else if (type == IMConstants.IM_CHAT_TYPE_RECALL) {
                content = "[" + VMStr.byRes(R.string.im_recall_already) + "]";
            } else if (type == IMConstants.IM_CHAT_TYPE_CALL_RECEIVE || type == IMConstants.IM_CHAT_TYPE_CALL_SEND) {
                content = "[" + VMStr.byRes(R.string.im_call) + "]";
            } else if (type == IMConstants.IM_CHAT_TYPE_TEXT_RECEIVE || type == IMConstants.IM_CHAT_TYPE_TEXT_SEND) {
                content = ((EMTextMessageBody) message.getBody()).getMessage();
            } else if (type == IMConstants.IM_CHAT_TYPE_IMAGE_RECEIVE || type == IMConstants.IM_CHAT_TYPE_IMAGE_SEND) {
                content = "[" + VMStr.byRes(R.string.im_picture) + "]";
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
            mRedDotView.setText(String.valueOf(conversation.getUnreadMsgCount()));

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
        mAvatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IM.getInstance().onHeadClick(mContext, mContact);
            }
        });
        IM.getInstance().getIMContact(conversation.conversationId(), new IMCallback<IMContact>() {
            @Override
            public void onSuccess(IMContact contact) {
                loadContactInfo(contact);
            }
        });
    }

    /**
     * 加载联系人信息
     */
    private void loadContactInfo(final IMContact contact) {
        if (contact == null) {
            return;
        }
        mContact = contact;
        VMSystem.runInUIThread(new Runnable() {
            @Override
            public void run() {
                if (VMStr.isEmpty(contact.mNickname)) {
                    mTitleView.setText(contact.mUsername);
                } else {
                    mTitleView.setText(contact.mNickname);
                }
                IM.getInstance().getPictureLoader().loadAvatar(mContext, contact.mAvatar, mAvatarView, mAvatarSize, mAvatarSize);
            }
        });
    }
}
