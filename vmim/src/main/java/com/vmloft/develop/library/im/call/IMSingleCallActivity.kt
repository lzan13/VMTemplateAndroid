package com.vmloft.develop.library.im.call

import android.view.View

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.common.base.BaseActivity
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMUser
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.logger.VMLog

import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine

import kotlinx.android.synthetic.main.im_activity_single_call.*


/**
 * Create on lzan13 on 2021/05/23 10:10
 *
 * 描述：1V1通话界面
 */
@Route(path = IMRouter.imSingleCall)
class IMSingleCallActivity : BaseActivity() {

    @Autowired
    lateinit var callId: String

    @JvmField
    @Autowired
    var isInComingCall: Boolean = false

    // 声网通话引擎
    lateinit var rtcEngine: RtcEngine
    var lastEffectView: View? = null

    lateinit var token: String // 加入频道所需 token
    lateinit var channel: String // 频道名

    // 通话对方用户信息
    lateinit var user: IMUser

    override fun layoutId() = R.layout.im_activity_single_call

    override fun initUI() {
        super.initUI()

        initRtcEngine()

        // 变声彩蛋
        imCallMicBtn.setOnLongClickListener {
            imCallVoiceEffectLL.visibility = View.VISIBLE
            true
        }
        // 设置人声变声效果
        imCallVoiceEffectIV0.setOnClickListener { view -> setVoiceEffect(view, 0) }
        imCallVoiceEffectIV1.setOnClickListener { view -> setVoiceEffect(view, 1) }
        imCallVoiceEffectIV2.setOnClickListener { view -> setVoiceEffect(view, 2) }
        imCallVoiceEffectIV3.setOnClickListener { view -> setVoiceEffect(view, 3) }
        imCallVoiceEffectIV4.setOnClickListener { view -> setVoiceEffect(view, 4) }

        // 设置麦克风点击事件
        imCallMicBtn.setOnClickListener {
            imCallMicBtn.isSelected = !imCallMicBtn.isSelected
            IMCallManager.instance.setMuteEnable(rtcEngine, !imCallMicBtn.isSelected)
        }
        // 设置扬声器点击事件
        imCallSpeakerBtn.setOnClickListener {
            imCallSpeakerBtn.isSelected = !imCallSpeakerBtn.isSelected
            IMCallManager.instance.setMuteEnable(rtcEngine, imCallSpeakerBtn.isSelected)
        }

        // 自己点击接听，然后加入频道
        imCallAnswerBtn.setOnClickListener {
            imCallAnswerBtn.visibility = View.GONE
            imCallStatusTV.setText(R.string.im_call_accepted)
            IMCallManager.instance.sendCallSignal(callId, IMConstants.Call.callStatusAgree)
            joinChannel()
        }
        // 挂断按钮，根据通话状态调用不同操作
        imCallEndBtn.setOnClickListener {
            if (IMCallManager.instance.callStatus == IMConstants.Call.callStatusApply) {
                IMCallManager.instance.sendCallSignal(callId, IMConstants.Call.callStatusReject)
                finish()
            } else {
                IMCallManager.instance.sendCallSignal(callId, IMConstants.Call.callStatusEnd)
                IMCallManager.instance.exitChannel(rtcEngine)
                finish()
            }
        }

        // 默认开启麦克风和扬声器
        imCallMicBtn.isSelected = !IMCallManager.instance.isMute
        imCallSpeakerBtn.isSelected = IMCallManager.instance.isSpeaker

        IMCallManager.instance.setMuteEnable(rtcEngine, !imCallMicBtn.isSelected)
        IMCallManager.instance.setSpeakerEnable(rtcEngine, imCallSpeakerBtn.isSelected)

        // 监听通话信令
        LDEventBus.observe(this, IMConstants.Call.cmdCallStatusEvent, EMMessage::class.java) {
            val status = it.getIntAttribute(IMConstants.Call.msgAttrCallStatus)
            if (status == IMConstants.Call.callStatusAgree) {
                imCallStatusTV.setText(R.string.im_call_accepted)
                // 对方接听后，这边同步加入频道
                joinChannel()
            } else if (status == IMConstants.Call.callStatusReject || status == IMConstants.Call.callStatusBusy) {
                finish()
            } else if (status == IMConstants.Call.callStatusEnd) {
                IMCallManager.instance.exitChannel(rtcEngine)
                finish()
            }
        }
        LDEventBus.observe(this, IMConstants.Call.callTimeEvent, Int::class.java) {
            imCallTimeTV.text = IMCallManager.instance.getCallTime()
        }
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        user = IM.imListener.getUser(callId) ?: IMUser(callId)

        // TODO 开发测试阶段，token 为空，或者设置临时 token
        token = ""

        channel = if (isInComingCall) {
            callId + IM.instance.getSelfId()
        } else {
            IM.instance.getSelfId() + callId
        }

        bindInfo()

        if (!isInComingCall) {
            IMCallManager.instance.sendCallSignal(callId, 0)
        }
    }

