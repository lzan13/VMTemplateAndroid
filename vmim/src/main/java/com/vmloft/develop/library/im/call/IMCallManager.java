package com.vmloft.develop.library.im.call;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import android.support.v4.content.LocalBroadcastManager;
import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;

import com.hyphenate.exceptions.HyphenateException;
import com.superrtc.sdk.VideoView;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.notify.IMNotifier;
import com.vmloft.develop.library.im.router.IMRouter;
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.im.widget.IMCallView;
import com.vmloft.develop.library.tools.utils.VMLog;

import com.vmloft.develop.library.tools.utils.VMStr;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lzan13 on 2019/06/02
 *
 * 实时音视频通话管理类，用来管理通话操作
 */
public class IMCallManager {

    // 音频管理器
    private AudioManager mAudioManager;
    // 音频池
    private SoundPool mSoundPool;

    private IMCallStatusReceiver mCallStatusReceiver;
    private IMCallReceiver mCallReceiver;

    // 声音资源 id
    private int streamID;
    private int loadId;
    // 表示声音已成功加载
    private boolean isLoaded = false;

    // 计时器
    private Timer mTimer;
    // 通话时间
    private int mCallTime = 0;

    // 通话状态监听
    private IMCallStateListener mCallStateListener;

    // 当前通话对象Id
    private String mCallId;
    // 是否为呼叫进来的通话
    private boolean isInComingCall = false;

    // 通话状态提示信息
    private String mCallStatusInfo;
    // 通话状态
    private int mCallStatus = CallStatus.DISCONNECTED;
    // 通话类型
    private int mCallType = CallType.VOICE;
    // 通话结束类型，用户保存消息
    private int mEndType = CallEndType.CANCEL;

    // 视频状态 默认为关闭
    private boolean isOpenVideo = true;
    // 语音状态 默认为关闭
    private boolean isOpenVoice = true;
    // 扬声器状态
    private boolean isOpenSpeaker = false;

