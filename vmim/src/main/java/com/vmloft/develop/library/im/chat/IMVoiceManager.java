package com.vmloft.develop.library.im.chat;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.chat.msgitem.IMVoiceMsgItem;

import com.vmloft.develop.library.tools.utils.VMStr;
import java.io.IOException;

/**
 * Created by lzan13 on 2019/06/07 16:29
 *
 * 音频播放管理类
 */
public class IMVoiceManager {

    // 播放器状态
    private final int IDLE = 0; // 空闲
    private final int PAUSE = 1; // 暂停
    private final int PLAYING = 2; // 播放中

    // 音频管理器
    private AudioManager mAudioManager;

    // 媒体播放器
    private MediaPlayer mMediaPlayer;

    private IMVoiceMsgItem mCurrItem;
    private IMVoiceMsgItem mOldItem;

    // 是否有音频正在播放中
    private int mStatus = IDLE;
    // 当前正在播放的消息ID
    private String mOldMsgId;
    private String mCurrMsgId;

    // 当前播放文件
    private String mCurrFile;

    /**
     * 私有构造方法
     */
    private IMVoiceManager() {
        mAudioManager = (AudioManager) IM.getInstance().getIMContext().getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMVoiceManager INSTANCE = new IMVoiceManager();
    }

    /**
     * 获取单例类的实例方法
     *
     * @return
     */
    public static IMVoiceManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 播放语音消息
     */
    public void onPlayMessage(EMMessage message, IMVoiceMsgItem item) {
        // 检查语音消息的 ACK
        checkVoiceACK(message);
        // 播放前先检查语音播放模式
        checkVoiceMode();

        if (mCurrItem != null) {
            mOldItem = mCurrItem;
        } else {
            mOldItem = item;
        }

        if (VMStr.isEmpty(mOldMsgId)) {
            mOldMsgId = message.getMsgId();
        } else {
            mOldMsgId = mCurrMsgId;
        }
        mCurrMsgId = message.getMsgId();

        mCurrItem = item;

        EMVoiceMessageBody body = (EMVoiceMessageBody) message.getBody();
        mCurrFile = body.getLocalUrl();

        if (mStatus == IDLE) {
            // 如果正常，直接开始播放
            start();
            mCurrItem.checkVoiceStatus();
        } else if (mStatus == PAUSE) {
            if (mCurrMsgId.equals(mOldMsgId)) {
                // 从暂停状态恢复
                resume();
                mCurrItem.checkVoiceStatus();
            } else {
                // 将之前暂停的停止，开始播放新的
                stop();
                mOldItem.checkVoiceStatus();
                start();
                mCurrItem.checkVoiceStatus();
            }
        } else if (mStatus == PLAYING) {
            if (mCurrMsgId.equals(mOldMsgId)) {
                // 点击的为正在播放的Item时 暂停播放
                pause();
                mCurrItem.checkVoiceStatus();
            } else {
                // 点击的不是当前播放的，停止播放之前的，播放新点击的
                stop();
                mOldItem.checkVoiceStatus();
                start();
                mCurrItem.checkVoiceStatus();
            }
        }
    }

    /**
     * 检查语音消息的 ACK
     */
    private void checkVoiceACK(EMMessage message) {
        // 如果是没有听过的语音就设置已听，并发送ACK
        if (!message.isListened()) {
            EMClient.getInstance().chatManager().setVoiceMessageListened(message);
            // 接收方发送已读 ACK
            IMChatManager.getInstance().sendReadACK(message);
        }
    }

    /**
     * 最终播放操作
     */
    private void start() {
        try {
            /**
             * 通过 new() 的方式实例化 MediaPlayer ，也可以调用 create 方法，不过 create 必须传递Uri指向的文件
             * 这里为了方便可以直接根据文件地址加载，使用 setDataSource() 的方法
             *
             * 当使用 new() 或者调用 reset() 方法时 MediaPlayer 会进入 Idle 状态
             * 这两种方法的一个重要差别就是：如果在这个状态下调用了getDuration()等方法（相当于调用时机不正确），
             * 通过reset()方法进入idle状态的话会触发OnErrorListener.onFailed()，并且MediaPlayer会进入Error状态；
             * 如果是新创建的MediaPlayer对象，则并不会触发onError(),也不会进入Error状态；
             */
            mMediaPlayer = new MediaPlayer();
            // 设置数据源，即要播放的音频文件，MediaPlayer 进入 Initialized 状态，必须在 Idle 状态下调用
            mMediaPlayer.setDataSource(mCurrFile);
            // 准备 MediaPlayer 进入 Prepared 状态
            mMediaPlayer.prepare();
            // 设置是否循环播放 默认为 false，必须在 Prepared 状态下调用
            mMediaPlayer.setLooping(false);

            // 初始化设置 Visualizer
            mCurrItem.getVisualizerView().setAudioSessionId(mMediaPlayer.getAudioSessionId());

            // 开始播放状态将变为 Started，必须在 Prepared 状态下进行
            mMediaPlayer.start();

            // 设置当前状态为播放中
            mStatus = PLAYING;

            // 媒体播放结束监听
            mMediaPlayer.setOnCompletionListener((MediaPlayer player) -> {
                // 调用停止播放方法，主要是为了释放资源
                stop();
                mCurrItem.checkVoiceStatus();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放，并释放资源
     */
    public void stop() {
        // 释放 MediaPlayer
        if (mMediaPlayer != null) {
            // 停止播放
            mMediaPlayer.stop();
            // 释放 MediaPlayer
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        // 设置当前状态为正常
        mStatus = IDLE;
    }

    /**
     * 暂停播放
     */
    private void pause() {
        if (mMediaPlayer != null) {
            // 暂停播放
            mMediaPlayer.pause();
            // 设置当前状态为暂停中
            mStatus = PAUSE;
        } else {
            // 设置当前状态为正常
            mStatus = IDLE;
        }
    }

    /**
     * 继续播放，从暂停状态恢复
     */
    private void resume() {
        if (mMediaPlayer != null) {
            // 当处于暂停状态时，直接调用 start 开始播放，否则重新开始加载音频文件播放
            mMediaPlayer.start();
            // 设置当前状态为播放中
            mStatus = PLAYING;
        } else {
            // 设置当前状态为正常
            mStatus = IDLE;
        }
    }

    /**
     * 获取当前是否播放中
     */
    public boolean isPlaying(EMMessage message) {
        if (mStatus == PLAYING && !VMStr.isEmpty(mCurrMsgId) && mCurrMsgId.equals(message.getMsgId())) {
            return true;
        }
        return false;
    }

    /**
     * 检查语音播放模式
     */
    public void checkVoiceMode() {
        if (IM.getInstance().isSpeakerVoice()) {
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
        // 开启了扬声器之后，因为这里是播放语音，声音的模式就设置为正常模式
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
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
        // 设置声音模式为正常模式
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }
}