    /**
     * 绑定信息
     */
    private fun bindInfo() {
        // 加载用户信息
        IMGLoader.loadCover(imCallCoverIV, user.avatar, isBlur = true)
        IMGLoader.loadAvatar(imCallAvatarIV, user.avatar)
        imCallNameTV.text = if (user.nickname.isNullOrEmpty()) "小透明" else user.nickname

        imCallAnswerBtn.visibility = if (isInComingCall) View.VISIBLE else View.GONE
        if (isInComingCall) {
            imCallStatusTV.setText(R.string.im_call_incoming)
        } else {
            imCallStatusTV.setText(R.string.im_call_out_wait)
        }
    }

    /**
     * --------------------------------------------------------------------
     * 声网 SDK 操作相关
     */
    private fun initRtcEngine() {
        try {
            rtcEngine = RtcEngine.create(IM.instance.imContext, IMConstants.agoraAppId(), object : IRtcEngineEventHandler() {})
            // 这里设置成通话场景
            rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
            // 设置采样率和音频通话场景
            rtcEngine.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY, Constants.AUDIO_SCENARIO_GAME_STREAMING)
        } catch (e: Exception) {
            VMLog.e("rtc engline init error ${e.message}")
        }
    }

    /**
     * 设置声音音效
     */
    private fun setVoiceEffect(view: View, effect: Int) {
        lastEffectView?.isSelected = false
        lastEffectView = view
        lastEffectView?.isSelected = true
        // VOICE_CHANGER_EFFECT_UNCLE 中年
        // VOICE_CHANGER_EFFECT_OLDMAN 老年
        // VOICE_CHANGER_EFFECT_BOY 男孩
        // VOICE_CHANGER_EFFECT_SISTER 少女
        // VOICE_CHANGER_EFFECT_GIRL 女孩
        // VOICE_CHANGER_EFFECT_PIGKING 八戒
        // VOICE_CHANGER_EFFECT_HULK 绿巨人
        when (effect) {
            0 -> rtcEngine.setAudioEffectPreset(Constants.VOICE_CHANGER_EFFECT_OLDMAN)
            1 -> rtcEngine.setAudioEffectPreset(Constants.VOICE_CHANGER_EFFECT_UNCLE)
            2 -> rtcEngine.setAudioEffectPreset(Constants.VOICE_CHANGER_EFFECT_BOY)
            3 -> rtcEngine.setAudioEffectPreset(Constants.VOICE_CHANGER_EFFECT_GIRL)
            4 -> rtcEngine.setAudioEffectPreset(Constants.VOICE_CHANGER_EFFECT_SISTER)
            else -> rtcEngine.setAudioEffectPreset(Constants.AUDIO_EFFECT_OFF)
        }
    }

    /**
     * 加入通话
     */
    private fun joinChannel() {
        IMCallManager.instance.joinChannel(rtcEngine, token, channel)
    }

    override fun onDestroy() {
        RtcEngine.destroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        showBar(R.string.im_calling_no_back)
    }
}