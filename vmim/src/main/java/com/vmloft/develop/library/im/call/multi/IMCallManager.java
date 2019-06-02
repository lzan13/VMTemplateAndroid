package com.vmloft.develop.library.im.call.multi;

import android.content.Context;
import android.media.AudioManager;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConference;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMStreamParam;
import com.superrtc.sdk.VideoView;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.chat.IMChatManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.common.IMSPManager;
import com.vmloft.develop.library.im.widget.IMCallView;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Create by lzan13 on 2019/5/9 10:46
 *
 * IM 实时音视频通话管理类
 */
public class IMCallManager {

    // 是否为会话创建者
    private boolean isCreator = false;

    // 通话类型
    private int mCallType = IMConstants.CallType.IM_SINGLE;
    // 通话状态
    private int mCallStatus = IMConstants.CallStatus.IM_IDLE;

    // 音频管理器
    private AudioManager mAudioManager;

    // 视频状态 默认为关闭
    private boolean isOpenVideo = true;
    // 语音状态 默认为关闭
    private boolean isOpenVoice = true;
    // 扬声器状态
    private boolean isOpenSpeaker = false;

    // 显示本地画面 View
    private IMCallView mLocalView;
    // 显示对方画面 View
    private IMCallView mRemoteView;

    /**
     * 私有的构造方法
     */
    private IMCallManager() {
    }

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
     * 初始化通话相关
     */
    public void init() {
        // 音频管理器
        mAudioManager = (AudioManager) IM.getInstance().getIMContext().getSystemService(Context.AUDIO_SERVICE);
        EMClient.getInstance().conferenceManager().addConferenceListener(new IMCallListener());
        if (IM.getInstance().isSignIn()) {
            initInfo();
        }
    }

    /**
     * 初始化创建会议需要的额信息
     */
    public void initInfo() {
        String token = EMClient.getInstance().getAccessToken();
        String appkey = EMClient.getInstance().getOptions().getAppKey();
        String username = IMSPManager.getInstance().getCurrUserId();
        EMClient.getInstance().conferenceManager().set(token, appkey, username);
    }

    /**
     * ----------------------------------------- 通话状态 -----------------------------------------
     */
    /**
     * 获取通话状态
     */
    public int getCallType() {
        return mCallType;
    }

    /**
     * 获取通话状态
     */
    public int getCallStatus() {
        return mCallStatus;
    }

    /**
     * 设置通话状态
     */
    public void setCallStatus(int status) {
        mCallStatus = status;
    }

    /**
     * 判断是否正在通话中
     */
    public boolean isCalling() {
        return mCallStatus == IMConstants.CallStatus.IM_CALL_OUT || mCallStatus == IMConstants.CallStatus.IM_INCOMING_CALL || mCallStatus == IMConstants.CallStatus.IM_CONNECT;
    }

    /**
     * 判断是否打开了摄像头
     */
    public boolean isOpenVideo() {
        return isOpenVideo;
    }

    /**
     * 判断是否打开了麦克风
     */
    public boolean isOpenVoice() {
        return isOpenVoice;
    }

    /**
     * ----------------------------------------- 通话操作 -----------------------------------------
     */
    /**
     * 视频传输状态
     */
    public void openVideo(boolean open) {
        isOpenVideo = open;
        if (open) {
            EMClient.getInstance().conferenceManager().openVideoTransfer();
        } else {
            EMClient.getInstance().conferenceManager().closeVideoTransfer();
        }
    }

    /**
     * 视频传输状态
     */
    public void openVoice(boolean open) {
        isOpenVoice = open;
        if (open) {
            EMClient.getInstance().conferenceManager().openVoiceTransfer();
        } else {
            EMClient.getInstance().conferenceManager().closeVoiceTransfer();
        }
    }

