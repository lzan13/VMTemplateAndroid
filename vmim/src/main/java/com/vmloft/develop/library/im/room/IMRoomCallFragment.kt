package com.vmloft.develop.library.im.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager

import com.drakeet.multitype.MultiTypeAdapter

import com.hyphenate.EMChatRoomChangeListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.base.BFragment
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.data.bean.Room
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImFragmentRoomCallBinding
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMSystem
import com.vmloft.develop.library.tools.utils.logger.VMLog

import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.models.ClientRoleOptions

/**
 * Create by lzan13 2021/05/27
 * 描述：资讯房间
 */
class IMRoomCallFragment : BFragment<ImFragmentRoomCallBinding>() {

    // 会话相关
    private lateinit var chatId: String

    private var isOwner: Boolean = false

    // 声网通话引擎
    lateinit var rtcEngine: RtcEngine
    var lastEffectView: View? = null

    lateinit var token: String // 加入频道所需 token
    lateinit var channel: String // 频道名

    // 自己的 Id
    lateinit var selfId: String

    // 房间信息
    lateinit var mRoom: Room

    var roomListener: EMChatRoomChangeListener? = null

    // 当前上麦用户信息
    var currMicUser: User? = null

    // 列表适配器
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { MultiTypeAdapter() }
    private val mItems = ArrayList<Any>()
    private lateinit var layoutManager: LinearLayoutManager