    /**
     * 私有化构造函数
     */
    private IMCallManager() {}

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMCallManager INSTANCE = new IMCallManager();
    }

    /**
     * 获取单例类实例
     */
    public static IMCallManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 通话管理类的初始化
     */
    public void init() {
        // 音频管理器
        mAudioManager = (AudioManager) IM.getInstance().getIMContext().getSystemService(Context.AUDIO_SERVICE);

        // 初始化音频池
        initSoundPool();

        initCallOptions();
    }

    /**
     * 初始化一些通话设置
     */
    private void initCallOptions() {
        /**
         * SDK 3.2.x 版本后通话相关设置，一定要在初始化后，开始音视频功能前设置，否则设置无效
         */
        // 设置通话过程中对方如果离线是否发送离线推送通知，默认 false，这里需要和推送配合使用，这个就算对方不在线也会持续呼叫对方
        EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(true);
        EMClient.getInstance().callManager().setPushProvider(new IMCallPushProvider());

        /**
         * 设置是否启用外部输入视频数据，默认 false，如果设置为 true，需要自己调用
         * {@link EMCallManager#inputExternalVideoData(byte[], int, int, int)}输入视频数据
         */
        EMClient.getInstance().callManager().getCallOptions().setEnableExternalVideoData(false);
        // 设置视频旋转角度，启动前和视频通话中均可设置
        //EMClient.getInstance().callManager().getCallOptions().setRotation(90);
        // 设置自动调节分辨率，默认为 true
        EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(true);
        /**
         * 设置视频通话最大和最小比特率，可以不用设置，比特率会根据分辨率进行计算，默认最大(800)， 默认最小(80)
         * 这里的带宽是指理想带宽，指单人单线情况下的最低要求
         * >240p: 100k ~ 400kbps
         * >480p: 300k ~ 1Mbps
         * >720p: 900k ~ 2.5Mbps
         * >1080p: 2M  ~ 5Mbps
         */
        EMClient.getInstance().callManager().getCallOptions().setMaxVideoKbps(5000);
        EMClient.getInstance().callManager().getCallOptions().setMinVideoKbps(150);
        // 设置视频通话分辨率 默认是(640, 480)
        EMClient.getInstance().callManager().getCallOptions().setVideoResolution(640, 480);
        // 设置通话最大帧率，SDK 最大支持(30)，默认(20)
        EMClient.getInstance().callManager().getCallOptions().setMaxVideoFrameRate(30);
        // 设置音视频通话采样率，一般不需要设置，除非采集声音有问题才需要手动设置
        EMClient.getInstance().callManager().getCallOptions().setAudioSampleRate(48000);
        // 设置录制视频采用 mov 编码 TODO 后期这个接口需要移动到 EMCallOptions 中
        EMClient.getInstance().callManager().getVideoCallHelper().setPreferMovFormatEnable(true);

        // 设置通话广播监听器过滤内容
        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        mCallReceiver = new IMCallReceiver();
        IM.getInstance().getIMContext().registerReceiver(mCallReceiver, callFilter);
    }

    /**
     * ----------------------------------------- 通话操作 -----------------------------------------
     */

    /**
     * 开始呼叫
     *
     * @param id   对方 id
     * @param type 呼叫类型
     */
    public void startCall(String id, int type) {
        startCall(id, type, "");
    }

    /**
     * 开始呼叫
     *
     * @param id   对方 id
     * @param type 呼叫类型
     * @param ext  呼叫时带的扩展信息
     */
    public void startCall(String id, int type, String ext) {
        if (mCallStatus != CallStatus.DISCONNECTED) {
            return;
        }

        mCallId = id;
        mCallType = type;
        isInComingCall = false;

        startCallStateListener();

        try {
            if (mCallType == CallType.VIDEO) {
                EMClient.getInstance().callManager().makeVideoCall(mCallId);
            } else {
                EMClient.getInstance().callManager().makeVoiceCall(mCallId);
            }
            mCallStatus = CallStatus.CONNECTING;
        } catch (EMServiceNotReadyException e) {
            VMLog.e("发起通话失败 - %s", e.getMessage());
            mCallStatus = CallStatus.DISCONNECTED;
        }
        if (mCallStatus == CallStatus.CONNECTING) {
            attemptPlaySound();
            if (mCallType == CallType.VIDEO) {
                IMRouter.goIMCallVideo(IM.getInstance().getIMContext());
            } else {
                IMRouter.goIMCallVoice(IM.getInstance().getIMContext());
            }
        }

        // 设置通话状态监听广播
        IntentFilter callStatusFilter = new IntentFilter(IMUtils.Action.getCallStatusChangeAction());
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(IM.getInstance().getIMContext());
        mCallStatusReceiver = new IMCallStatusReceiver();
        lbm.registerReceiver(mCallStatusReceiver, callStatusFilter);
    }

    /**
     * 收到呼叫
     *
     * @param id   对方 id
     * @param type 呼叫类型
     */
    public void inComingCall(String id, int type) {
        mCallId = id;
        mCallType = type;
        isInComingCall = true;
        mCallStatus = CallStatus.CONNECTING;
        attemptPlaySound();

        startCallStateListener();

        if (mCallType == CallType.VIDEO) {
            IMRouter.goIMCallVideo(IM.getInstance().getIMContext());
        } else {
            IMRouter.goIMCallVoice(IM.getInstance().getIMContext());
        }
    }

    /**
     * 拒绝通话
     */
    public void rejectCall() {
        stopCallStateListener();
        mEndType = CallEndType.REJECT;
        try {
            // 调用 SDK 的拒绝通话方法
            EMClient.getInstance().callManager().rejectCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
        }
        // 保存一条通话消息
        saveCallMessage();
        // 通话结束，重置通话状态
        reset();
    }

    /**
     * 结束通话
     */
    public void endCall() {
        stopCallStateListener();
        try {
            // 调用 SDK 的结束通话方法
            EMClient.getInstance().callManager().endCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            VMLog.e("结束通话失败：error %d - %s", e.getErrorCode(), e.getMessage());
        }
        // 挂断电话调用保存消息方法
        saveCallMessage();
        // 通话结束，重置通话状态
        reset();
    }

    /**
     * 接听通话
     */
    public boolean answerCall() {
        // 接听通话后关闭通知铃音
        stopPlaySound();
        // 调用接通通话方法
        try {
            EMClient.getInstance().callManager().answerCall();
            return true;
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置视频通话显示控件
     *
     * @param local  显示本地画面控件
     * @param remote 显示对方画面控件
     */
    public void setCallView(IMCallView local, IMCallView remote) {
        // 设置本地和远端画面的显示方式，是填充，还是居中
        local.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
        remote.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
        EMClient.getInstance().callManager().setSurfaceView(local, remote);
    }

    /**
     * 开启扬声器
     */
    public void openSpeaker(boolean open) {
        isOpenSpeaker = open;
        if (isOpenSpeaker) {
            openSpeaker();
        } else {
            closeSpeaker();
        }
    }

    /**
     * 打开扬声器
     * 主要是通过扬声器的开关以及设置音频播放模式来实现
     * 1、MODE_NORMAL：是正常模式，一般用于外放音频
     * 2、MODE_IN_CALL：
     * 3、MODE_IN_COMMUNICATION：这个和 CALL 都表示通讯模式，不过 CALL 在华为上不好使，故使用 COMMUNICATION
     * 4、MODE_RINGTONE：铃声模式
     */
    public void openSpeaker() {
        // 检查是否已经开启扬声器
        if (!mAudioManager.isSpeakerphoneOn()) {
            // 打开扬声器
            mAudioManager.setSpeakerphoneOn(true);
        }
        // 开启了扬声器之后，因为是进行通话，声音的模式也要设置成通讯模式
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     * 关闭扬声器，即开启听筒播放模式
     * 更多内容看{@link #openSpeaker()}
     */
    public void closeSpeaker() {
        // 检查是否已经开启扬声器
        if (mAudioManager.isSpeakerphoneOn()) {
            // 关闭扬声器
            mAudioManager.setSpeakerphoneOn(false);
        }
        // 设置声音模式为通讯模式
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     * 设置视频状态
     */
    public void openVideo(boolean open) {
        isOpenVideo = open;
        try {
            if (isOpenVideo) {
                EMClient.getInstance().callManager().resumeVideoTransfer();
            } else {
                EMClient.getInstance().callManager().pauseVideoTransfer();
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置音频状态
     */
    public void openVoice(boolean open) {
        isOpenVideo = open;
        try {
            if (isOpenVideo) {
                EMClient.getInstance().callManager().resumeVoiceTransfer();
            } else {
                EMClient.getInstance().callManager().pauseVoiceTransfer();
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    // 获取通话 id
    public String getCallId() {
        return mCallId;
    }

    // 通话状态
    public void setCallType(int type) {
        mCallType = type;
    }

    public int getCallType() {
        return mCallType;
    }

    // 通话状态提示信息
    public void setCallStatusInfo(String info) {
        mCallStatusInfo = info;
    }

    public String getCallStatusInfo() {
        return mCallStatusInfo;
    }

    // 通话状态
    public void setCallStatus(int status) {
        mCallStatus = status;
    }

    public int getCallStatus() {
        return mCallStatus;
    }

    // 是否为呼叫进来的通话
    public boolean isInComingCall() {
        return isInComingCall;
    }

    // 通话结束类型
    public void setEndType(int type) {
        mEndType = type;
    }

    public int getEndType() {
        return mEndType;
    }

    // 判断是否打开了摄像头
    public boolean isOpenVideo() {
        return isOpenVideo;
    }

    // 判断是否打开了麦克风
    public boolean isOpenVoice() {
        return isOpenVoice;
    }

    // 是否打开了扬声器
    public boolean isOpenSpeaker() {
        return isOpenSpeaker;
    }

    /**
     * ----------------------------- Sound start -----------------------------
     * 初始化 SoundPool
     */
    private void initSoundPool() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                // 设置音频要用在什么地方，这里选择电话通知铃音
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            // 当系统的 SDK 版本高于21时，使用 build 的方式实例化 SoundPool
            mSoundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(1).build();
        } else {
            // 老版本使用构造函数方式实例化 SoundPool，MODE 设置为铃音 MODE_RINGTONE
            mSoundPool = new SoundPool(1, AudioManager.MODE_RINGTONE, 0);
        }
    }

    /**
     * 加载音效资源
     */
    private void loadSound() {
        if (isInComingCall) {
            loadId = mSoundPool.load(IM.getInstance().getIMContext(), R.raw.im_incoming_call, 1);
        } else {
            loadId = mSoundPool.load(IM.getInstance().getIMContext(), R.raw.im_call_out, 1);
        }
    }

    /**
     * 尝试播放呼叫通话提示音
     */
    public void attemptPlaySound() {
        // 检查音频资源是否已经加载完毕
        if (isLoaded) {
            startPlaySound();
        } else {
            // 播放之前先去加载音效
            loadSound();
            // 设置资源加载监听，也因为加载资源在单独的进程，需要时间，所以等监听到加载完成才能播放
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    VMLog.d("SoundPool load complete! loadId: %d", loadId);
                    isLoaded = true;
                    // 首次监听到加载完毕，开始播放音频
                    startPlaySound();
                }
            });
        }
    }

    /**
     * 播放音频
     */
    private void startPlaySound() {
        // 打开扬声器
        openSpeaker();
        // 设置音频管理器音频模式为铃音模式
        mAudioManager.setMode(AudioManager.MODE_RINGTONE);
        // 播放提示音，返回一个播放的音频id，等下停止播放需要用到
        if (mSoundPool != null && mCallStatus != CallStatus.DISCONNECTED) {
            streamID = mSoundPool.play(loadId, // 播放资源id；就是加载到SoundPool里的音频资源顺序
                0.5f,   // 左声道音量
                0.5f,   // 右声道音量
                1,      // 优先级，数值越高，优先级越大
                -1,     // 是否循环；0 不循环，-1 循环，N 表示循环次数
                1);     // 播放速率；从0.5-2，一般设置为1，表示正常播放
        }
    }

    /**
     * 关闭音效的播放，并释放资源
     */
    protected void stopPlaySound() {
        if (mSoundPool != null) {
            // 停止播放音效
            mSoundPool.stop(streamID);
            // 卸载音效
            //mSoundPool.unload(loadId);
            // 释放资源
            //mSoundPool.release();
        }
    }

    /**
     * 开始通话状态监听
     */
    private void startCallStateListener() {
        // 设置通话状态监听
        mCallStateListener = new IMCallStateListener();
        EMClient.getInstance().callManager().addCallStateChangeListener(mCallStateListener);
    }

    /**
     * 停止通话状态监听
     */
    private void stopCallStateListener() {
        if (mCallStateListener != null) {
            EMClient.getInstance().callManager().removeCallStateChangeListener(mCallStateListener);
        }
    }

    /**
     * 开始通话计时，这里在全局管理器中开启一个定时器进行计时，可以做到最小化，以及后台时进行计时
     */
    public void startCallTime() {
        notifyRefreshCallTime();
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.purge();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mCallTime++;
                notifyRefreshCallTime();
            }
        };
        mTimer.scheduleAtFixedRate(task, 1000, 1000);
    }

    /**
     * 通知更新时间
     */
    private void notifyRefreshCallTime() {
        Intent intent = new Intent(IMUtils.Action.getCallStatusChangeAction());
        intent.putExtra(IMConstants.IM_CHAT_CALL_TIME, true);
        IMUtils.sendLocalBroadcast(intent);
    }

    /**
     * 获取通话时间
     */
    public String getCallTime() {
        int t = mCallTime;
        int h = t / 60 / 60;
        int m = t / 60 % 60;
        int s = t % 60 % 60;
        String time = "";
        if (h > 9) {
            time = "" + h;
        } else {
            time = "0" + h;
        }
        if (m > 9) {
            time += ":" + m;
        } else {
            time += ":0" + m;
        }
        if (s > 9) {
            time += ":" + s;
        } else {
            time += ":0" + s;
        }
        return time;
    }

    /**
     * 停止计时
     */
    public void stopCallTime() {
        if (mTimer != null) {
            mTimer.purge();
            mTimer.cancel();
            mTimer = null;
        }
        mCallTime = 0;
    }

    /**
     * 通话结束，保存一条记录通话的消息
     */
    public void saveCallMessage() {
        VMLog.d("The call ends and the call log message is saved! " + mEndType);
        EMMessage message = null;
        EMTextMessageBody body = null;
        String content = null;

        switch (mEndType) {
        case CallEndType.NORMAL: // 正常结束通话
            content = VMStr.byResArgs(R.string.im_call_time, getCallTime());
            break;
        case CallEndType.CANCEL: // 取消
            content = VMStr.byRes(R.string.call_cancel);
            break;
        case CallEndType.CANCELLED: // 被取消
            content = VMStr.byRes(R.string.call_cancel_is_incoming);
            break;
        case CallEndType.BUSY: // 对方忙碌
            content = VMStr.byRes(R.string.call_busy);
            break;
        case CallEndType.OFFLINE: // 对方不在线
            content = VMStr.byRes(R.string.call_offline);
            break;
        case CallEndType.REJECT: // 拒绝的
            content = VMStr.byRes(R.string.call_reject_is_incoming);
            break;
        case CallEndType.REJECTED: // 被拒绝的
            content = VMStr.byRes(R.string.call_reject);
            break;
        case CallEndType.NORESPONSE: // 未响应
            content = VMStr.byRes(R.string.call_no_response);
            break;
        case CallEndType.TRANSPORT: // 建立连接失败
            content = VMStr.byRes(R.string.call_connection_fail);
            break;
        case CallEndType.DIFFERENT: // 通讯协议不同
            content = VMStr.byRes(R.string.call_offline);
            break;
        default:
            // 默认取消
            content = VMStr.byRes(R.string.call_cancel);
            break;
        }
        message = IMChatManager.getInstance().createTextMessage(content, mCallId, !isInComingCall);
        message.setStatus(EMMessage.Status.SUCCESS);
        message.setAttribute(IMConstants.IM_MSG_EXT_NOTIFY, false);
        message.setAttribute(IMConstants.IM_MSG_EXT_TYPE, IMConstants.MsgExtType.IM_CALL);
        message.setUnread(false);
        message.setAcked(true);
        message.setDeliverAcked(true);
        message.setAttribute(IMConstants.IM_MSG_EXT_VIDEO_CALL, mCallType == CallType.VIDEO);
        IMChatManager.getInstance().saveMessage(message);

        // 更新会话时间
        EMConversation conversation = IMChatManager.getInstance().getConversation(mCallId, IMConstants.ChatType.IM_SINGLE_CHAT);
        IMChatManager.getInstance().setTime(conversation, message.localTime());

        // 发送广播更新聊天界面
        Intent intent = new Intent(IMUtils.Action.getNewMessageAction());
        intent.putExtra(IMConstants.IM_CHAT_ID, message.conversationId());
        intent.putExtra(IMConstants.IM_CHAT_MSG, message);
        IMUtils.sendLocalBroadcast(intent);
        // 会话也需要刷新
        IMUtils.sendLocalBroadcast(IMUtils.Action.getRefreshConversationAction());
    }

    /**
     * ---------------------------------------- 最小化通话界面相关操作 ----------------------------------------
     */
    /**
     * 最小化通话
     */
    public void miniCall() {
        IMNotifier.getInstance().notifyCall();
        IMCallFloatWindow.getInstance().addFloatWindow();
    }

    /**
     * 恢复通话
     */
    public void fillCall() {
        IMNotifier.getInstance().notifyCallCancel();
        IMCallFloatWindow.getInstance().removeFloatWindow();
    }

    /**
     * 释放资源
     */
    public void reset() {
        mCallId = null;

        isOpenVoice = true;
        isOpenVideo = true;
        isOpenSpeaker = false;

        mCallStatus = CallStatus.DISCONNECTED;
        mEndType = CallEndType.CANCEL;

        // 停止播放声音
        stopPlaySound();
        // 停止监听
        stopCallStateListener();
        // 停止计时
        stopCallTime();

        // 释放音频资源
        if (mSoundPool != null) {
            // 停止播放音效
            mSoundPool.stop(streamID);
        }
        // 重置音频管理器
        if (mAudioManager != null) {
            mAudioManager.setSpeakerphoneOn(true);
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
        }

        IMNotifier.getInstance().notifyCallCancel();
        IMCallFloatWindow.getInstance().removeFloatWindow();
        LocalBroadcastManager.getInstance(IM.getInstance().getIMContext()).unregisterReceiver(mCallStatusReceiver);
    }

    /**
     * 通话状态
     */
    public interface CallStatus {
        int CONNECTING = 0;   // 连接中
        int CONNECTED = 1;    // 连接成功，等待接受
        int ACCEPTED = 2;     // 通话中
        int DISCONNECTED = 3; // 通话中断
    }

    /**
     * 通话类型
     */
    public interface CallType {
        int VIDEO = 0;  // 视频通话
        int VOICE = 1;  // 音频通话
    }

    /**
     * 通话结束类型
     */
    public interface CallEndType {
        int NORMAL = 0;      // 正常结束通话
        int CANCEL = 1;      // 取消
        int CANCELLED = 2;   // 被取消
        int BUSY = 3;        // 对方忙碌
        int OFFLINE = 4;     // 对方不在线
        int REJECT = 5;      // 拒绝的
        int REJECTED = 6;    // 被拒绝的
        int NORESPONSE = 7;  // 未响应
        int TRANSPORT = 8;   // 建立连接失败
        int DIFFERENT = 9;   // 通讯协议不同
    }
}
