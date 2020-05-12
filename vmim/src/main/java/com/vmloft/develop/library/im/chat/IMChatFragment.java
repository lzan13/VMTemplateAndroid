package com.vmloft.develop.library.im.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseFragment;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.call.IMCallManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.common.IMSPManager;
import com.vmloft.develop.library.im.emotion.IMEmotionGroup;
import com.vmloft.develop.library.im.emotion.IMEmotionItem;
import com.vmloft.develop.library.im.emotion.IMEmotionManager;
import com.vmloft.develop.library.im.emotion.IMEmotionPager;
import com.vmloft.develop.library.im.utils.IMChatUtils;
import com.vmloft.develop.library.im.utils.IMDialog;
import com.vmloft.develop.library.im.utils.IMKeyboardLayout;
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.tools.base.VMConstant;
import com.vmloft.develop.library.tools.picker.VMPicker;
import com.vmloft.develop.library.tools.picker.bean.VMPictureBean;
import com.vmloft.develop.library.tools.utils.VMColor;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.utils.VMSystem;
import com.vmloft.develop.library.tools.widget.VMEmojiRainView;
import com.vmloft.develop.library.tools.widget.record.VMRecordView;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/05/09 10:11
 *
 * IM 可自定义加载的聊天界面
 */
public class IMChatFragment extends IMBaseFragment {

    // 键盘监听布局
    private IMKeyboardLayout mKeyboardLayout;
    // 输入框
    private EditText mInputET;
    // 表情按钮
    private ImageView mEmotionBtn;
    // 发送按钮
    private ImageButton mSendBtn;
    // 图片
    private ImageButton mPictureBtn;
    // 相机
    private ImageButton mCameraBtn;
    // 通话
    private ImageButton mCallBtn;
    // 语音
    private ImageButton mRecordBtn;
    // 更多
    private ImageButton mMoreBtn;
    // 扩展容器
    private RelativeLayout mExtContainer;
    private RelativeLayout mExtEmotionContainer;
    private RelativeLayout mExtRecordContainer;
    private VMRecordView mExtRecordView;
    private RelativeLayout mExtMoreContainer;
    private VMEmojiRainView mEmojiRainView;

    // 键盘高度
    private int mKeyboardHeight;

    // 列表布局
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private IMChatAdapter mAdapter;

    // 会话相关
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        IM.getInstance().setCurrChatId(mId);
        // 检查是否有草稿没有发出
        String draft = IMChatManager.getInstance().getDraft(mConversation);
        if (!VMStr.isEmpty(draft)) {
            mInputET.setText(IMEmotionManager.getInstance().getEmotionSpannable(draft));
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
        mKeyboardHeight = IMSPManager.getInstance().getKeyboardHeight();

        mKeyboardLayout = getView().findViewById(R.id.im_chat_keyboard_layout);
        mInputET = getView().findViewById(R.id.im_chat_input_et);
        mEmotionBtn = getView().findViewById(R.id.im_chat_input_emotion_btn);
        mSendBtn = getView().findViewById(R.id.im_chat_input_send_btn);
        mPictureBtn = getView().findViewById(R.id.im_chat_bottom_picture_btn);
        mCameraBtn = getView().findViewById(R.id.im_chat_bottom_camera_btn);
        mCallBtn = getView().findViewById(R.id.im_chat_bottom_call_btn);
        mRecordBtn = getView().findViewById(R.id.im_chat_bottom_record_btn);
        mMoreBtn = getView().findViewById(R.id.im_chat_bottom_more_btn);
        mExtContainer = getView().findViewById(R.id.im_chat_bottom_ext_rl);
        mExtEmotionContainer = getView().findViewById(R.id.im_chat_ext_emotion_rl);
        mExtRecordContainer = getView().findViewById(R.id.im_chat_ext_record_rl);
        mExtRecordView = getView().findViewById(R.id.im_chat_ext_record_view);
        mExtMoreContainer = getView().findViewById(R.id.im_chat_ext_more_rl);
        mEmojiRainView = getView().findViewById(R.id.im_chat_emoji_rain_view);

        mEmojiRainView = getView().findViewById(R.id.im_chat_emoji_rain_view);

        mEmotionBtn.setOnClickListener(viewListener);
        mSendBtn.setOnClickListener(viewListener);
        mPictureBtn.setOnClickListener(viewListener);
        mCameraBtn.setOnClickListener(viewListener);
        mCallBtn.setOnClickListener(viewListener);
        mRecordBtn.setOnClickListener(viewListener);
        mMoreBtn.setOnClickListener(viewListener);

        initConversation();

        initRecyclerView();

        initInputWatcher();

        initKeyboardListener();

        initEmotionView();

        initRecordView();
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
        mRefreshLayout = getView().findViewById(R.id.im_chat_swipe);
        mRefreshLayout.setColorSchemeColors(VMColor.byRes(R.color.im_accent));
        mRefreshLayout.setOnRefreshListener(() -> {
            loadMore();
            mRefreshLayout.setRefreshing(false);
        });
        mRecyclerView = getView().findViewById(R.id.im_chat_recycler_view);
        mRecyclerView.setOnTouchListener((View v, MotionEvent event) -> {
            mKeyboardLayout.hideKeyboard(getActivity(), mInputET);
            mEmotionBtn.setSelected(false);
            mRecordBtn.setSelected(false);
            changeEmotion();
            changeRecord();
            return false;
        });
        mAdapter = new IMChatAdapter(mContext, mId, mChatType);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        scrollToBottom();
    }

