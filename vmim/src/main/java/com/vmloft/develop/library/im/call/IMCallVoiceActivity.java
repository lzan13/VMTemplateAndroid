package com.vmloft.develop.library.im.call;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.emotion.IMEmotionGroup;
import com.vmloft.develop.library.im.emotion.IMEmotionItem;
import com.vmloft.develop.library.im.emotion.IMEmotionManager;
import com.vmloft.develop.library.im.emotion.IMEmotionRecyclerView;
import com.vmloft.develop.library.im.utils.IMAnimator;
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.utils.VMColor;
import com.vmloft.develop.library.tools.utils.VMStr;

import java.util.List;

/**
 * Created by lzan13 on 2016/10/18.
 *
 * 音频通话界面处理
 */
public class IMCallVoiceActivity extends IMCallActivity {

    private View mRootView;
    private ImageView mCoverView;
    private ImageView mAvatarView;
    private TextView mNameView;
    private ImageView mSelfAvatarView;
    private TextView mSelfNameView;
    private TextView mStatusView;
    private TextView mTimeView;
    private ImageButton mAnswerBtn;
    private ImageButton mMicMuteBtn;
    private ImageButton mEndBtn;
    private ImageButton mSpeakerBtn;

    // 扩展表情按钮
    private ImageView mExtEmotionView;
    // 通话扩展容器
    private RelativeLayout mExtContainer;
    // 展示通话过程中收到的表情控件
    private ImageView mEmotionView;

    /**
     * 加载布局 id
     *
     * @return 返回布局 id
     */
    @Override
    protected int layoutId() {
        return R.layout.im_activity_call_voice;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initReceiver();
    }

    @Override
    protected void initUI() {
        super.initUI();
        getTopBar().setTitleColor(R.color.vm_white);

        mRootView = findViewById(R.id.im_call_root_cl);
        mCoverView = findViewById(R.id.im_call_cover_iv);
        mAvatarView = findViewById(R.id.im_call_avatar_iv);
        mNameView = findViewById(R.id.im_call_name_tv);
        mSelfAvatarView = findViewById(R.id.im_call_self_avatar_iv);
        mSelfNameView = findViewById(R.id.im_call_self_name_tv);
        mStatusView = findViewById(R.id.im_call_status_tv);
        mTimeView = findViewById(R.id.im_call_time_tv);
        mMicMuteBtn = findViewById(R.id.im_call_mic_btn);
        mAnswerBtn = findViewById(R.id.im_call_answer_btn);
        mEndBtn = findViewById(R.id.im_call_end_btn);
        mSpeakerBtn = findViewById(R.id.im_call_speaker_btn);
        mExtEmotionView = findViewById(R.id.im_call_ext_emotion_iv);
        mExtContainer = findViewById(R.id.im_call_ext_container_rl);
        mEmotionView = findViewById(R.id.im_call_emotion_iv);

        mRootView.setOnClickListener(viewListener);
        mMicMuteBtn.setOnClickListener(viewListener);
        mAnswerBtn.setOnClickListener(viewListener);
        mEndBtn.setOnClickListener(viewListener);
        mSpeakerBtn.setOnClickListener(viewListener);
        mExtEmotionView.setOnClickListener(viewListener);

        // 设置按钮状态
        mMicMuteBtn.setSelected(!IMCallManager.getInstance().isOpenVoice());
        mSpeakerBtn.setSelected(IMCallManager.getInstance().isOpenSpeaker());

        if (IMCallManager.getInstance().isInComingCall()) {
            mAnswerBtn.setVisibility(View.VISIBLE);
            mStatusView.setText(R.string.im_call_incoming);
        } else {
            mAnswerBtn.setVisibility(View.GONE);
            mStatusView.setText(R.string.im_call_out);
        }

        // 判断当前通话时刚开始，还是从后台恢复已经存在的通话
        if (IMCallManager.getInstance().getCallStatus() == IMCallManager.CallStatus.ACCEPTED) {
            mAnswerBtn.setVisibility(View.GONE);
            mStatusView.setText(R.string.im_call_accepted);
        }

        initExtContainer();
    }

    /**
     * 初始化扩展部分
     */
    private void initExtContainer() {
        List<IMEmotionGroup> groupList = IMEmotionManager.getInstance().getEmotionGroupList();
        if (groupList == null || groupList.size() < 2) {
            return;
        }
        IMEmotionRecyclerView emotionRecyclerView = new IMEmotionRecyclerView(mActivity, groupList.get(1));
        emotionRecyclerView.setEmotionListener((IMEmotionGroup group, IMEmotionItem item) -> {
            EMMessage message = IMChatManager.getInstance().createActionMessage(IMConstants.IM_MSG_ACTION_EMOTION, mId);
            message.setAttribute(IMConstants.IM_MSG_EXT_EMOTION_GROUP, group.mName);
            message.setAttribute(IMConstants.IM_MSG_EXT_EMOTION_DESC, item.mDesc);
            // 调用发送消息方法，这个不需要回调
            IMChatManager.getInstance().sendMessage(message, null);
        });
        mExtContainer.addView(emotionRecyclerView);
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
        mId = IMCallManager.getInstance().getCallId();
        if (VMStr.isEmpty(mId)) {
            onFinish();
            return;
        }
        mContact = IM.getInstance().getIMContact(mId);
        mSelfContact = IM.getInstance().getIMSelfContact();

        setupContact();
    }

