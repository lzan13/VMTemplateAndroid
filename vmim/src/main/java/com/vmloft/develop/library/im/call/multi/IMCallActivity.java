package com.vmloft.develop.library.im.call.multi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseActivity;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.call.CallManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.im.widget.IMCallView;
import com.vmloft.develop.library.tools.picker.IPictureLoader;

/**
 * Create by lzan13 on 2019/5/9 10:46
 *
 * IM 实时音视频通话界面
 */
public class IMCallActivity extends IMBaseActivity {

    private ImageView mCoverView;
    private IMCallView mRemoteView;
    private IMCallView mLocalView;
    private ImageView mAvatarView;
    private TextView mNameView;
    private ImageView mSelfAvatarView;
    private TextView mSelfNameView;
    private TextView mTimeView;
    private ImageButton mAnswerBtn;
    private ImageButton mMicMuteBtn;
    private ImageButton mEndBtn;
    private ImageButton mSpeakerBtn;

    // 对方 Id
    private String mId;
    // 是否别人呼叫来的
    private boolean isCall;
    // 会议 id
    private String mConferenceId;
    // 当前联系人信息
    private IMContact mContact;
    private IMContact mSelfContact;

    @Override
    protected void onResume() {
        super.onResume();

        initReceiver();
    }

    @Override
    protected int layoutId() {
        return R.layout.im_activity_call;
    }

    @Override
    protected void initUI() {
        super.initUI();
        getTopBar().setTitleColor(R.color.vm_white);

        mCoverView = findViewById(R.id.im_call_cover_iv);
        mRemoteView = findViewById(R.id.im_call_remote_view);
        mLocalView = findViewById(R.id.im_call_local_view);
        mAvatarView = findViewById(R.id.im_call_avatar_iv);
        mNameView = findViewById(R.id.im_call_name_tv);
        mSelfAvatarView = findViewById(R.id.im_call_self_avatar_iv);
        mSelfNameView = findViewById(R.id.im_call_self_name_tv);
        mTimeView = findViewById(R.id.im_call_time_tv);
        mMicMuteBtn = findViewById(R.id.im_call_mic_btn);
        mAnswerBtn = findViewById(R.id.im_call_answer_btn);
        mEndBtn = findViewById(R.id.im_call_end_btn);
        mSpeakerBtn = findViewById(R.id.im_call_speaker_btn);

        mMicMuteBtn.setOnClickListener(viewListener);
        mAnswerBtn.setOnClickListener(viewListener);
        mEndBtn.setOnClickListener(viewListener);
        mSpeakerBtn.setOnClickListener(viewListener);

        IMCallManager.getInstance().setRemoteView(mRemoteView);
        IMCallManager.getInstance().setLocalView(mLocalView);
    }

    @Override
    protected void initData() {
        mId = getIntent().getStringExtra(IMConstants.IM_CHAT_ID);
        isCall = getIntent().getBooleanExtra(IMConstants.IM_CHAT_IS_CALL, false);
        mConferenceId = getIntent().getStringExtra(IMConstants.IM_CHAT_CONFERENCE_ID);

        mContact = IM.getInstance().getIMContact(mId);
        mSelfContact = IM.getInstance().getIMSelfContact();

        setupContact();

        // 装载通话
        setupCall();
    }

    /**
     * 装载联系人信息
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
     * 改变麦克风
     */
    private void changeMic() {
        mMicMuteBtn.setSelected(!mMicMuteBtn.isSelected());
    }

    /**
     * 改变扬声器
     */
    private void changeSpeaker() {
        mSpeakerBtn.setSelected(!mSpeakerBtn.isSelected());
    }

    /**
     * 开始呼叫
     */
    private void setupCall() {
        // 如果不是别人呼叫进来的，就主动发起呼叫
        if (!isCall) {
            mAnswerBtn.setVisibility(View.GONE);

        } else {
            mAnswerBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 通话接听
     */
    private void answerCall() {
        mAnswerBtn.setVisibility(View.GONE);
        CallManager.getInstance().answerCall();
    }

    /**
     * 结束通话
     */
    private void endCall() {
        //CallManager.getInstance().rejectCall();
        CallManager.getInstance().endCall();
        onFinish();
    }

    /**
     * 重写父类的onNewIntent方法，防止打开两个聊天界面
     *
     * @param intent 带有参数的intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        String id = intent.getStringExtra(IMConstants.IM_CHAT_ID);
        // 判断 intent 携带的数据是否是当前聊天对象
        if (this.mId.equals(id)) {
            super.onNewIntent(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver();
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }

    @Override
    public void onBackPressed() {
        endCall();
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
            EMMessage message = intent.getParcelableExtra(IMConstants.IM_CHAT_MSG);
            EMCmdMessageBody body = (EMCmdMessageBody) message.getBody();
            if (body.action().equals(IMConstants.IM_CHAT_ACTION_CALL_BUSY)) {
                IMCallManager.getInstance().setCallStatus(IMConstants.CallStatus.IM_BUSY);
                endCall();
            } else if (body.action().equals(IMConstants.IM_CHAT_ACTION_CALL_REJECT)) {
                IMCallManager.getInstance().setCallStatus(IMConstants.CallStatus.IM_REJECTED);
                onFinish();
            } else if (body.action().equals(IMConstants.IM_CHAT_ACTION_CALL_END)) {
                IMCallManager.getInstance().setCallStatus(IMConstants.CallStatus.IM_END);
                onFinish();
            }
        }
    }
}
