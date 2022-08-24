package com.vmloft.develop.library.im.call

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool

import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.common.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.common.IMConversationManager
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.VMTools
import com.vmloft.develop.library.tools.utils.logger.VMLog

import io.agora.rtc.RtcEngine

import java.util.*


/**
 * Create by lzan13 on 2021/5/23 09:57
 * 描述：通化管理类
 */
object IMCallManager {

    // 声音管理器
    private lateinit var audioManager: AudioManager

    // 音频池
    private lateinit var soundPool: SoundPool

    // 资源相关
    private var loadResId: Int = 0
    private var streamId: Int = 0

    // 通话界面状态
    var isMute: Boolean = false
    var isSpeaker: Boolean = true

    // 是否通话中
    val isCalling: Boolean
        get() = callStatus == IMConstants.Call.callStatusApply || callStatus == IMConstants.Call.callStatusAgree

    // 是否呼入通话
    var isInComingCall = false
    var callStatus = -1 // 通话状态

    // 计时器，记录通话时间
    private var timer: Timer? = null
    private var callTime = 0


    /**
     * 初始化
     */
    fun init(context: Context) {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // 设置音频要用在什么地方，这里选择电话通知铃音
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        // 当系统的 SDK 版本高于21时，使用 build 的方式实例化 SoundPool
        soundPool = SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(1).build()
    }

    /**
     * ----------------------------------------------------------------
     * 通话信令处理
     */
    /**
     * 发送通话信号，发送通话信令状态 类型查看[IMConstants.Call]
     */
    fun sendCallSignal(callId: String, status: Int = IMConstants.Call.callStatusApply) {
        // 记录下通话状态，为后续通话界面最小化做准备
        callStatus = status
        if (status == IMConstants.Call.callStatusApply) {
            isInComingCall = false
        } else if (status == IMConstants.Call.callStatusAgree) {
            stopPlaySound()
            startCallTime()
        } else if (status == IMConstants.Call.callStatusReject) {
            stopPlaySound()
            saveCallMessage(callId)
        } else if (status == IMConstants.Call.callStatusBusy) {
            saveCallMessage(callId)
        } else {
            saveCallMessage(callId)
            stopCallTime()
        }
        IMChatManager.sendCallSignal(callId, status)
    }

    /**
     * 收到通话信号 类型查看[IMConstants.Call]
     */
    fun receiveCallSignal(callId: String, status: Int) {
        // 收到申请时需要判断下是否忙碌
        if (status == IMConstants.Call.callStatusApply) {
            if (callStatus == IMConstants.Call.callStatusApply || callStatus == IMConstants.Call.callStatusAgree) {
                VMLog.i("忙碌中")
                sendCallSignal(callId, IMConstants.Call.callStatusBusy)
                return
            } else {
                isInComingCall = true
                IMRouter.goSingleCall(callId, true)
            }
        }
        // 记录下通话状态，为后续通话界面最小化做准备
        callStatus = status

        if (status == IMConstants.Call.callStatusAgree) {
            stopPlaySound()
            startCallTime()
        } else if (status == IMConstants.Call.callStatusReject || status == IMConstants.Call.callStatusBusy) {
            stopPlaySound()
            saveCallMessage(callId)
        } else if (status == IMConstants.Call.callStatusEnd) {
            saveCallMessage(callId)
            stopCallTime()
        }
    }

    /**
     * 保存通话消息
     */
    private fun saveCallMessage(chatId: String) {
        var content = when (callStatus) {
            IMConstants.Call.callStatusReject -> "通话取消"
            IMConstants.Call.callStatusBusy -> "对方忙碌"
            else -> "通话结束 " + getCallTime()
        }
        val message = IMChatManager.createTextMessage(chatId, content, !isInComingCall)
        message.setAttribute(IMConstants.Common.extType, IMConstants.MsgType.imCall)
        message.setAttribute(IMConstants.Call.extCallType, IMConstants.Call.callTypeVoice)

        IMConversationManager.addMessage(message)
    }