    private View.OnClickListener viewListener = (View v) -> {
        if (v.getId() == R.id.im_call_root_cl) {
            showExtEmotion(false);
        } else if (v.getId() == R.id.im_call_mic_btn) {
            changeMic();
        } else if (v.getId() == R.id.im_call_answer_btn) {
            answerCall();
        } else if (v.getId() == R.id.im_call_end_btn) {
            endCall();
        } else if (v.getId() == R.id.im_call_speaker_btn) {
            changeSpeaker();
        } else if (v.getId() == R.id.im_call_ext_emotion_iv) {
            showExtEmotion(true);
        }
    };

    /**
     * 接听通话
     */
    @Override
    protected void answerCall() {
        super.answerCall();
        mAnswerBtn.setVisibility(View.GONE);
    }

    /**
     * 麦克风开关，主要调用环信语音数据传输方法
     */
    private void changeMic() {
        // 根据按钮状态决定打开还是关闭麦克风
        mMicMuteBtn.setSelected(!mMicMuteBtn.isSelected());
        if (mMicMuteBtn.isSelected()) {
            mMicMuteBtn.setImageTintList(ColorStateList.valueOf(VMColor.byRes(R.color.vm_black_87)));
        } else {
            mMicMuteBtn.setImageTintList(ColorStateList.valueOf(VMColor.byRes(R.color.vm_white_87)));
        }
        IMCallManager.getInstance().openVoice(!mMicMuteBtn.isSelected());
    }

    /**
     * 扬声器开关
     */
    private void changeSpeaker() {
        // 根据按钮状态决定打开还是关闭扬声器
        mSpeakerBtn.setSelected(!mSpeakerBtn.isSelected());
        if (mSpeakerBtn.isSelected()) {
            mSpeakerBtn.setImageTintList(ColorStateList.valueOf(VMColor.byRes(R.color.vm_black_87)));
        } else {
            mSpeakerBtn.setImageTintList(ColorStateList.valueOf(VMColor.byRes(R.color.vm_white_87)));
        }
        IMCallManager.getInstance().openSpeaker(mSpeakerBtn.isSelected());
    }

    /**
     * 加载联系人信息
     */
    private void setupContact() {
        IPictureLoader.Options options = new IPictureLoader.Options(mContact.mAvatar);
        options.isBlur = true;
        IM.getInstance().getPictureLoader().load(mActivity, options, mCoverView);

        options = new IPictureLoader.Options(mContact.mAvatar);
        options.isCircle = true;
        IM.getInstance().getPictureLoader().load(mActivity, options, mAvatarView);
        mNameView.setText(mContact.mNickname);

        options = new IPictureLoader.Options(mSelfContact.mAvatar);
        options.isCircle = true;
        IM.getInstance().getPictureLoader().load(mActivity, options, mSelfAvatarView);
        mSelfNameView.setText(mSelfContact.mNickname);
    }

    /**
     * 显示扩展表情
     *
     * @param show 是否显示
     */
    private void showExtEmotion(boolean show) {
        mExtContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示收到的表情
     */
    private void showReceiveEmotion(EMMessage message) {
        EMCmdMessageBody body = (EMCmdMessageBody) message.getBody();
        if (!body.action().equals(IMConstants.IM_MSG_ACTION_EMOTION)) {
            return;
        }
        String group = message.getStringAttribute(IMConstants.IM_MSG_EXT_EMOTION_GROUP, "");
        String desc = message.getStringAttribute(IMConstants.IM_MSG_EXT_EMOTION_DESC, "");
        IMEmotionItem item = IMEmotionManager.getInstance().getEmotionItem(group, desc);
        if (item == null) {
            return;
        }
        mEmotionView.setImageResource(item.mResId);

        IMAnimator.createAnimator().play(mEmotionView, IMAnimator.ROTATION, null, 1, 1000, 0f, 360f);
    }

    /**
     * 通话状态改变
     */
    @Override
    protected void onStatusChange() {
        mStatusView.setText(IMCallManager.getInstance().getCallStatusInfo());
    }

    /**
     * 刷新通话时间显示
     */
    @Override
    protected void onRefreshCallTime() {
        if (!mTimeView.isShown()) {
            mTimeView.setVisibility(View.VISIBLE);
        }
        mTimeView.setText(IMCallManager.getInstance().getCallTime());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    /**
     * ------------------------------- 广播接收器部分 -------------------------------
     */
    private CMDMessageReceiver mReceiver = new CMDMessageReceiver();

    /**
     * 初始化注册广播接收器
     */
    private void initReceiver() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mActivity);
        // 新消息广播接收器
        IntentFilter filter = new IntentFilter(IMUtils.Action.getCMDMessageAction());
        lbm.registerReceiver(mReceiver, filter);
    }

    /**
     * 取消注册广播接收器
     */
    private void unregisterReceiver() {
        // 取消新消息广播接收器
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mReceiver);
    }

    /**
     * 定义广播接收器
     */
    private class CMDMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //这里处理接收的信息
            String id = intent.getStringExtra(IMConstants.IM_CHAT_ID);
            if (!VMStr.isEmpty(id) && id.equals(mId)) {
                EMMessage message = intent.getParcelableExtra(IMConstants.IM_CHAT_MSG);
                showReceiveEmotion(message);
            }
        }
    }
}
