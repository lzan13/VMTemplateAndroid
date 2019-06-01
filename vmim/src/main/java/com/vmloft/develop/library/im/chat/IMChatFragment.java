package com.vmloft.develop.library.im.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseFragment;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.emoji.IMEmojiGroup;
import com.vmloft.develop.library.im.emoji.IMEmojiItem;
import com.vmloft.develop.library.im.emoji.IMEmojiManager;
import com.vmloft.develop.library.im.emoji.IMEmojiPager;
import com.vmloft.develop.library.im.router.IMRouter;
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.tools.adapter.VMAdapter;
import com.vmloft.develop.library.tools.base.VMConstant;
import com.vmloft.develop.library.tools.picker.VMPicker;
import com.vmloft.develop.library.tools.picker.bean.VMPictureBean;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.utils.VMSystem;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

import java.util.List;

/**
 * Create by lzan13 on 2019/05/09 10:11
 *
 * IM 可自定义加载的聊天界面
 */
public class IMChatFragment extends IMBaseFragment {

    // 输入框
    private EditText mInputET;
    // 表情按钮
    private ImageButton mEmojiBtn;
    // 发送按钮
    private ImageButton mSendBtn;
    // 图片
    private ImageButton mPictureBtn;
    // 相机
    private ImageButton mCameraBtn;
    // 通话
    private ImageButton mCallBtn;
    // 语音
    private ImageButton mVoiceBtn;
    // 更多
    private ImageButton mMoreBtn;
    // 输入扩展容器
    private RelativeLayout mExtContainer;
    private RelativeLayout mExtEmojiContainer;
    private RelativeLayout mExtMoreContainer;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private IMChatAdapter mAdapter;

    private String mId;
    private int mPageSize = IMConstants.IM_CHAT_MSG_LIMIT;
    private int mChatType = IMConstants.ChatType.IM_SINGLE_CHAT;
    private EMConversation mConversation;

    /**
     * Fragment 的工厂方法，方便创建并设置参数
     */
    public static IMChatFragment newInstance(String id, int chatType) {
        IMChatFragment fragment = new IMChatFragment();

        Bundle args = new Bundle();
        args.putString(IMConstants.IM_CHAT_ID, id);
        args.putInt(IMConstants.IM_CHAT_TYPE, chatType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        initReceiver();

        // 检查是否有草稿没有发出
        String draft = IMChatManager.getInstance().getDraft(mConversation);
        if (!VMStr.isEmpty(draft)) {
            mInputET.setText(IMEmojiManager.getInstance().getEmojiSpannable(draft));
        }
    }

    /**
     * 加载布局
     */
    @Override
    protected int layoutId() {
        return R.layout.im_fragment_chat;
    }

    /**
     * 界面初始化
     */
    @Override
    protected void init() {
        mId = getArguments().getString(IMConstants.IM_CHAT_ID);
        mChatType = getArguments().getInt(IMConstants.IM_CHAT_TYPE);

        initConversation();

        mInputET = getView().findViewById(R.id.im_chat_input_et);
        mEmojiBtn = getView().findViewById(R.id.im_chat_input_emoji_btn);
        mSendBtn = getView().findViewById(R.id.im_chat_input_send_btn);
        mPictureBtn = getView().findViewById(R.id.im_chat_bottom_picture_btn);
        mCameraBtn = getView().findViewById(R.id.im_chat_bottom_camera_btn);
        mCallBtn = getView().findViewById(R.id.im_chat_bottom_call_btn);
        mVoiceBtn = getView().findViewById(R.id.im_chat_bottom_voice_btn);
        mMoreBtn = getView().findViewById(R.id.im_chat_bottom_more_btn);
        mExtContainer = getView().findViewById(R.id.im_chat_bottom_ext_rl);
        mExtEmojiContainer = getView().findViewById(R.id.im_chat_ext_emoji_rl);
        mExtMoreContainer = getView().findViewById(R.id.im_chat_ext_more_rl);

        mEmojiBtn.setOnClickListener(viewListener);
        mSendBtn.setOnClickListener(viewListener);
        mPictureBtn.setOnClickListener(viewListener);
        mCameraBtn.setOnClickListener(viewListener);
        mCallBtn.setOnClickListener(viewListener);
        mVoiceBtn.setOnClickListener(viewListener);
        mMoreBtn.setOnClickListener(viewListener);

        initRecyclerView();

        initEmojiView();

        initInputWatcher();
    }

    /**
     * 初始化会话
     */
    private void initConversation() {
        mConversation = IMChatManager.getInstance().getConversation(mId, mChatType);

        // 清空未读
        IMChatManager.getInstance().clearUnreadCount(mId, mChatType);

        int cacheCount = IMChatManager.getInstance().getCacheMessages(mId, mChatType).size();
        int sumCount = IMChatManager.getInstance().getMessagesCount(mId, mChatType);
        if (cacheCount > 0 && cacheCount < sumCount && cacheCount < mPageSize) {
            // 获取已经在列表中的最上边的一条消息id
            String msgId = IMChatManager.getInstance().getCacheMessages(mId, mChatType).get(0).getMsgId();
            // 加载更多消息，填充满一页
            IMChatManager.getInstance().loadMoreMessages(mId, mChatType, msgId);
        }
    }

    /**
     * 初始化消息列表
     */
    private void initRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.im_chat_recycler_view);
        mRecyclerView.setOnClickListener(viewListener);
        mAdapter = new IMChatAdapter(mContext, mId, mChatType);
        mLayoutManager = new LinearLayoutManager(mContext);
        // 是否固定在底部
        mLayoutManager.setStackFromEnd(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(new VMAdapter.IClickListener() {
            @Override
            public void onItemAction(int action, Object object) {

            }

            @Override
            public boolean onItemLongAction(int action, Object object) {
                return false;
            }
        });
        scrollToBottom();
    }