    /**
     * 开启扬声器
     */
    public void openSpeaker(boolean open) {
        isOpenSpeaker = open;
        /**
         * 打开扬声器
         * 主要是通过扬声器的开关以及设置音频播放模式来实现
         * 1、MODE_NORMAL：是正常模式，一般用于外放音频
         * 2、MODE_IN_CALL：
         * 3、MODE_IN_COMMUNICATION：这个和 CALL 都表示通讯模式，不过 CALL 在华为上不好使，故使用 COMMUNICATION
         * 4、MODE_RINGTONE：铃声模式
         */
        if (isOpenSpeaker) {
            // 检查是否已经开启扬声器
            if (!mAudioManager.isSpeakerphoneOn()) {
                // 打开扬声器
                mAudioManager.setSpeakerphoneOn(true);
            }
            // 开启了扬声器之后，因为是进行通话，声音的模式也要设置成通讯模式
            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        } else {
            // 检查是否已经开启扬声器
            if (mAudioManager.isSpeakerphoneOn()) {
                // 关闭扬声器
                mAudioManager.setSpeakerphoneOn(false);
            }
            // 设置声音模式为通讯模式
            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
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
        openSpeaker(true);
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
        openSpeaker(false);
    }

    /**
     * 切换摄像头
     */
    public void changeCamera() {
        EMClient.getInstance().conferenceManager().switchCamera();
    }

    /**
     * 设置展示本地画面 View
     */
    public void setLocalView(IMCallView localView) {
        mLocalView = localView;
        mLocalView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
        EMClient.getInstance().conferenceManager().setLocalSurfaceView(localView);
    }

    /**
     * 更新展示本地画面 View
     */
    public void updateLocalView(IMCallView localView) {
        mLocalView = localView;
        mLocalView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
        EMClient.getInstance().conferenceManager().updateLocalSurfaceView(localView);
    }

    /**
     * 设置对方展示画面 View
     */
    public void setRemoteView(IMCallView remoteView) {
        mRemoteView = remoteView;
        mRemoteView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
    }

    /**
     * 更新展示远端画面的 View
     */
    public void updateRemoteView(String streamId, IMCallView remoteView) {
        mRemoteView = remoteView;
        mRemoteView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
        EMClient.getInstance().conferenceManager().updateRemoteSurfaceView(streamId, remoteView);
    }

    /**
     * ----------------------------- 使用会议的功能实现单对单通话 -----------------------------
     */

    /**
     * 单对单呼叫通话
     */
    public void singleCall(final String id, final IMCallback callback) {
        setCallStatus(IMConstants.CallStatus.IM_CALL_OUT);
        isCreator = true;
        EMConferenceManager.EMConferenceType type = EMConferenceManager.EMConferenceType.SmallCommunication;
        EMClient.getInstance().conferenceManager().createAndJoinConference(type, "", new EMValueCallBack<EMConference>() {
            @Override
            public void onSuccess(EMConference conference) {
                // 返回当前会议对象实例 value, 可进行推流等相关操作m, 运行在子线程中，勿直接操作UI
                EMMessage msg = IMChatManager.getInstance().createActionMessage(IMConstants.IM_CHAT_ACTION_CALL, id);
                msg.setAttribute(IMConstants.IM_CHAT_CONFERENCE_ID, conference.getConferenceId());
                IMChatManager.getInstance().sendMessage(msg, new IMCallback<EMMessage>() {
                    @Override
                    public void onSuccess(EMMessage message) {
                        publishStream();
                    }

                    @Override
                    public void onError(int code, String desc) {
                        // 邀请失败销毁通话
                        EMClient.getInstance().conferenceManager().destroyConference(null);
                        if (callback != null) {
                            callback.onError(code, desc);
                        }
                    }
                });
            }

            @Override
            public void onError(int code, String desc) {
                // 运行在子线程中，勿直接操作UI
                if (callback != null) {
                    callback.onError(code, desc);
                }
            }
        });
    }

    /**
     * 接听通话
     */
    public void answerCall(String conferenceId, final IMCallback callback) {
        EMClient.getInstance().conferenceManager().joinConference(conferenceId, "", new EMValueCallBack<EMConference>() {
            @Override
            public void onSuccess(EMConference value) {
                setCallStatus(IMConstants.CallStatus.IM_CONNECT);
                // 返回当前会议对象实例 value, 可进行推流等相关操作, 运行在子线程中，勿直接操作UI
                if (callback != null) {
                    callback.onSuccess(value);
                }
            }

            @Override
            public void onError(int code, String desc) {
                // 运行在子线程中，勿直接操作UI
                if (callback != null) {
                    callback.onError(code, desc);
                }
            }
        });
    }

    /**
     * 结束通话
     */
    public void endCall(String id) {
        String action = "";
        if (mCallStatus == IMConstants.CallStatus.IM_INCOMING_CALL) {
            action = IMConstants.IM_CHAT_ACTION_CALL_REJECT;
            setCallStatus(IMConstants.CallStatus.IM_REJECT);
        } else if (mCallStatus == IMConstants.CallStatus.IM_CALL_OUT || mCallStatus == IMConstants.CallStatus.IM_CONNECT) {
            action = IMConstants.IM_CHAT_ACTION_CALL_END;
            setCallStatus(IMConstants.CallStatus.IM_END);
        } else if (mCallStatus == IMConstants.CallStatus.IM_BUSY) {
            setCallStatus(IMConstants.CallStatus.IM_END);
        }
        if (!VMStr.isEmpty(action)) {
            EMMessage msg = IMChatManager.getInstance().createActionMessage(action, id);
            IMChatManager.getInstance().sendMessage(msg, null);
        }
        // 退出会议
        EMClient.getInstance().conferenceManager().exitConference(null);
        // 挂断后，销毁会议，只有创建者可以销毁，接受邀请方不可以
        if (isCreator) {
            EMClient.getInstance().conferenceManager().destroyConference(null);
        }
    }

    /**
     * 忙碌通话，这里只是简单的发送忙碌消息告诉对方
     */
    public void busyCall(String id) {
        EMMessage msg = IMChatManager.getInstance().createActionMessage(IMConstants.IM_CHAT_ACTION_CALL_BUSY, id);
        IMChatManager.getInstance().sendMessage(msg, null);
    }

    /**
     * 推送自己本地流数据
     */
    public void publishStream() {
        // 邀请成功，开始推流
        EMStreamParam param = new EMStreamParam();
        param.setVideoOff(!isOpenVideo);
        param.setAudioOff(!isOpenVoice);
        param.setVideoWidth(480);
        param.setVideoHeight(640);
        param.setEnableFixedVideoResolution(true);
        EMClient.getInstance().conferenceManager().publish(param, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                VMLog.d("推送本地流成功");
            }

            @Override
            public void onError(int code, String desc) {
                VMLog.d("推送本地流失败 %d - %s", code, desc);
            }
        });
    }

    /**
     * 订阅通话流数据
     *
     * @param stream 流数据
     */
    public void subscribeStream(EMConferenceStream stream) {
        EMClient.getInstance().conferenceManager().subscribe(stream, mRemoteView, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                VMLog.d("订阅远程流成功");
            }

            @Override
            public void onError(int code, String desc) {
                VMLog.d("订阅远程流失败 %d - %s", code, desc);
            }
        });
    }
}

