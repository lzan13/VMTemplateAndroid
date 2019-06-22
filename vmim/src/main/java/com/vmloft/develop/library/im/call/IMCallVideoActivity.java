package com.vmloft.develop.library.im.call;

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

import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.im.widget.IMCallView;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.utils.VMColor;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Created by lzan13 on 2016/10/18.
 *
 * 音频通话界面处理
 */
public class IMCallVideoActivity extends IMCallActivity {

    // 视频通话容器
    private RelativeLayout mCallContainer;
    private IMCallView mCallLView = null;
    private IMCallView mCallRView = null;
    private RelativeLayout.LayoutParams mLocalParams = null;
    private RelativeLayout.LayoutParams mRemoteParams = null;
    // 通话显示画面控件状态，-1 表示通话未接通，0 表示本小远大，1 表示远小本大
    private int mViewStatus = -1;

    private View mRootView;
    private View mControlView;
    private ImageView mCoverView;
    private ImageView mAvatarView;
    private TextView mNameView;
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
        return R.layout.im_activity_call_video;
    }

    @Override
    protected void initUI() {
        super.initUI();
        getTopBar().setIcon(R.drawable.im_ic_mini);
        getTopBar().setTitleColor(R.color.vm_white);

        mRootView = findViewById(R.id.im_call_root_rl);
        mControlView = findViewById(R.id.im_call_control_cl);
        mCallContainer = findViewById(R.id.im_call_video_container);
        mCoverView = findViewById(R.id.im_call_cover_iv);
        mAvatarView = findViewById(R.id.im_call_avatar_iv);
        mNameView = findViewById(R.id.im_call_name_tv);
        mStatusView = findViewById(R.id.im_call_status_tv);
        mTimeView = findViewById(R.id.im_call_time_tv);
        mMicMuteBtn = findViewById(R.id.im_call_mic_btn);
        mAnswerBtn = findViewById(R.id.im_call_answer_btn);
        mEndBtn = findViewById(R.id.im_call_end_btn);
        mSpeakerBtn = findViewById(R.id.im_call_speaker_btn);

        mRootView.setOnClickListener(viewListener);
        mMicMuteBtn.setOnClickListener(viewListener);
        mAnswerBtn.setOnClickListener(viewListener);
        mEndBtn.setOnClickListener(viewListener);
        mSpeakerBtn.setOnClickListener(viewListener);

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

        initCallView();

        // 判断当前通话时刚开始，还是从后台恢复已经存在的通话
        if (IMCallManager.getInstance().getCallStatus() == IMCallManager.CallStatus.ACCEPTED) {
            mAnswerBtn.setVisibility(View.GONE);
            mStatusView.setText(R.string.im_call_accepted);
            onSetupCallView();
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
     * 界面控件点击监听
     */
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
     * 初始化通话界面控件
     */
    private void initCallView() {
        // 初始化显示远端画面控件
        mCallRView = new IMCallView(mActivity);
        mRemoteParams = new RelativeLayout.LayoutParams(0, 0);
        mRemoteParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        mRemoteParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        mCallRView.setLayoutParams(mRemoteParams);
        mCallContainer.addView(mCallRView);

        // 初始化显示本地画面控件
        mCallLView = new IMCallView(mActivity);
        mLocalParams = new RelativeLayout.LayoutParams(0, 0);
        mLocalParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        mLocalParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        mLocalParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mCallLView.setLayoutParams(mLocalParams);
        mCallContainer.addView(mCallLView);

        mCallLView.setZOrderOnTop(false);
        mCallLView.setZOrderMediaOverlay(true);

        mCallLView.setOnClickListener(v -> {
            onControlLayout();
        });

        if (IMCallManager.getInstance().isInComingCall()) {
            mCallLView.setVisibility(View.GONE);
            mCallRView.setVisibility(View.GONE);
            mCoverView.setVisibility(View.VISIBLE);
        } else {
            mCallLView.setVisibility(View.VISIBLE);
            mCoverView.setVisibility(View.GONE);
        }
        // 设置通话画面显示控件
        IMCallManager.getInstance().setCallView(mCallLView, mCallRView);
    }

    /**
     * 接通通话，这个时候要做的只是改变本地画面 view 大小，不需要做其他操作
     */
    private void onSetupCallView() {
        if (mViewStatus >= 0) {
            return;
        }
        // 更新通话界面控件状态
        mViewStatus = 0;

        mCoverView.setVisibility(View.GONE);
        mAvatarView.setVisibility(View.GONE);
        mNameView.setVisibility(View.GONE);

        int width = VMDimen.dp2px(96);
        int height = VMDimen.dp2px(128);
        int rightMargin = VMDimen.dp2px(16);
        int topMargin = VMDimen.dp2px(24);

        mLocalParams = new RelativeLayout.LayoutParams(width, height);
        mLocalParams.width = width;
        mLocalParams.height = height;
        mLocalParams.rightMargin = rightMargin;
        mLocalParams.topMargin = topMargin;
        mLocalParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mCallLView.setLayoutParams(mLocalParams);

        mCallLView.setVisibility(View.VISIBLE);
        mCallLView.setOnClickListener(v -> {changeCallView();});

        mCallRView.setVisibility(View.VISIBLE);
        mCallRView.setOnClickListener(v -> {
            onControlLayout();
        });
    }

    private void onControlLayout() {
        if (mControlView.isShown()) {
            mControlView.setVisibility(View.GONE);
        } else {
            mControlView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 切换通话界面，这里就是交换本地和远端画面控件设置，以达到通话大小画面的切换
     */
    private void changeCallView() {
        if (mViewStatus == 0) {
            mViewStatus = 1;
            IMCallManager.getInstance().setCallView(mCallRView, mCallLView);
        } else {
            mViewStatus = 0;
            IMCallManager.getInstance().setCallView(mCallLView, mCallRView);
        }
    }

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
    }

    /**
     * 通话状态改变
     */
    @Override
    protected void onStatusChange() {
        mStatusView.setText(IMCallManager.getInstance().getCallStatusInfo());
        if (IMCallManager.getInstance().getCallStatus() == IMCallManager.CallStatus.ACCEPTED) {
            onSetupCallView();
        }
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
            }
        }
    }
}