    /**
     * 初始化表情
     */
    private void initEmojiView() {
        IMEmojiPager emojiPager = new IMEmojiPager(mContext);
        mExtEmojiContainer.addView(emojiPager);
        emojiPager.stEmojiListener(new IMEmojiPager.IIMEmojiListener() {
            /**
             * 添加表情
             * @param group 表情所在组
             * @param item  表情
             */
            @Override
            public void onInsertEmoji(IMEmojiGroup group, IMEmojiItem item) {
                if (group.isEmoji) {
                    SpannableString spannableString = IMEmojiManager.getInstance().getEmojiSpannable(item.mEmojiResId, item.mEmojiDesc);
                    int currentIndex = mInputET.getSelectionStart();
                    Editable editable = mInputET.getText();
                    editable.insert(currentIndex, spannableString);
                } else {
                    // 不是 Emoji 表情，直接发送消息
                }
            }

            /**
             * 删除表情
             */
            @Override
            public void onDeleteEmoji() {
                int selection = mInputET.getSelectionStart();
                String text = mInputET.getText().toString();

                int start = mInputET.getSelectionStart();
                int end = mInputET.getSelectionEnd();
                if (start > 0) {
                    if (end - start > 0) {
                        mInputET.getText().delete(start, end);
                    } else {
                        int deleteLength = IMEmojiManager.getInstance().deleteEmoji(start, text);
                        if (deleteLength > 0) {
                            mInputET.getText().delete(selection - deleteLength, selection);
                        }
                    }
                }
            }
        });
        emojiPager.loadData();
    }

