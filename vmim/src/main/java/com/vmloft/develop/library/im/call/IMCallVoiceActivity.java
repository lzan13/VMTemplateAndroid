package com.vmloft.develop.library.im.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
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
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.tools.animator.VMAnimator;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.utils.VMColor;
import com.vmloft.develop.library.tools.utils.VMLog;
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
    // 骰子🎲
    private ImageView mExtDiceView;
    // 石头剪刀布
    private ImageView mExtSJBView;
    // 表情
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
    protected void initUI() {
        super.initUI();
        getTopBar().setIcon(R.drawable.im_ic_mini);
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
        // 扩展部分
        mExtEmotionView = findViewById(R.id.im_call_ext_emotion_iv);
        mExtDiceView = findViewById(R.id.im_call_ext_dice_iv);
        mExtSJBView = findViewById(R.id.im_call_ext_sjb_iv);
        mExtContainer = findViewById(R.id.im_call_ext_container_rl);
        mEmotionView = findViewById(R.id.im_call_emotion_iv);

        mRootView.setOnClickListener(viewListener);
        mMicMuteBtn.setOnClickListener(viewListener);
        mAnswerBtn.setOnClickListener(viewListener);
        mEndBtn.setOnClickListener(viewListener);
        mSpeakerBtn.setOnClickListener(viewListener);
        mExtEmotionView.setOnClickListener(viewListener);
        mExtDiceView.setOnClickListener(viewListener);
        mExtSJBView.setOnClickListener(viewListener);

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
            showExtMessage(message);
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

    /**
     * 界面控件点击监听
     */
    private View.OnClickListener viewListener = (View v) -> {
        if (v.getId() == R.id.im_call_root_cl) {
            mExtContainer.setVisibility(View.GONE);
        } else if (v.getId() == R.id.im_call_mic_btn) {
            changeMic();
        } else if (v.getId() == R.id.im_call_answer_btn) {
            answerCall();
        } else if (v.getId() == R.id.im_call_end_btn) {
            endCall();
        } else if (v.getId() == R.id.im_call_speaker_btn) {
            changeSpeaker();
        } else if (v.getId() == R.id.im_call_ext_emotion_iv) {
            mExtContainer.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.im_call_ext_dice_iv) {
            sendDice();
        } else if (v.getId() == R.id.im_call_ext_sjb_iv) {
            sendSJB();
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
        if (VMStr.isEmpty(mContact.mNickname)) {
            mNameView.setText(mContact.mUsername);
        } else {
            mNameView.setText(mContact.mNickname);
        }
        options = new IPictureLoader.Options(mSelfContact.mAvatar);
        options.isCircle = true;
        IM.getInstance().getPictureLoader().load(mActivity, options, mSelfAvatarView);
        mSelfNameView.setText("我");
    }

    /**
     * 发送骰子
     */
    private void sendDice() {
        EMMessage message = IMChatManager.getInstance().createActionMessage(IMConstants.IM_MSG_ACTION_DICE, mId);
        int index = IMUtils.random(1, 7);
        VMLog.d("骰子数 %d", index);
        message.setAttribute(IMConstants.IM_MSG_EXT_DICE_INDEX, index);
        // 调用发送消息方法，这个不需要回调
        IMChatManager.getInstance().sendMessage(message, null);
        showExtMessage(message);
    }

    /**
     * 发送石头剪刀布
     */
    private void sendSJB() {
        EMMessage message = IMChatManager.getInstance().createActionMessage(IMConstants.IM_MSG_ACTION_SJB, mId);
        int index = IMUtils.random(4);
        VMLog.d("石头剪刀布 %d", index);
        message.setAttribute(IMConstants.IM_MSG_EXT_SJB_INDEX, index);
        // 调用发送消息方法，这个不需要回调
        IMChatManager.getInstance().sendMessage(message, null);
        showExtMessage(message);
    }

    /**
     * 显示收到的扩展消息
     */
    private void showExtMessage(EMMessage message) {
        EMCmdMessageBody body = (EMCmdMessageBody) message.getBody();
        // 处理大表情动画及结果展示
        if (body.action().equals(IMConstants.IM_MSG_ACTION_EMOTION)) {
            String group = message.getStringAttribute(IMConstants.IM_MSG_EXT_EMOTION_GROUP, "");
            String desc = message.getStringAttribute(IMConstants.IM_MSG_EXT_EMOTION_DESC, "");
            IMEmotionItem item = IMEmotionManager.getInstance().getEmotionItem(group, desc);
            if (item == null) {
                return;
            }
            mEmotionView.setImageResource(item.mResId);

            VMAnimator.createAnimator()
                .play(VMAnimator.createOptions(mEmotionView, VMAnimator.ALPHA, 0.0f, 1.0f))
                .with(VMAnimator.createOptions(mEmotionView, VMAnimator.ROTATION, -30.0f, 0.0f, 30.0f, 0.f))
                .with(VMAnimator.createOptions(mEmotionView, VMAnimator.SCALEX, 0.0f, 1.5f, 1.0f))
                .with(VMAnimator.createOptions(mEmotionView, VMAnimator.SCALEY, 0.0f, 1.5f, 1.0f))
                .start(500);
        }

        // 处理骰子动画及结果展示
        if (body.action().equals(IMConstants.IM_MSG_ACTION_DICE)) {
            // 设置骰子动画，
            AnimationDrawable anim = new AnimationDrawable();
            anim.addFrame(getResources().getDrawable(R.drawable.im_dice_anim_0, null), 120);
            anim.addFrame(getResources().getDrawable(R.drawable.im_dice_anim_1, null), 120);
            anim.addFrame(getResources().getDrawable(R.drawable.im_dice_anim_2, null), 120);
            anim.addFrame(getResources().getDrawable(R.drawable.im_dice_anim_3, null), 120);
            anim.setOneShot(false);
            mEmotionView.setImageDrawable(anim);
            anim.start();
            new Handler().postDelayed(() -> {
                if (anim != null && anim.isRunning()) {
                    anim.stop();
                }
                int index = message.getIntAttribute(IMConstants.IM_MSG_EXT_DICE_INDEX, 1);
                int resId;
                switch (index) {
                case 1:
                    resId = R.drawable.im_dice_1;
                    break;
                case 2:
                    resId = R.drawable.im_dice_2;
                    break;
                case 3:
                    resId = R.drawable.im_dice_3;
                    break;
                case 4:
                    resId = R.drawable.im_dice_4;
                    break;
                case 5:
                    resId = R.drawable.im_dice_5;
                    break;
                case 6:
                    resId = R.drawable.im_dice_6;
                    break;
                default:
                    resId = R.drawable.im_dice_1;
                    break;
                }
                mEmotionView.setImageResource(resId);
            }, 1500);
        }
        // 处理石头剪刀布的动画及结果展示
        if (body.action().equals(IMConstants.IM_MSG_ACTION_SJB)) {
            // 设置骰子动画，
            AnimationDrawable anim = new AnimationDrawable();
            anim.addFrame(getResources().getDrawable(R.drawable.im_sjb_s, null), 120);
            anim.addFrame(getResources().getDrawable(R.drawable.im_sjb_j, null), 120);
            anim.addFrame(getResources().getDrawable(R.drawable.im_sjb_b, null), 120);
            anim.setOneShot(false);
            mEmotionView.setImageDrawable(anim);
            anim.start();
            new Handler().postDelayed(() -> {
                if (anim != null && anim.isRunning()) {
                    anim.stop();
                }
                int index = message.getIntAttribute(IMConstants.IM_MSG_EXT_SJB_INDEX, 1);
                int resId;
                switch (index) {
                case 1:
                    resId = R.drawable.im_sjb_s;
                    break;
                case 2:
                    resId = R.drawable.im_sjb_j;
                    break;
                case 3:
                    resId = R.drawable.im_sjb_b;
                    break;
                default:
                    resId = R.drawable.im_sjb_s;
                    break;
                }
                mEmotionView.setImageResource(resId);
            }, 1200);
        }
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

    /**
     * ------------------------------- 广播接收器部分 -------------------------------
     */
    private CMDMessageReceiver mReceiver = new CMDMessageReceiver();

    /**
     * 初始化注册广播接收器
     */
    @Override
    protected void initReceiver() {
        super.initReceiver();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mActivity);
        // 新消息广播接收器
        IntentFilter filter = new IntentFilter(IMUtils.Action.getCMDMessageAction());
        lbm.registerReceiver(mReceiver, filter);
    }

    /**
     * 取消注册广播接收器
     */
    @Override
    protected void unregisterReceiver() {
        super.unregisterReceiver();
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
                showExtMessage(message);
            }
        }
    }
}
