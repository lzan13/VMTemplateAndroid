package com.vmloft.develop.library.im.call;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.widget.IMCallView;

/**
 * Created by lzan13 on 2016/10/18.
 *
 * 音频通话界面处理
 */
public class VoiceCallActivity extends CallActivity {

    private ImageView mCoverView;
    private IMCallView mRemoteView;
    private IMCallView mLocalView;
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
        return R.layout.im_activity_call;
    }

    @Override
    protected void initUI() {
        super.initUI();

        mCoverView = findViewById(R.id.im_call_cover_iv);
        mRemoteView = findViewById(R.id.im_call_remote_view);
        mLocalView = findViewById(R.id.im_call_local_view);
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

        mMicMuteBtn.setSelected(!CallManager.getInstance().isOpenVoice());
        mSpeakerBtn.setSelected(CallManager.getInstance().isOpenSpeaker());

        if (CallManager.getInstance().isInComingCall()) {
            mAnswerBtn.setVisibility(View.VISIBLE);
            mStatusView.setText(R.string.im_call_incoming);
        } else {
            mAnswerBtn.setVisibility(View.GONE);
            mStatusView.setText(R.string.im_call_out);
        }

        // 判断当前通话时刚开始，还是从后台恢复已经存在的通话
        if (CallManager.getInstance().getCallStatus() == CallManager.CallStatus.ACCEPTED) {
            mAnswerBtn.setVisibility(View.GONE);
            mStatusView.setText(R.string.im_call_accepted);
            refreshCallTime();
        }
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {

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
        CallManager.getInstance().openVoice(!mMicMuteBtn.isSelected());
    }

    /**
     * 扬声器开关
     */
    private void changeSpeaker() {
        // 根据按钮状态决定打开还是关闭扬声器
        mSpeakerBtn.setSelected(!mSpeakerBtn.isSelected());
        CallManager.getInstance().openSpeaker(mSpeakerBtn.isSelected());
    }

    ///**
    // * 刷新通话界面
    // */
    //private void refreshCallView(CallEvent event) {
    //    EMCallStateChangeListener.CallError callError = event.getCallError();
    //    EMCallStateChangeListener.CallState callState = event.getCallState();
    //    switch (callState) {
    //    case CONNECTING: // 正在呼叫对方，TODO 没见回调过
    //        VMLog.i("正在呼叫对方" + callError);
    //        break;
    //    case CONNECTED: // 正在等待对方接受呼叫申请（对方申请与你进行通话）
    //        VMLog.i("正在连接" + callError);
    //        runOnUiThread(new Runnable() {
    //            @Override
    //            public void run() {
    //                if (CallManager.getInstance().isInComingCall()) {
    //                    callStateView.setText(R.string.call_connected_is_incoming);
    //                } else {
    //                    callStateView.setText(R.string.call_connected);
    //                }
    //            }
    //        });
    //        break;
    //    case ACCEPTED: // 通话已接通
    //        VMLog.i("通话已接通");
    //        runOnUiThread(new Runnable() {
    //            @Override
    //            public void run() {
    //                callStateView.setText(R.string.call_accepted);
    //            }
    //        });
    //        break;
    //    case DISCONNECTED: // 通话已中断
    //        VMLog.i("通话已结束" + callError);
    //        onFinish();
    //        break;
    //    // TODO 3.3.0版本 SDK 下边几个暂时都没有回调
    //    case NETWORK_UNSTABLE:
    //        if (callError == EMCallStateChangeListener.CallError.ERROR_NO_DATA) {
    //            VMLog.i("没有通话数据" + callError);
    //        } else {
    //            VMLog.i("网络不稳定" + callError);
    //        }
    //        break;
    //    case NETWORK_NORMAL:
    //        VMLog.i("网络正常");
    //        break;
    //    case VIDEO_PAUSE:
    //        VMLog.i("视频传输已暂停");
    //        break;
    //    case VIDEO_RESUME:
    //        VMLog.i("视频传输已恢复");
    //        break;
    //    case VOICE_PAUSE:
    //        VMLog.i("语音传输已暂停");
    //        break;
    //    case VOICE_RESUME:
    //        VMLog.i("语音传输已恢复");
    //        break;
    //    default:
    //        break;
    //    }
    //}

    /**
     * 刷新通话时间显示
     */
    private void refreshCallTime() {
        //int t = CallManager.getInstance().getCallTime();
        //int h = t / 60 / 60;
        //int m = t / 60 % 60;
        //int s = t % 60 % 60;
        //String time = "";
        //if (h > 9) {
        //    time = "" + h;
        //} else {
        //    time = "0" + h;
        //}
        //if (m > 9) {
        //    time += ":" + m;
        //} else {
        //    time += ":0" + m;
        //}
        //if (s > 9) {
        //    time += ":" + s;
        //} else {
        //    time += ":0" + s;
        //}
        //if (!mTimeView.isShown()) {
        //    mTimeView.setVisibility(View.VISIBLE);
        //}
        //mTimeView.setText(time);
    }
}
