package com.vmloft.develop.app.match.im;

import android.content.Context;

import com.vmloft.develop.library.im.IM;

/**
 * Create by lzan13 on 2019/5/23 09:39
 */
public class AIMManager {

    private AIMManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        private static final AIMManager INSTANCE = new AIMManager();
    }

    /**
     * 获取的实例
     */
    public static final AIMManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 初始化 IM
     */
    public void initIM(Context context) {
        IM.getInstance().init(context, new AIMGlobalListener(), new AIMPictureLoader());
    }

    /**
     * ---------------------------------------------------------------------
     * 设置开启通知
     */
    public void setNotify(boolean open) {
        IM.getInstance().setNotify(open);
    }

    /**
     * 判断是否开启通知
     */
    public boolean isNotify() {
        return IM.getInstance().isNotify();
    }

    /**
     * 设置开启通知
     */
    public void setNotifyDetail(boolean open) {
        IM.getInstance().setNotifyDetail(open);
    }

    /**
     * 判断是否开启通知
     */
    public boolean isNotifyDetail() {
        return IM.getInstance().isNotifyDetail();
    }

    /**
     * ---------------------------------------------------------------------
     * 判断是否开启圆形头像
     */
    public boolean isCircleAvatar() {
        return IM.getInstance().isCircleAvatar();
    }

    /**
     * 设置开启圆形头像
     */
    public void setCircleAvatar(boolean open) {
        IM.getInstance().setCircleAvatar(open);
    }

    /**
     * 判断是否扬声器播放语音
     */
    public boolean isSpeakerVoice() {
        return IM.getInstance().isSpeakerVoice();
    }

    /**
     * 设置是否扬声器播放语音
     */
    public void setSpeakerVoice(boolean speaker) {
        IM.getInstance().setSpeakerVoice(speaker);
    }
}
