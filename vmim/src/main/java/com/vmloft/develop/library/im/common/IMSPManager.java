package com.vmloft.develop.library.im.common;

import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMSPUtil;

/**
 * Create by lzan13 on 2019/05/21
 *
 * SharedPreferences 配置管理类
 */
public class IMSPManager {

    private final String IM_CURR_USER_ID = "im_curr_user_id";
    private final String IM_KEYBOARD_HEIGHT = "key_keyboard_height";
    // 通知开关
    private final String IM_NOTIFY = "im_notify";
    private final String IM_NOTIFY_DETAIL = "im_notify_detail";

    // 聊天开关
    // 圆形头像
    private final String IM_CIRCLE_AVATAR = "im_circle_avatar";
    // 麦克风播放语音
    private final String IM_SPEAKER_VOICE = "im_speaker_voice";

    /**
     * 私有构造，初始化 ShredPreferences 文件名
     */
    private IMSPManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        private static final IMSPManager INSTANCE = new IMSPManager();
    }

    /**
     * 获取的实例
     */
    public static final IMSPManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 保存当前登录账户 Id
     *
     * @param userId 当前账户 Id
     */
    public void putCurrUserId(String userId) {
        VMSPUtil.put(IM_CURR_USER_ID, userId);
    }

    /**
     * 获取当前登录账户 Id
     *
     * @return 如果为空，说明没有登录
     */
    public String getCurrUserId() {
        return (String) VMSPUtil.get(IM_CURR_USER_ID, "");
    }

    /**
     * 保存键盘高度
     */
    public void putKeyboardHeight(int height) {
        VMSPUtil.put(IM_KEYBOARD_HEIGHT, height);
    }

    /**
     * 获取键盘高度
     */
    public int getKeyboardHeight() {
        return (int) VMSPUtil.get(IM_KEYBOARD_HEIGHT, VMDimen.dp2px(256));
    }

    /**
     * ---------------------------------------------------------------------------------
     * 设置是否启用圆形头像
     */
    public void putCircleAvatar(boolean circle) {
        VMSPUtil.put(IM_CIRCLE_AVATAR, circle);
    }

    /**
     * 是否启用圆形头像
     */
    public boolean getCircleAvatar() {
        return (boolean) VMSPUtil.get(IM_CIRCLE_AVATAR, true);
    }

    /**
     * 设置是否扬声器播放语音
     */
    public void putSpeakerVoice(boolean speaker) {
        VMSPUtil.put(IM_SPEAKER_VOICE, speaker);
    }

    /**
     * 是否扬声器播放语音
     */
    public boolean getSpeakerVoice() {
        return (boolean) VMSPUtil.get(IM_SPEAKER_VOICE, false);
    }

    /**
     * ---------------------------------------------------------------------------------
     * 保存通知栏提醒状态
     */
    public void putNotify(boolean status) {
        VMSPUtil.put(IM_NOTIFY, status);
    }

    /**
     * 获取通知栏提醒状态
     */
    public boolean getNotify() {
        return (boolean) VMSPUtil.get(IM_NOTIFY, true);
    }

    /**
     * 保存通知栏提醒状态
     */
    public void putNotifyDetail(boolean status) {
        VMSPUtil.put(IM_NOTIFY_DETAIL, status);
    }

    /**
     * 获取通知栏提醒状态
     */
    public boolean getNotifyDetail() {
        return (boolean) VMSPUtil.get(IM_NOTIFY_DETAIL, true);
    }
}
