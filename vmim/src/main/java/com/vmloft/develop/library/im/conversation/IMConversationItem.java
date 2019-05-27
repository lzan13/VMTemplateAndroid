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
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.utils.IMConversationUtils;
import com.vmloft.develop.library.im.utils.IMMessageUtils;
import com.vmloft.develop.library.tools.utils.VMColor;
import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Create by lzan13 on 2019/5/27 21:42
 *
 * 会话列表项
 */
public class IMConversationItem extends RelativeLayout {

    protected Context mContext;
    protected IMConversationAdapter mAdapter;
    protected EMConversation mConversation;

    public ImageView avatarView;
    public TextView titleView;
    public TextView contentView;
    public TextView timeView;

    public IMConversationItem(Context context, IMConversationAdapter adapter) {
        super(context);
        mContext = context;
        mAdapter = adapter;

        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.im_conversation_list_item, this);

        avatarView = findViewById(R.id.im_conversation_avatar_iv);
        timeView = findViewById(R.id.im_conversation_time_tv);
        titleView = findViewById(R.id.im_conversation_time_tv);
        contentView = findViewById(R.id.im_conversation_content_tv);
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
        long timestamp = IMConversationUtils.getConversationLastTime(conversation);

        // 设置时间
        timeView.setText(VMDate.getRelativeTime(timestamp));

        /**
         * 根据当前 conversation 判断会话列表项要显示的内容
         * 判断的项目有两项：
         *  当前会话在本地是否有聊天记录，
         *  当前会话是否有草稿，
         */
        String content = "";
        String prefix = "";
        String draft = IMConversationUtils.getConversationDraft(conversation);
        if (!VMStr.isEmpty(draft)) {
            // 表示草稿的前缀
            prefix = "[" + VMStr.byRes(R.string.im_draft) + "]";
            content = prefix + draft;
        } else if (conversation.getAllMessages().size() > 0) {
            EMMessage message = conversation.getLastMessage();
            int type = IMMessageUtils.getMessageType(message);
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
            contentView.setText(spannable);
        } else if (conversation.getAllMsgCount() > 0 && conversation.getLastMessage().status() == EMMessage.Status.FAIL) {
            Spannable spannable = new SpannableString(content);
            spannable.setSpan(new ForegroundColorSpan(VMColor.byRes(R.color.vm_red_87)), 0, prefix.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            contentView.setText(spannable);
        } else {
            contentView.setText(content);
        }

        // 设置当前会话未读数
        int unreadCount = conversation.getUnreadMsgCount();
        VMLog.i("conversation unread count %d", unreadCount);
        if (unreadCount == 0 && !IMConversationUtils.getConversationUnread(conversation)) {
            titleView.setTypeface(Typeface.DEFAULT);
            contentView.setTypeface(Typeface.DEFAULT);
            contentView.setTextColor(VMColor.byRes(R.color.vm_black_54));
        } else {
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
            contentView.setTypeface(Typeface.DEFAULT_BOLD);
            contentView.setTextColor(VMColor.byRes(R.color.vm_black_87));
        }
        /**
         * 判断当前会话是否置顶
         * 调用工具类{@link IMConversationUtils#setConversationPushpin(EMConversation, boolean)}进行设置
         */
        if (IMConversationUtils.getConversationTop(conversation)) {
            //pushpinView.setVisibility(View.VISIBLE);
        } else {
            //pushpinView.setVisibility(View.GONE);
        }

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //callback.onAction(holder.avatarView.getId(), position);
            }
        });
        // 为每个Item设置点击与长按监听
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //callback.onAction(AConstants.ACTION_CLICK, position);
            }
        });
        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //callback.onAction(AConstants.ACTION_LONG_CLICK, position);
                return true;
            }
        });
    }
}
