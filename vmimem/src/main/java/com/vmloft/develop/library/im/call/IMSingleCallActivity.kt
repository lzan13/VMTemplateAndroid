package com.vmloft.develop.library.im.call

import android.view.View
import androidx.core.view.isVisible

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImActivitySingleCallBinding
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.logger.VMLog

import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine

/**
 * Create on lzan13 on 2021/05/23 10:10
 * 描述：1V1通话界面
 */
@Route(path = IMRouter.imSingleCall)
class IMSingleCallActivity: BActivity<ImActivitySingleCallBinding>() {

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
    lateinit var user: User

    override fun initVB() = ImActivitySingleCallBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

        initRtcEngine()

        // 魔音变声
        mBinding.imCallMicMagicBtn.setOnClickListener {
            if (mBinding.imCallMagicVoiceLL.isVisible) {
                mBinding.imCallMagicVoiceLL.visibility = View.GONE
            } else {
                mBinding.imCallMagicVoiceLL.visibility = View.VISIBLE
            }
        }
        // 设置人声变声效果
        mBinding.imCallMagicVoiceIV0.setOnClickListener { view -> setVoiceEffect(view, 0) }
        mBinding.imCallMagicVoiceIV1.setOnClickListener { view -> setVoiceEffect(view, 1) }
        mBinding.imCallMagicVoiceIV2.setOnClickListener { view -> setVoiceEffect(view, 2) }
        mBinding.imCallMagicVoiceIV3.setOnClickListener { view -> setVoiceEffect(view, 3) }
        mBinding.imCallMagicVoiceIV4.setOnClickListener { view -> setVoiceEffect(view, 4) }

        // 设置麦克风点击事件
        mBinding.imCallMicBtn.setOnClickListener {
            mBinding.imCallMicBtn.isSelected = !mBinding.imCallMicBtn.isSelected
            IMCallManager.setMuteEnable(rtcEngine, !mBinding.imCallMicBtn.isSelected)
        }
        // 设置扬声器点击事件
        mBinding.imCallSpeakerBtn.setOnClickListener {
            mBinding.imCallSpeakerBtn.isSelected = !mBinding.imCallSpeakerBtn.isSelected
            IMCallManager.setMuteEnable(rtcEngine, mBinding.imCallSpeakerBtn.isSelected)
        }

        // 自己点击接听，然后加入频道
        mBinding.imCallAnswerBtn.setOnClickListener {
            mBinding.imCallAnswerBtn.visibility = View.GONE
            mBinding.imCallStatusTV.setText(R.string.im_call_accepted)
            IMCallManager.sendCallSignal(callId, IMConstants.Call.callStatusAgree)
            joinChannel()
        }
        // 挂断按钮，根据通话状态调用不同操作
        mBinding.imCallEndBtn.setOnClickListener {
            if (IMCallManager.callStatus == IMConstants.Call.callStatusApply) {
                IMCallManager.sendCallSignal(callId, IMConstants.Call.callStatusReject)
                finish()
            } else {
                IMCallManager.sendCallSignal(callId, IMConstants.Call.callStatusEnd)
                IMCallManager.exitChannel(rtcEngine)
                finish()
            }
        }

        // 默认开启麦克风和扬声器
        mBinding.imCallMicBtn.isSelected = !IMCallManager.isMute
        mBinding.imCallSpeakerBtn.isSelected = IMCallManager.isSpeaker

        IMCallManager.setMuteEnable(rtcEngine, !mBinding.imCallMicBtn.isSelected)
        IMCallManager.setSpeakerEnable(rtcEngine, mBinding.imCallSpeakerBtn.isSelected)

        // 监听通话信令
        LDEventBus.observe(this, IMConstants.Call.cmdCallStatusEvent, EMMessage::class.java) {
            val status = it.getIntAttribute(IMConstants.Call.msgAttrCallStatus)
            if (status == IMConstants.Call.callStatusAgree) {
                mBinding.imCallStatusTV.setText(R.string.im_call_accepted)
                // 对方接听后，这边同步加入频道
                joinChannel()
            } else if (status == IMConstants.Call.callStatusReject || status == IMConstants.Call.callStatusBusy) {
                finish()
            } else if (status == IMConstants.Call.callStatusEnd) {
                IMCallManager.exitChannel(rtcEngine)
                finish()
            }
        }
        // 监听通话时间变化
        LDEventBus.observe(this, IMConstants.Call.callTimeEvent, Int::class.java) {
            mBinding.imCallTimeTV.text = IMCallManager.getCallTime()
        }
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        user = CacheManager.getUser(callId)

        // TODO 开发测试阶段，token 为空，或者设置临时 token
        token = ""

        channel = if (isInComingCall) {
            callId + SignManager.getSignId()
        } else {
            SignManager.getSignId() + callId
        }

        bindInfo()

        if (!isInComingCall) {
            IMCallManager.sendCallSignal(callId, 0)
        }
    }

    /**
     * 绑定信息
     */
    private fun bindInfo() {
        // 加载用户信息
        IMGLoader.loadCover(mBinding.imCallCoverIV, user.avatar, isBlur = true)
        IMGLoader.loadAvatar(mBinding.imCallAvatarIV, user.avatar)
        mBinding.imCallNameTV.text = if (user.nickname.isNullOrEmpty()) "小透明" else user.nickname

        mBinding.imCallAnswerBtn.visibility = if (isInComingCall) View.VISIBLE else View.GONE
        if (isInComingCall) {
            mBinding.imCallStatusTV.setText(R.string.im_call_incoming)
        } else {
            mBinding.imCallStatusTV.setText(R.string.im_call_out_wait)
        }
    }

    /**
     * --------------------------------------------------------------------
     * 声网 SDK 操作相关
     */
    private fun initRtcEngine() {
        try {
            rtcEngine = RtcEngine.create(this, IMConstants.agoraAppId(), object : IRtcEngineEventHandler() {})
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
        mBinding.imCallMagicVoiceLL.visibility = View.GONE

        lastEffectView?.isSelected = false
        lastEffectView = view
        lastEffectView?.isSelected = true
        // VOICE_CHANGER_EFFECT_OLDMAN 老年
        // VOICE_CHANGER_EFFECT_UNCLE 中年
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
        IMCallManager.joinChannel(rtcEngine, token, channel)
    }

    override fun onDestroy() {
        RtcEngine.destroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        showBar(R.string.im_calling_no_back)
    }
}