    /**
     * 设置输入框内容的监听
     */
    private void initInputWatcher() {
        mInputET.addTextChangedListener(new TextWatcher() {
            /**
             * 输入框内容改变之前
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * 输入框内容改变
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendInputStatus();
            }

            /**
             * 输入框内容改变之后
             */
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    mSendBtn.setVisibility(View.GONE);
                } else {
                    mSendBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 初始化键盘监听
     */
    private void initKeyboardListener() {
        setupExtContainerParams();

        mInputET.setOnClickListener((View v) -> {
            mKeyboardLayout.postDelayed(() -> {
                // 输入法弹出之后，重新调整
                mEmotionBtn.setSelected(false);
                mRecordBtn.setSelected(false);
                mExtContainer.setVisibility(View.GONE);
                // 设置是否调整布局大小
                mKeyboardLayout.setResizeLayout(getActivity(), true);
            }, 220); // 延迟一段时间，等待输入法完全弹出
        });
        mKeyboardLayout.setKeyboardListener((boolean active, int height) -> {
            if (active) {
                // 输入法激活
                if (mKeyboardHeight != height) {
                    mKeyboardHeight = height;
                    setupExtContainerParams();
                }
                mEmotionBtn.setSelected(false);
                mRecordBtn.setSelected(false);
                changeEmotion();
                changeRecord();
            } else {
                // 输入法关闭，TODO 这里不需要主动操作什么
            }
        });
    }

    /**
     * 设置扩展容器参数
     */
    private void setupExtContainerParams() {
        ViewGroup.LayoutParams layoutParams = mExtContainer.getLayoutParams();
        layoutParams.height = mKeyboardHeight;
        mExtContainer.setLayoutParams(layoutParams);
    }

    /**
     * 初始化表情
     */
    private void initEmotionView() {
        IMEmotionPager emotionPager = new IMEmotionPager(mContext);
        mExtEmotionContainer.addView(emotionPager);
        emotionPager.stEmotionListener(new IMEmotionPager.IIMEmotionListener() {
            /**
             * 添加表情
             * @param group 表情所在组
             * @param item  表情
             */
            @Override
            public void onInsertEmotion(IMEmotionGroup group, IMEmotionItem item) {
                if (group.isInnerEmotion) {
                    if (group.isBigEmotion) {
                        // TODO 内部大需要特殊处理
                        sendBigEmotion(group, item);
                    } else {
                        SpannableString spannableString = IMEmotionManager.getInstance().getEmotionSpannable(item.mResId, item.mDesc);
                        int currentIndex = mInputET.getSelectionStart();
                        Editable editable = mInputET.getText();
                        editable.insert(currentIndex, spannableString);
                    }
                } else {
                    // TODO 不是内部表情，直接发送消息
                    sendBigEmotion(group, item);
                }
            }

            /**
             * 删除表情
             */
            @Override
            public void onDeleteEmotion() {
                int selection = mInputET.getSelectionStart();
                String text = mInputET.getText().toString();

                int start = mInputET.getSelectionStart();
                int end = mInputET.getSelectionEnd();
                if (start > 0) {
                    if (end - start > 0) {
                        mInputET.getText().delete(start, end);
                    } else {
                        int deleteLength = IMEmotionManager.getInstance().deleteEmotion(start, text);
                        if (deleteLength > 0) {
                            mInputET.getText().delete(selection - deleteLength, selection);
                        }
                    }
                }
            }
        });
        emotionPager.loadData();
    }

    /**
     * 初始化录音控件
     */
    private void initRecordView() {
        mExtRecordView.setRecordListener(new VMRecordView.RecordListener() {
            @Override
            public void onError(int code, String desc) {

            }

            @Override
            public void onComplete(String path, long time) {
                sendVoice(path, (int) time);
            }
        });
    }

    /**
     * 设置界面控件点击监听
     */
    private View.OnClickListener viewListener = (View v) -> {
        if (v.getId() == R.id.im_chat_recycler_view) {
        } else if (v.getId() == R.id.im_chat_input_emotion_btn) {
            mEmotionBtn.setSelected(!mEmotionBtn.isSelected());
            changeEmotion();
        } else if (v.getId() == R.id.im_chat_input_send_btn) {
            sendText();
        } else if (v.getId() == R.id.im_chat_bottom_picture_btn) {
            startAlbum();
        } else if (v.getId() == R.id.im_chat_bottom_camera_btn) {

        } else if (v.getId() == R.id.im_chat_bottom_call_btn) {
            startCall();
        } else if (v.getId() == R.id.im_chat_bottom_record_btn) {
            mRecordBtn.setSelected(!mRecordBtn.isSelected());
            changeRecord();
        } else if (v.getId() == R.id.im_chat_bottom_more_btn) {
            startMore();
        }
    };

    /**
     * 改变表情状态，打开 关闭
     */
    private void changeEmotion() {
        if (mKeyboardLayout.isActive()) {
            // 输入法打开状态下
            if (mEmotionBtn.isSelected()) {
                mKeyboardLayout.setResizeLayout(getActivity(), false);
                mKeyboardLayout.hideKeyboard(getActivity(), mInputET);
                // 改变其他扩展按钮状态
                mRecordBtn.setSelected(false);

                mExtContainer.setVisibility(View.VISIBLE);
                mExtEmotionContainer.setVisibility(View.VISIBLE);
                mExtRecordContainer.setVisibility(View.GONE);
                mExtMoreContainer.setVisibility(View.GONE);
            } else {
                mExtContainer.setVisibility(View.GONE);
                mKeyboardLayout.setResizeLayout(getActivity(), true);
            }
        } else {
            // 输入法关闭状态下
            if (mEmotionBtn.isSelected()) {
                // 设置为不会调整大小，以便输入弹起时布局不会改变。若不设置此属性，输入法弹起时布局会闪一下
                mKeyboardLayout.setResizeLayout(getActivity(), false);
                // 改变其他扩展按钮状态
                mRecordBtn.setSelected(false);

                mExtContainer.setVisibility(View.VISIBLE);
                mExtEmotionContainer.setVisibility(View.VISIBLE);
                mExtRecordContainer.setVisibility(View.GONE);
                mExtMoreContainer.setVisibility(View.GONE);
            } else {
                mExtContainer.setVisibility(View.GONE);
                mKeyboardLayout.setResizeLayout(getActivity(), true);
            }
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
        String[] menus = {
                VMStr.byRes(R.string.im_call_video), VMStr.byRes(R.string.im_call_voice)
        };
        IMDialog.showAlertDialog(mContext, menus, (DialogInterface dialog, int which) -> {
            if (which == 0) {
                IMCallManager.getInstance().startCall(mId, IMCallManager.CallType.VIDEO);
            } else {
                IMCallManager.getInstance().startCall(mId, IMCallManager.CallType.VOICE);
            }
        });
    }

    /**
     * 改变录音状态
     */
    private void changeRecord() {
        if (mKeyboardLayout.isActive()) {
            // 输入法打开状态下
            if (mRecordBtn.isSelected()) {
                mKeyboardLayout.setResizeLayout(getActivity(), false);
                mKeyboardLayout.hideKeyboard(getActivity(), mInputET);
                // 改变其他扩展按钮状态
                mEmotionBtn.setSelected(false);

                mExtContainer.setVisibility(View.VISIBLE);
                mExtEmotionContainer.setVisibility(View.GONE);
                mExtRecordContainer.setVisibility(View.VISIBLE);
                mExtMoreContainer.setVisibility(View.GONE);
            } else {
                mExtContainer.setVisibility(View.GONE);
                mKeyboardLayout.setResizeLayout(getActivity(), true);
            }
        } else {
            // 输入法关闭状态下
            if (mRecordBtn.isSelected()) {
                // 设置为不会调整大小，以便输入弹起时布局不会改变。若不设置此属性，输入法弹起时布局会闪一下
                mKeyboardLayout.setResizeLayout(getActivity(), false);
                // 改变其他扩展按钮状态
                mEmotionBtn.setSelected(false);

                mExtContainer.setVisibility(View.VISIBLE);
                mExtEmotionContainer.setVisibility(View.GONE);
                mExtRecordContainer.setVisibility(View.VISIBLE);
                mExtMoreContainer.setVisibility(View.GONE);
            } else {
                mExtContainer.setVisibility(View.GONE);
                mKeyboardLayout.setResizeLayout(getActivity(), true);
            }
        }
    }

    /**
     * 打开扩展
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
     * 发送语音消息
     *
     * @param path 语音文件路径
     * @param time 语音时长 毫秒值
     */
    private void sendVoice(String path, int time) {
        EMMessage message = IMChatManager.getInstance().createVoiceMessage(path, time, mId, true);
        sendMessage(message);
    }

    /**
     * 发送大表情
     *
     * @param group 表情分组
     * @param item  表情
     */
    private void sendBigEmotion(IMEmotionGroup group, IMEmotionItem item) {
        EMMessage message = IMChatManager.getInstance().createTextMessage(item.mDesc, mId, true);
        message.setAttribute(IMConstants.IM_MSG_EXT_TYPE, IMConstants.MsgExtType.IM_BIG_EMOTION);

        message.setAttribute(IMConstants.IM_MSG_EXT_EMOTION_GROUP, group.mName);
        message.setAttribute(IMConstants.IM_MSG_EXT_EMOTION_DESC, item.mDesc);
        if (!group.isInnerEmotion) {
            // TODO 下载的大表情，这两个主要是为了在本地没有文件时展示
            message.setAttribute(IMConstants.IM_MSG_EXT_EMOTION_URL, item.mUrl);
            message.setAttribute(IMConstants.IM_MSG_EXT_EMOTION_GIF_URL, item.mGifUrl);
        }
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
     * 检查表情雨
     */
    private void checkEmojiRain(EMMessage message) {
        if (IMChatUtils.getMessageType(message) != IMConstants.MsgType.IM_TEXT_RECEIVE && IMChatUtils.getMessageType(message) != IMConstants.MsgType.IM_TEXT_SEND) {
            return;
        }
        String content = IMChatUtils.getSummary(message);
        if (VMStr.isEmpty(content)) {
            return;
        }

        // 资源 id 方式
        List<Integer> resList = new ArrayList<>();
        if (content.contains(VMStr.byRes(R.string.im_emoji_rain_memeda))) {
            resList.add(R.drawable.im_emoji_rain_lip);
            resList.add(R.drawable.im_emoji_rain_kiss);
        } else if (content.contains(VMStr.byRes(R.string.im_emoji_rain_birthday))) {
            resList.add(R.drawable.im_emoji_rain_happy);
            resList.add(R.drawable.im_emoji_rain_birthday);
        } else if (content.contains(VMStr.byRes(R.string.im_emoji_rain_year))) {
            resList.add(R.drawable.im_emoji_rain_happy);
        } else if (content.contains(VMStr.byRes(R.string.im_emoji_rain_luck))) {
            resList.add(R.drawable.im_emoji_rain_luck);
        }

        if (resList.isEmpty()) {
            return;
        }
//        // bitmap 方式
//        List<Bitmap> bitmapList = new ArrayList<>();
//        bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.emoji_cake));
//        bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.emoji_dog));
        VMEmojiRainView.Config config = new VMEmojiRainView.Config.Builder()
//                .setBitmapList(bitmapList)
                .setResList(resList)
                .setWidth(VMDimen.dp2px(32))
                .setHeight(VMDimen.dp2px(32))
                .setCount(56)
                .setDelay(200)
                .setMaxDuration(5000)
                .setMinDuration(4500)
                .setMaxScale(150)
                .setMinScale(50)
                .build();

        mEmojiRainView.start(config);
    }

    /**
     * 刷新插入新消息
     */
    private void refreshInsert(final EMMessage message) {
        // 触发表情雨
        checkEmojiRain(message);
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
     */
    private void loadMore() {
        if (IMChatManager.getInstance().getLastMessage(mId, mChatType) == null) {
            return;
        }
        EMMessage message = mConversation.getAllMessages().get(0);
        List<EMMessage> list = IMChatManager.getInstance().loadMoreMessages(mId, mChatType, message.getMsgId());
        if (list.size() > 0) {
            mAdapter.updateInsert(0, list.size());
            mRecyclerView.smoothScrollBy(0, -VMDimen.dp2px(50));
        } else {

        }
    }

    /**
     * 主要接收图片选择器数据
     */
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
        IM.getInstance().setCurrChatId(null);
        IMVoiceManager.getInstance().stop();
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
        /**
         * 减少内存用量，这里设置为当前会话内存中多于一页时清除内存中的消息，只保留一条
         */
        if (mConversation.getAllMessages().size() > mPageSize) {
            // 清除内存中的消息，此方法不清空DB
            IMChatManager.getInstance().clearConversation(mId);
            // 加载消息到内存
            IMChatManager.getInstance().getLastMessage(mId, mChatType);
        }

        unregisterReceiver();

        super.onDestroy();
    }

    /**
     * ------------------------------- 广播接收器部分 -------------------------------
     */
    private ChatReceiver mChatReceiver = new ChatReceiver();

    /**
     * 初始化注册广播接收器
     */
    private void initReceiver() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
        // 新消息广播接收器
        IntentFilter filter = new IntentFilter(IMUtils.Action.getNewMessageAction());
        filter.addAction(IMUtils.Action.getUpdateMessageAction());
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        lbm.registerReceiver(mChatReceiver, filter);
    }

    /**
     * 取消注册广播接收器
     */
    private void unregisterReceiver() {
        // 取消新消息广播接收器
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mChatReceiver);
    }

    /**
     * 定义广播接收器
     */
    private class ChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //这里处理接收的信息
            String id = intent.getStringExtra(IMConstants.IM_CHAT_ID);
            if (!VMStr.isEmpty(id) && id.equals(mId)) {
                EMMessage message = intent.getParcelableExtra(IMConstants.IM_CHAT_MSG);
                if (intent.getAction().equals(IMUtils.Action.getNewMessageAction())) {
                    // 当前会话设置消息已读
                    mConversation.markMessageAsRead(message.getMsgId());
                    refreshInsert(message);
                } else if (intent.getAction().equals(IMUtils.Action.getUpdateMessageAction())) {
                    refreshChange(message);
                }
            }
        }
    }
}
