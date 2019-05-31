package com.vmloft.develop.library.im.call;

import android.annotation.TargetApi;
import android.os.Build;
import com.hyphenate.EMConferenceListener;
import com.hyphenate.chat.EMConferenceAttribute;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMConferenceMember;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMStreamStatistics;

import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.utils.VMLog;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/30 19:33
 *
 * 实现会议监听接口
 */
public class IMCallListener implements EMConferenceListener {
    /**
     * 成员加入会议
     *
     * @param member 会议成员
     */
    @Override
    public void onMemberJoined(EMConferenceMember member) {
        if (IMCallManager.getInstance().getCallType() == IMConstants.CallType.IM_SINGLE) {
            IMCallManager.getInstance().setCallStatus(IMConstants.CallStatus.IM_CONNECT);
        } else {
            // TODO 多人通话有其他操作，
        }
    }

    /**
     * 成员离开会议
     *
     * @param member 会议成员
     */
    @Override
    public void onMemberExited(EMConferenceMember member) {
        if (IMCallManager.getInstance().getCallType() == IMConstants.CallType.IM_SINGLE) {
            IMCallManager.getInstance().setCallStatus(IMConstants.CallStatus.IM_END);
        } else {
            // TODO 多人通话进行其他操作
        }
    }

    /**
     * 有新的成员推流
     *
     * @param stream
     */
    @Override
    public void onStreamAdded(EMConferenceStream stream) {
        IMCallManager.getInstance().subscribeStream(stream);
    }

    /**
     * 成员停止推流
     *
     * @param stream
     */
    @Override
    public void onStreamRemoved(EMConferenceStream stream) {

    }

    /**
     * 有成员更新自己的推流，比如打开摄像头，静音等操作
     *
     * @param stream
     */
    @Override
    public void onStreamUpdate(EMConferenceStream stream) {

    }

    /**
     * 被动离开会议
     *
     * @param error
     * @param message
     */
    @Override
    public void onPassiveLeave(int error, String message) {

    }

    /**
     * 会议状态通知回调
     *
     * @param state
     */
    @Override
    public void onConferenceState(ConferenceState state) {
        VMLog.d("call state " + state);
    }

    /**
     * 统计信息回调
     *
     * @param statistics
     */
    @Override
    public void onStreamStatistics(EMStreamStatistics statistics) {

    }

    /**
     * 推本地流 或 订阅成员流 成功回调
     *
     * @param streamId 本地流 或 成员流ID
     */
    @Override
    public void onStreamSetup(String streamId) {

    }

    /**
     * 当前说话者回调
     *
     * @param speakers 当前说话的Stream id 集合
     */
    @Override
    public void onSpeakers(List<String> speakers) {

    }

    /**
     * 收到会议邀请
     *
     * @param confId    会议 id
     * @param password  会议密码
     * @param extension 邀请扩展内容
     */
    @Override
    public void onReceiveInvite(String confId, String password, String extension) {
        // TODO 基本不用这个，建议使用发送消息的方式邀请他人
    }

    /**
     * 用于直播模式。当前登录用户角色被管理员改变
     *
     * @param role 改变后的角色，可参考{@link EMConferenceManager.EMConferenceRole}
     */
    @Override
    public void onRoleChanged(EMConferenceManager.EMConferenceRole role) {

    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onAttributesUpdated(EMConferenceAttribute[] attributes) {

    }
}