    companion object {
        private val argChatId = "argChatId"

        /**
         * Fragment 的工厂方法，方便创建并设置参数
         */
        fun newInstance(chatId: String): IMRoomCallFragment {
            val fragment = IMRoomCallFragment()
            val args = Bundle()
            args.putString(argChatId, chatId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = ImFragmentRoomCallBinding.inflate(inflater, parent, false)

    override fun initUI() {
        super.initUI()

        // 魔音变声
        mBinding.imCallMicMagicBtn.setOnClickListener {
            if (mBinding.imMagicVoiceLL.isVisible) {
                mBinding.imMagicVoiceLL.visibility = View.GONE
            } else {
                mBinding.imMagicVoiceLL.visibility = View.VISIBLE
            }
        }
        // 设置人声变声效果
        mBinding.imMagicVoiceIV0.setOnClickListener { view -> setVoiceEffect(view, 0) }
        mBinding.imMagicVoiceIV1.setOnClickListener { view -> setVoiceEffect(view, 1) }
        mBinding.imMagicVoiceIV2.setOnClickListener { view -> setVoiceEffect(view, 2) }
        mBinding.imMagicVoiceIV3.setOnClickListener { view -> setVoiceEffect(view, 3) }
        mBinding.imMagicVoiceIV4.setOnClickListener { view -> setVoiceEffect(view, 4) }

        mBinding.imRoomGuestApplyBtn.setOnClickListener {
            if (isOwner) return@setOnClickListener
            sendApplyMicMsg(IMConstants.Call.roomApplyMicStatusApply)
            showBar(R.string.im_room_apply_hint)
        }
        mBinding.imRoomGuestAvatarIV.setOnClickListener {
            // 房主可以点击用户头像下麦 TODO 其他人不能，后续会完善头像点击事件
            if (isOwner) {
                kickUserMic()
            } else if (currMicUser?.id == selfId) {
                sendApplyMicMsg(IMConstants.Call.roomApplyMicStatusDown)
            }
        }

        initRecyclerView()

        // 监听申请上麦命令
        LDEventBus.observe(this, IMConstants.Call.cmdRoomApplyMic, EMMessage::class.java) {
            onApplyMic(it)
        }
    }

    override fun initData() {
        chatId = requireArguments().getString(argChatId) ?: ""

        token = ""
        channel = chatId

        mRoom = CacheManager.getRoom(chatId)
        // 判断下当前用户是不是 Owner
        selfId = IM.getSelfId()
        isOwner = selfId == mRoom.owner.id

        initRtcEngine()

        initListener()

        bindOwner()
    }

    /**
     * 初始化申请列表
     */
    private fun initRecyclerView() {
        mAdapter.register(ItemRoomApplyUserDelegate(object : BItemDelegate.BItemListener<User> {
            override fun onClick(v: View, data: User, position: Int) {
                // 同意上麦申请
                agreeApply(data, position)
            }
        }))

        mAdapter.items = mItems

        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        mBinding.imRoomApplyRecyclerView.layoutManager = layoutManager
        mBinding.imRoomApplyRecyclerView.adapter = mAdapter
    }

    /**
     * 绑定房主信息
     */
    private fun bindOwner() {
        IMGLoader.loadAvatar(mBinding.imRoomOwnerAvatarIV, mRoom.owner.avatar)
        mBinding.imRoomOwnerNameTV.text = mRoom.owner.nickname
        mBinding.imRoomCountTV.text = mRoom.count.toString()

        when (mRoom.owner.gender) {
            1 -> mBinding.imRoomOwnerGenderIV.setImageResource(R.drawable.ic_gender_man)
            0 -> mBinding.imRoomOwnerGenderIV.setImageResource(R.drawable.ic_gender_woman)
            else -> mBinding.imRoomOwnerGenderIV.setImageResource(R.drawable.ic_gender_man)
        }

        mBinding.imRoomDescTV.text = mRoom.desc
    }

    /**
     * 绑定上麦者信息
     */
    private fun bindGuest() {
        mBinding.imRoomGuestAvatarIV.visibility = if (currMicUser == null) View.GONE else View.VISIBLE
        IMGLoader.loadAvatar(mBinding.imRoomGuestAvatarIV, currMicUser?.avatar ?: "")
    }

    /**
     * 同意上麦申请
     */
    private fun agreeApply(data: User, position: Int) {
        if (currMicUser != null) {
            errorBar(R.string.im_room_mic_not_null)
            return
        }
        mItems.remove(data)
        mAdapter.notifyItemRemoved(position)

        sendApplyMicMsg(IMConstants.Call.roomApplyMicStatusAgree, data.id)
    }

    /**
     * 发送上麦申请相关消息 [IMConstants.Call]
     * @param status 0-申请 1-同意 2-上麦 3-下麦 4-被踢下麦
     */
    private fun sendApplyMicMsg(status: Int, id: String = "") {
        val msg = IMChatManager.createCMDMessage(IMConstants.Call.cmdRoomApplyMic, chatId)
        msg.chatType = IMChatManager.wrapChatType(IMConstants.ChatType.imChatRoom)
        msg.setAttribute(IMConstants.Call.msgAttrApplyMicStatus, status)

        if (status == IMConstants.Call.roomApplyMicStatusAgree) {
            // 同意之后带上同意上麦的用户 id
            msg.setAttribute(IMConstants.Call.msgAttrApplyMicInfo, id)
        } else if (status == IMConstants.Call.roomApplyMicStatusUp) {
            // 房主同意之后，自己要发布上麦命令，把自己的信息发给其他用户
            if (!isOwner) {
                currMicUser = SignManager.getCurrUser()
            }
            val info = JsonUtils.toJson(currMicUser)
            msg.setAttribute(IMConstants.Call.msgAttrApplyMicInfo, info)
            bindGuest()
            upMic()
        } else if (status == IMConstants.Call.roomApplyMicStatusDown) {
            // 下麦，清空当前上麦者信息
            currMicUser = null
            bindGuest()
            downMic()
        } else if (status == IMConstants.Call.roomApplyMicStatusKickDown) {
            // 下麦，清空当前上麦者信息
            currMicUser = null
            bindGuest()
        }

        IMChatManager.sendMessage(msg)
    }

    /**
     * 处理申请上麦相关处理
     */
    private fun onApplyMic(msg: EMMessage) {
        val status = msg.getIntAttribute(IMConstants.Call.msgAttrApplyMicStatus)

        if (status == IMConstants.Call.roomApplyMicStatusApply && isOwner) {
            // 收到用户上麦申请
            val user = CacheManager.getUser(msg.from)
            if (!mItems.contains(user)) {
                mItems.add(user)
                mAdapter.notifyItemInserted(mItems.size - 1)
            }
        } else if (status == IMConstants.Call.roomApplyMicStatusAgree) {
            val id = msg.getStringAttribute(IMConstants.Call.msgAttrApplyMicInfo)
            // 收到房主同意上麦申请，这里要判断下是不是同意自己上麦，是自己的话就发送自己的信息到房间
            if (selfId == id) {
                sendApplyMicMsg(IMConstants.Call.roomApplyMicStatusUp)
            }
        } else if (status == IMConstants.Call.roomApplyMicStatusUp) {
            // 收到用户上麦信息，保存下上麦用户的信息，并刷新 UI
            val info = msg.getStringAttribute(IMConstants.Call.msgAttrApplyMicInfo, "")
            currMicUser = JsonUtils.fromJson(info, User::class.java)
            bindGuest()
        } else if (status == IMConstants.Call.roomApplyMicStatusDown) {
            // 收到用户下麦信息，清空当前上麦者信息
            currMicUser = null
            bindGuest()
        } else if (status == IMConstants.Call.roomApplyMicStatusKickDown) {
            if (currMicUser?.id == selfId) {
                showKickDialog()
            }
            // 收到被踢下麦信息，清空当前上麦者信息
            currMicUser = null
            bindGuest()
            downMic()
        }
    }

    /**
     * 弹出踢用户下麦确认对话框
     */
    private fun kickUserMic() {
        mDialog = CommonDialog(requireContext())
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.im_room_kick_user_mic_hint)
            dialog.setPositive(listener = {
                sendApplyMicMsg(IMConstants.Call.roomApplyMicStatusKickDown)
            })
            dialog.show()
        }
    }

    /**
     * 显示被踢下麦对话框
     */
    private fun showKickDialog() {
        mDialog = CommonDialog(requireContext())
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.im_room_kicked_mic)
            dialog.setNegative("")
            dialog.setPositive(VMStr.byRes(R.string.vm_i_know))
            dialog.show()
        }
    }

    /**
     * 显示房间解散对话框
     */
    private fun showDestroyedDialog() {
        mDialog = CommonDialog(requireContext())
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.im_room_destroyed)
            dialog.setNegative("")
            dialog.setPositive(VMStr.byRes(R.string.vm_i_know)) {
                // 退出聊天室
                IMChatRoomManager.exitRoom(chatId)
                requireActivity().finish()
            }
            dialog.show()
        }
    }

    /**
     * --------------------------------------------------------------------
     * 聊天室 SDK 相关
     */
    private fun initListener() {
        roomListener = object : EMChatRoomChangeListener {
            override fun onChatRoomDestroyed(roomId: String?, roomName: String?) {
                VMSystem.runInUIThread({
                    showDestroyedDialog()
                })
            }

            override fun onMemberJoined(roomId: String?, participant: String?) {
                VMSystem.runInUIThread({
                    mRoom.count++
                    mBinding.imRoomCountTV.text = mRoom.count.toString()
                })
            }

            override fun onMemberExited(roomId: String?, roomName: String?, participant: String?) {
                VMSystem.runInUIThread({
                    mRoom.count--
                    if (mRoom.count <= 0) {
                        mRoom.count = 1
                    }
                    mBinding.imRoomCountTV.text = mRoom.count.toString()
                })
            }

            override fun onRemovedFromChatRoom(reason: Int, roomId: String?, roomName: String?, participant: String?) {}
            override fun onMuteListAdded(chatRoomId: String?, mutes: MutableList<String>?, expireTime: Long) {}
            override fun onMuteListRemoved(chatRoomId: String?, mutes: MutableList<String>?) {}
            override fun onWhiteListAdded(chatRoomId: String?, whitelist: MutableList<String>?) {}
            override fun onWhiteListRemoved(chatRoomId: String?, whitelist: MutableList<String>?) {}
            override fun onAllMemberMuteStateChanged(chatRoomId: String?, isMuted: Boolean) {}
            override fun onAdminAdded(chatRoomId: String?, admin: String?) {}
            override fun onAdminRemoved(chatRoomId: String?, admin: String?) {}
            override fun onOwnerChanged(chatRoomId: String?, newOwner: String?, oldOwner: String?) {}
            override fun onAnnouncementChanged(chatRoomId: String?, announcement: String?) {}
        }
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(roomListener)
    }

    /**
     * --------------------------------------------------------------------
     * 声网 SDK 操作相关
     */
    private fun initRtcEngine() {
        try {
            rtcEngine = RtcEngine.create(requireContext(), IMConstants.agoraAppId(), object : IRtcEngineEventHandler() {})
            // 这里设置成直播场景
            rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            // 设置用户角色，房主默认为主播，其他人默认为观众
            if (isOwner) {
                rtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER)
            } else {
                rtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_AUDIENCE)
                // 设置用户观众级别
                val options = ClientRoleOptions()
                options.audienceLatencyLevel = Constants.AUDIENCE_LATENCY_LEVEL_LOW_LATENCY
                rtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_AUDIENCE, options)
            }
            // 设置采样率和音频通话场景
            rtcEngine.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY, Constants.AUDIO_SCENARIO_GAME_STREAMING)
            // 加入频道
            rtcEngine.joinChannel(token, channel, "", 0)

        } catch (e: Exception) {
            VMLog.e("rtc engline init error ${e.message}")
        }
    }

    /**
     * 上麦操作，设置用户身份为主播
     */
    private fun upMic() {
        rtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_BROADCASTER)

    }

    /**
     * 下麦操作，设置用户身份为观众
     */
    private fun downMic() {
        rtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_AUDIENCE)
        // 设置用户观众级别
        val options = ClientRoleOptions()
        options.audienceLatencyLevel = Constants.AUDIENCE_LATENCY_LEVEL_LOW_LATENCY
        rtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_AUDIENCE, options)
    }

    /**
     * 设置声音音效
     */
    private fun setVoiceEffect(view: View, effect: Int) {
        mBinding.imMagicVoiceLL.visibility = View.GONE

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

    override fun onDestroy() {
        EMClient.getInstance().chatroomManager().removeChatRoomListener(roomListener)
        roomListener = null
        // 离开频道
        rtcEngine.leaveChannel()

        RtcEngine.destroy()
        super.onDestroy()
    }

}