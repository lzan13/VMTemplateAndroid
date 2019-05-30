package com.vmloft.develop.library.im.call;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConference;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMStreamParam;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.common.IMChatManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.widget.IMCallView;
import com.vmloft.develop.library.tools.utils.VMLog;

/**
 * Create by lzan13 on 2019/5/9 10:46
 *
 * IM 实时音视频通话管理类
 */
public class IMCallManager {

    // 视频状态 默认为关闭
    private boolean isOpenVideo = false;
    // 语音状态 默认为关闭
    private boolean isOpenVoice = false;
    // 是否正在通话中
    private boolean isCalling = false;
    // 会话创建者
    private boolean isCreator = false;

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
        EMClient.getInstance().conferenceManager().addConferenceListener(new IMCallListener());
    }

    /**
     * ----------------------------------------- 通话状态 -----------------------------------------
     */
    /**
     * 判断是否正在通话中
     */
    public boolean isCalling() {
        return isCalling;
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
    public void setVideoStatus(boolean open) {
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
    public void setVoiceStatus(boolean open) {
        isOpenVoice = open;
        if (open) {
            EMClient.getInstance().conferenceManager().openVoiceTransfer();
        } else {
            EMClient.getInstance().conferenceManager().closeVoiceTransfer();
        }
    }

    /**
     * 切换摄像头
     */
    public void changeCamera() {
        EMClient.getInstance().conferenceManager().switchCamera();
    }

    /**
     * 设置展示本地画面 View
     *
     * @param localView
     */
    public void setLocalView(IMCallView localView) {
        EMClient.getInstance().conferenceManager().setLocalSurfaceView(localView);
    }

    /**
     * 更新展示本地画面 View
     *
     * @param localView
     */
    public void updateLocalView(IMCallView localView) {
        EMClient.getInstance().conferenceManager().updateLocalSurfaceView(localView);
    }

    /**
     * 更新展示远端画面的 View
     *
     * @param streamId
     * @param remoteView
     */
    public void updateRemoteView(String streamId, IMCallView remoteView) {
        EMClient.getInstance().conferenceManager().updateRemoteSurfaceView(streamId, remoteView);
    }

    /**
     * ----------------------------- 使用会议的功能实现单对单通话 -----------------------------
     */

    /**
     * 通话 呼叫
     */
    public void callSingle(final String id, final IMCallback callback) {
        isCalling = true;
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
     * 通话 接听
     */
    public void callAnswer(String conferenceId, final IMCallback callback) {
        EMClient.getInstance().conferenceManager().joinConference(conferenceId, "", new EMValueCallBack<EMConference>() {
            @Override
            public void onSuccess(EMConference value) {
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
     * 通话 结束
     */
    public void callEnd(String id, IMCallback<EMMessage> callback) {
        EMMessage msg = IMChatManager.getInstance().createActionMessage(IMConstants.IM_CHAT_ACTION_CALL_END, id);
        IMChatManager.getInstance().sendMessage(msg, callback);
        // 挂断后，销毁会议，只有创建者可以销毁，接受邀请方不可以
        if (isCreator) {
            EMClient.getInstance().conferenceManager().destroyConference(null);
        }
        isCalling = false;
    }

    /**
     * 通话 拒绝
     */
    public void callReject(String id, IMCallback<EMMessage> callback) {
        EMMessage msg = IMChatManager.getInstance().createActionMessage(IMConstants.IM_CHAT_ACTION_CALL_REJECT, id);
        IMChatManager.getInstance().sendMessage(msg, callback);
        isCalling = false;
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
     * @param stream   流数据
     * @param callView 显示画面 View
     */
    public void subscribeStream(EMConferenceStream stream, IMCallView callView) {
        EMClient.getInstance().conferenceManager().subscribe(stream, callView, new EMValueCallBack<String>() {
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