    /**
     * ---------------------------------------------------------------------------------
     * 开始通话计时，这里在全局管理器中开启一个定时器进行计时，可以做到最小化，以及后台时进行计时
     */
    private fun startCallTime() {
        timer = Timer()
        timer?.purge()
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                callTime++
                LDEventBus.post(IMConstants.Call.callTimeEvent, callTime)
            }
        }
        timer?.scheduleAtFixedRate(task, 1000, 1000)
    }

    /**
     * 获取通话时间
     */
    fun getCallTime(): String {
        val t: Int = callTime
        val h = t / 60 / 60
        val m = t / 60 % 60
        val s = t % 60 % 60
        var time = when {
            h > 9 -> "$h:"
            h > 1 -> "0$h:"
            else -> ""
        }
        time += if (m > 9) {
            "$m:"
        } else {
            "0$m:"
        }
        time += if (s > 9) {
            "$s"
        } else {
            "0$s"
        }
        return time
    }

    /**
     * 停止计时
     */
    fun stopCallTime() {
        timer?.purge()
        timer?.cancel()
        timer = null

        callStatus = IMConstants.Call.callStatusEnd
        callTime = 0
    }

    /**
     * --------------------------------------------------------------------
     * 声网 SDK 操作相关
     */
    /**
     * 加入通话
     */
    fun joinChannel(rtcEngine: RtcEngine, token: String, channel: String, id: Int = 0, extend: String = "") {
        rtcEngine.joinChannel(token, channel, extend, id)
    }

    /**
     * 退出通话
     */
    fun exitChannel(rtcEngine: RtcEngine) {
        rtcEngine.leaveChannel()
    }

    /**
     * 设置扬声器开启状态
     */
    fun setSpeakerEnable(rtcEngine: RtcEngine, enabled: Boolean) {
        isSpeaker = enabled
        rtcEngine.setEnableSpeakerphone(enabled)
    }

    /**
     * 设置静音开启状态
     */
    fun setMuteEnable(rtcEngine: RtcEngine, enabled: Boolean) {
        isMute = enabled
        rtcEngine.muteLocalAudioStream(enabled)
    }


    /**
     * -------------------------------------------------------------------------------
     * 尝试播放呼叫通话提示音
     */
    fun attemptPlaySound(isInComingCall: Boolean) {
        // 检查音频资源是否已经加载完毕
        if (loadResId == 0) {
            startPlaySound()
        } else {
            // 设置资源加载监听，也因为加载资源在单独的进程，需要时间，所以等监听到加载完成才能播放
            soundPool.setOnLoadCompleteListener { _, _, _ ->
                // 首次监听到加载完毕，开始播放音频
                startPlaySound()
            }
            loadResId = if (isInComingCall) {
                soundPool.load(VMTools.context, R.raw.im_incoming_call, 1)
            } else {
                soundPool.load(VMTools.context, R.raw.im_call_out, 1)
            }
        }
    }

    /**
     * 播放音频
     */
    private fun startPlaySound() {
        // 打开扬声器
        audioManager.isSpeakerphoneOn = true

        /**
         * 设置扬声器状态
         * 主要是通过扬声器的开关以及设置音频播放模式来实现
         * 1、MODE_NORMAL：是正常模式，一般用于外放音频
         * 2、MODE_IN_CALL：
         * 3、MODE_IN_COMMUNICATION：这个和 CALL 都表示通讯模式，不过 CALL 在华为上不好使，故使用 COMMUNICATION
         * 4、MODE_RINGTONE：铃声模式
         */
        // 设置音频管理器音频模式为铃音模式
        audioManager.mode = AudioManager.MODE_RINGTONE

        // 播放提示音，返回一个播放的音频id，等下停止播放需要用到
        streamId = soundPool.play(
            loadResId,  // 播放资源id；就是加载到SoundPool里的音频资源顺序
            0.5f,  // 左声道音量
            0.5f,  // 右声道音量
            1,  // 优先级，数值越高，优先级越大
            -1,  // 是否循环；0 不循环，-1 循环，N 表示循环次数
            1.0f // 播放速率；从0.5-2，一般设置为1，表示正常播放
        )
    }

    /**
     * 关闭音效的播放，并释放资源
     */
    private fun stopPlaySound() {
        // 关闭扬声器
        audioManager.isSpeakerphoneOn = false
        // 设置声音模式为正常模式
        audioManager.mode = AudioManager.MODE_NORMAL

        // 停止播放音效
        soundPool.stop(streamId)
        // 卸载音效
        //soundPool.unload(loadId);
        // 释放资源
        //soundPool.release();
    }

}