    /**
     * 设置输入框内容的监听
     */
    private void initInputWatcher() {
        mInputET.addTextChangedListener(new TextWatcher() {
            /**
             * 输入框内容改变之前
             * params s         输入框内容改变前的内容
             * params start     输入框内容开始变化的索引位置，从0开始计数
             * params count     输入框内容将要减少的变化的字符数 大于0 表示删除字符
             * params after     输入框内容将要增加的文本的长度，大于0 表示增加字符
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                VMLog.d("beforeTextChanged s-%s, start-%d, count-%d, after-%d", s, start, count, after);
            }

            /**
             * 输入框内容改变
             * params s         输入框内容改变后的内容
             * params start     输入框内容开始变化的索引位置，从0开始计数
             * params before    输入框内容减少的文本的长度
             * params count     输入框内容改变的字符数量
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                VMLog.d("onTextChanged s-%s, start-%d, before-%d, count-%d", s, start, before, count);
                sendInputStatus();
            }

            /**
             * 输入框内容改变之后
             * params s 输入框最终的内容
             */
            @Override
            public void afterTextChanged(Editable s) {
                VMLog.d("afterTextChanged s-" + s);
                if (s.toString().equals("")) {
                    mSendBtn.setVisibility(View.GONE);
                } else {
                    mSendBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 设置界面控件点击监听
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.im_chat_recycler_view) {
            } else if (v.getId() == R.id.im_chat_input_emoji_btn) {
                changeEmojiStatus();
            } else if (v.getId() == R.id.im_chat_input_send_btn) {
                sendText();
            } else if (v.getId() == R.id.im_chat_bottom_picture_btn) {
                startAlbum();
            } else if (v.getId() == R.id.im_chat_bottom_camera_btn) {

            } else if (v.getId() == R.id.im_chat_bottom_call_btn) {
                startCall();
            } else if (v.getId() == R.id.im_chat_bottom_voice_btn) {
                startVoice();
            } else if (v.getId() == R.id.im_chat_bottom_more_btn) {
                startMore();
            }
        }
    };

    /**
     * 改变表情状态，打开 关闭
     */
    private void changeEmojiStatus() {
        mEmojiBtn.setSelected(!mEmojiBtn.isSelected());
        if (mEmojiBtn.isSelected()) {
            mExtContainer.setVisibility(View.VISIBLE);
            mExtEmojiContainer.setVisibility(View.VISIBLE);
            mExtMoreContainer.setVisibility(View.GONE);
        } else {
            mExtContainer.setVisibility(View.GONE);
            mExtEmojiContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 打开相册，选择图片
     */
    private void startAlbum() {
        VMPicker.getInstance().setShowCamera(true).startPicker(this);
    }

    /**
     * 开始呼叫
     */
    private void startCall() {
        IMRouter.goIMCall(mContext, mId, false);
    }

    /**
     * 开始录音
     */
    private void startVoice() {

    }

    /**
     * 开始呼叫
     */
    private void startMore() {

    }

    /**
     * 发送文本消息
     */
    private void sendText() {
        String text = mInputET.getText().toString().trim();
        if (VMStr.isEmpty(text)) {
            VMToast.make((Activity) mContext, R.string.im_chat_send_notnull).error();
            return;
        }
        mInputET.setText("");
        EMMessage message = IMChatManager.getInstance().createTextMessage(text, mId, true);
        sendMessage(message);
    }

    /**
     * 发送图片消息
     *
     * @param path 图片地址
     */
    private void sendPicture(String path) {
        EMMessage message = IMChatManager.getInstance().createPictureMessage(path, mId, true);
        sendMessage(message);
    }

    /**
     * 发送输入状态消息
     */
    private void sendInputStatus() {
        // 当新增内容长度为1时采取判断增加的字符是否为@符号
        //                if (conversation.getType() == EMConversation.EMConversationType.Chat) {
        //                    if ((VMDate.currentMilli() - oldTime) > AConstants.TIME_INPUT_STATUS) {
        //                        oldTime = System.currentTimeMillis();
        //                        // 调用发送输入状态方法
        //                        MessageUtils.sendInputStatusMessage(chatId);
        //                    }
        //                }
    }

    /**
     * 发送消息统一处理方法
     *
     * @param message 要发送的消息
     */
    private void sendMessage(final EMMessage message) {
        IMChatManager.getInstance().getConversation(mId, mChatType).appendMessage(message);
        refreshInsert(message);
        // 更新会话时间
        IMChatManager.getInstance().setTime(mConversation, message.localTime());
        // 调用发送消息方法
        IMChatManager.getInstance().sendMessage(message, new IMCallback<EMMessage>() {
            @Override
            public void onSuccess(EMMessage message) {
                refreshChange(message);
            }

            @Override
            public void onError(int code, String desc) {
                refreshChange(message);
            }

            @Override
            public void onProgress(int progress, String desc) {
                refreshChange(message);
            }
        });
    }

    /**
     * 刷新插入新消息
     */
    private void refreshInsert(final EMMessage message) {
        VMSystem.runInUIThread(() -> {
            int position = IMChatManager.getInstance().getPosition(message);
            if (position >= 0) {
                mAdapter.updateInsert(position);
                scrollToBottom();
            }
        });
    }

    /**
     * 刷新更新消息
     */
    private void refreshChange(final EMMessage message) {
        VMSystem.runInUIThread(() -> {
            int position = IMChatManager.getInstance().getPosition(message);
            if (position >= 0) {
                mAdapter.updateChange(position);
            }
        });
    }

    /**
     * 滚动到底部
     */
    private void scrollToBottom() {
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    /**
     * 加载更多消息
     *
     * @param msgId 从这一条消息 id 开始加载
     */
    private void loadMore(String msgId) {
        List<EMMessage> list = IMChatManager.getInstance().loadMoreMessages(mId, mChatType, msgId);
        if (list.size() > 0) {
            mAdapter.updateInsert(0, list.size());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == VMConstant.VM_PICK_RESULT_CODE_PICTURES) {
            if (data != null && requestCode == VMConstant.VM_PICK_REQUEST_CODE) {
                List<VMPictureBean> pictures = VMPicker.getInstance().getResultData();
                sendPicture(pictures.get(0).path);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver();

        /**
         * 判断聊天输入框内容是否为空，不为空就保存输入框内容到{@link EMConversation}的扩展中
         * 调用{@link ConversationExtUtils#setConversationDraft(EMConversation, String)}方法
         */
        String draft = mInputET.getText().toString().trim();
        // 将输入框的内容保存为草稿
        IMChatManager.getInstance().setDraft(mConversation, draft);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * ------------------------------- 广播接收器部分 -------------------------------
     */

    /**
     * 初始化注册广播接收器
     */
    private void initReceiver() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
        // 新消息广播接收器
        IntentFilter newMessageFilter = new IntentFilter(IMUtils.Action.getNewMessageAction());
        lbm.registerReceiver(mNewMessageReceiver, newMessageFilter);

        IntentFilter updateMessageFilter = new IntentFilter(IMUtils.Action.getUpdateMessageAction());
        lbm.registerReceiver(mUpdateMessageReceiver, updateMessageFilter);
    }

    /**
     * 取消注册广播接收器
     */
    private void unregisterReceiver() {
        // 取消新消息广播接收器
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mNewMessageReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mUpdateMessageReceiver);
    }

    private NewMessageReceiver mNewMessageReceiver = new NewMessageReceiver();
    private UpdateMessageReceiver mUpdateMessageReceiver = new UpdateMessageReceiver();

    /**
     * 定义广播接收器
     */
    private class NewMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //这里处理接收的信息
            String id = intent.getStringExtra(IMConstants.IM_CHAT_ID);
            if (!VMStr.isEmpty(id) && id.equals(mId)) {
                EMMessage message = intent.getParcelableExtra(IMConstants.IM_CHAT_MSG);
                // 当前会话设置消息已读
                mConversation.markMessageAsRead(message.getMsgId());
                refreshInsert(message);
            }
        }
    }

    /**
     * 定义广播接收器
     */
    private class UpdateMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //这里处理接收的信息
            String id = intent.getStringExtra(IMConstants.IM_CHAT_ID);
            if (!VMStr.isEmpty(id) && id.equals(mId)) {
                EMMessage message = intent.getParcelableExtra(IMConstants.IM_CHAT_MSG);
                refreshChange(message);
            }
        }
    }
}
