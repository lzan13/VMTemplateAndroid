package com.vmloft.develop.library.im.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseFragment;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.conversation.IMConversationManager;
import com.vmloft.develop.library.tools.adapter.VMAdapter;
import com.vmloft.develop.library.tools.base.VMConstant;
import com.vmloft.develop.library.tools.picker.VMPicker;
import com.vmloft.develop.library.tools.picker.bean.VMPictureBean;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.utils.VMSystem;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/05/09 10:11
 *
 * IM 可自定义加载的聊天界面
 */
public class IMChatFragment extends IMBaseFragment {

    // 输入容器
    private View mInputContainer;
    // 输入框
    private EditText mInputET;
    // 表情按钮
    private ImageButton mEmojiBtn;
    // 发送按钮
    private ImageButton mSendBtn;
    private ImageButton mPictureBtn;
    private ImageButton mCameraBtn;
    private ImageButton mCallBtn;
    private ImageButton mMoreBtn;

    // 输入扩展容器
    private View mInputExtContainer;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private IMChatAdapter mAdapter;

    private String mId;
    private int mChatType = IMConstants.ChatType.IM_SINGLE_CHAT;
    private int mPageSize = IMConstants.IM_CHAT_MSG_LIMIT;

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
        mPictureBtn = getView().findViewById(R.id.im_chat_input_picture_btn);
        mCameraBtn = getView().findViewById(R.id.im_chat_input_camera_btn);
        mCallBtn = getView().findViewById(R.id.im_chat_input_call_btn);
        mMoreBtn = getView().findViewById(R.id.im_chat_input_more_btn);

        mEmojiBtn.setOnClickListener(viewListener);
        mSendBtn.setOnClickListener(viewListener);
        mPictureBtn.setOnClickListener(viewListener);
        mCameraBtn.setOnClickListener(viewListener);
        mCallBtn.setOnClickListener(viewListener);
        mMoreBtn.setOnClickListener(viewListener);

        initRecyclerView();

        initInputWatcher();
    }

    /**
     * 初始化会话
     */
    private void initConversation() {
        // 清空未读
        IMConversationManager.getInstance().clearUnreadCount(mId, mChatType);

        int cacheCount = IMConversationManager.getInstance().getCacheMessages(mId, mChatType).size();
        int sumCount = IMConversationManager.getInstance().getMessagesCount(mId, mChatType);
        if (cacheCount > 0 && cacheCount < sumCount && cacheCount < mPageSize) {
            // 获取已经在列表中的最上边的一条消息id
            String msgId = IMConversationManager.getInstance().getCacheMessages(mId, mChatType).get(0).getMsgId();
            // 加载更多消息，填充满一页
            IMConversationManager.getInstance().loadMoreMessages(mId, mChatType, msgId);
        }

        // 检查是否有草稿没有发出
        String draft = IMConversationManager.getInstance().getDraft(mId, mChatType);
        if (!VMStr.isEmpty(draft)) {
            mInputET.setText(draft);
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
     * 设置界面控件点击监听
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.im_chat_recycler_view) {
            } else if (v.getId() == R.id.im_chat_input_emoji_btn) {

            } else if (v.getId() == R.id.im_chat_input_send_btn) {
                sendText();
            } else if (v.getId() == R.id.im_chat_input_picture_btn) {
                startAlbum();
            } else if (v.getId() == R.id.im_chat_input_camera_btn) {
            } else if (v.getId() == R.id.im_chat_input_call_btn) {
            } else if (v.getId() == R.id.im_chat_input_more_btn) {

            }
        }
    };

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
        IMChatManager.getInstance().sendText(text, mId, new IMCallback<EMMessage>() {
            @Override
            public void onSuccess(EMMessage message) {
                refresh(message);
            }

            @Override
            public void onError(int code, String desc) {
            }
        });
    }

    /**
     * 打开相册，选择图片
     */
    private void startAlbum() {
        VMPicker.getInstance().setShowCamera(true).startPicker(this);
    }

    /**
     * 发送图片消息
     *
     * @param path 图片地址
     */
    private void sendPicture(String path) {
        IMChatManager.getInstance().sendPicture(path, mId, new IMCallback<EMMessage>() {
            @Override
            public void onSuccess(EMMessage message) {

            }

            @Override
            public void onError(int code, String desc) {

            }

            @Override
            public void onProgress(int progress, String desc) {

            }
        });
    }

    /**
     * 刷新
     *
     * @param message
     */
    private void refresh(final EMMessage message) {
        VMSystem.runInUIThread(new Runnable() {
            @Override
            public void run() {
                int position = IMConversationManager.getInstance().getPosition(message);
                if (position >= 0) {
                    mAdapter.updateInsert(position);
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }
        });
    }

    /**
     * 加载更多消息
     *
     * @param msgId 从这一条消息 id 开始加载
     */
    private void loadMore(String msgId) {
        List<EMMessage> list = IMConversationManager.getInstance().loadMoreMessages(mId, mChatType, msgId);
        if (list.size() > 0) {
            mAdapter.updateInsert(0, list.size());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == VMConstant.VM_PICK_RESULT_CODE_PICTURES) {
            if (data != null && requestCode == VMConstant.VM_PICK_REQUEST_CODE) {
                ArrayList<VMPictureBean> pictures = VMPicker.getInstance().getSelectedPictures();
                sendPicture(pictures.get(0).path);
            }
        }
    }
}
