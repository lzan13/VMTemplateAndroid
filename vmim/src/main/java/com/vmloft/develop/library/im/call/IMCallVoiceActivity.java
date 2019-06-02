package com.vmloft.develop.library.im.call;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Created by lzan13 on 2016/10/18.
 *
 * 音频通话界面处理
 */
public class IMCallVoiceActivity extends IMCallActivity {

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

        mMicMuteBtn.setOnClickListener(viewListener);
        mAnswerBtn.setOnClickListener(viewListener);
        mEndBtn.setOnClickListener(viewListener);
        mSpeakerBtn.setOnClickListener(viewListener);

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

    private View.OnClickListener viewListener = (View v) -> {
        if (v.getId() == R.id.im_call_mic_btn) {
            changeMic();
        } else if (v.getId() == R.id.im_call_answer_btn) {
            answerCall();
        } else if (v.getId() == R.id.im_call_end_btn) {
            endCall();
        } else if (v.getId() == R.id.im_call_speaker_btn) {
            changeSpeaker();
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
        IMCallManager.getInstance().openVoice(!mMicMuteBtn.isSelected());
    }

    /**
     * 扬声器开关
     */
    private void changeSpeaker() {
        // 根据按钮状态决定打开还是关闭扬声器
        mSpeakerBtn.setSelected(!mSpeakerBtn.isSelected());
        IMCallManager.getInstance().openSpeaker(mSpeakerBtn.isSelected());
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
}
