package com.vmloft.develop.library.im.common

import com.vmloft.develop.library.im.BuildConfig

/**
 * Create by lzan13 on 2019/5/21 15:32
 *
 * IM 常量类
 */
object IMConstants {

    /**
     * 获取声网 AppId
     */
    fun agoraAppId(): String {
        return BuildConfig.agoraAppId
    }

    /**
     * 链接状态
     */
    object ConnectStatus {
        // 链接状态改变事件
        const val connectStatusEvent = "connectStatusEvent"

        const val connected = 1 // 已链接
        const val disconnect = 0 // 已断开
    }

    /**
     * 通用字段
     */
    object Common {
        // 新消息
        const val newMsgEvent = "newMsgEvent"
        const val updateMsgEvent = "updateMsgEvent"
        const val readMsgEvent = "readMsgEvent"
        const val deliveredMsgEvent = "deliveredMsgEvent"
        const val recallMsgEvent = "recallMsgEvent"
        const val changeMsgEvent = "changeMsgEvent"

        // CMD Action
        const val cmdEncourageAction = "cmdEncourageAction" // 鼓励
        const val cmdInfoChangeAction = "cmdInfoChangeAction" // 联系人信息改变
        const val cmdInputStatusAction = "cmdInputStatusAction" // 输入状态
        const val cmdRecallAction = "cmdRecallAction" // 撤回

        /**
         * 定义会话与消息扩展字段 key
         */
        /**
         * 会话扩展
         */
        const val conversationDraft = "conversationDraft" // 草稿
        const val conversationTime = "conversationTime" // 最后时间
        const val conversationTop = "conversationTop" // 置顶
        const val conversationUnread = "conversationUnread" // 会话未读
        const val conversationMsgSendCount = "conversationMsgSendCount" // 会话消息发送数
        const val conversationMsgReceiveCount = "conversationMsgReceiveCount" // 会话消息接收数

        /**
         * 消息扩展
         */
        const val msgAttrExtType = "msgAttrExtType" // 扩展类型

        const val msgAttrMsgId = "msgAttrMsgId" // 消息 Id，这个是透传消息撤回用到
        const val msgAttrNotifyEnable = "msgAttrNotifyEnable" // 是否发送通知栏提醒
        const val msgAttrUnsupported = "msgAttrUnsupported" // 不支持消息扩展文案 Key
        const val msgAttrSystem = "msgAttrSystem" // 系统消息扩展文案 Key

    }

    /**
     * 快速聊天
     */
    object ChatFast {
        const val cmdFastInputAction = "cmdFastInputAction" // 实时输入内容
        const val msgAttrFastInputStatus = "msgAttrFastInputStatus" // 快速聊天状态
        const val msgAttrFastInputContent = "msgAttrFastInputContent" // 快速聊天输入变化内容
        const val msgAttrFastInputLen = "msgAttrFastInputCount" // 快速聊天输入变化长度

        const val fastInputStatusApply = 0 // 申请
        const val fastInputStatusAgree = 1 // 同意
        const val fastInputStatusReject = 2 // 拒绝
        const val fastInputStatusBusy = 3 // 拒绝
        const val fastInputStatusContent = 5 // 内容变化
        const val fastInputStatusEnd = 9 // 结束

    }

    /**
     * 通话状态
     */
    object Call {
        // CMD 实现通话信令事件
        const val cmdCallStatusEvent = "cmdCallStatusEvent"

        // 通话信令 Action，具体指令通过扩展区分
        const val cmdCallAction = "cmdCallAction"

        // 通话时间事件
        const val callTimeEvent = "callTimeEvent"

        const val msgAttrCallStatus = "msgAttrCallStatus" // 通话信令状态
        const val msgAttrCallType = "msgAttrCallType" // 通话类型 0-语音 1-视频

        // 通话类型 0-语音 1-视频
        const val callTypeVoice = 0
        const val callTypeVideo = 1

        const val callStatusApply = 0 // 申请中
        const val callStatusAgree = 1 // 同意
        const val callStatusReject = 2 // 拒绝
        const val callStatusBusy = 3 // 忙碌
        const val callStatusEnd = 9 // 结束

        // 房间上麦相关
        const val cmdRoomApplyMic = "cmdRoomApplyMic" // 房间申请上麦相关操作
        const val msgAttrApplyMicStatus = "msgAttrApplyMicStatus" // 房间申请上麦相关操作状态
        const val msgAttrApplyMicInfo = "msgAttrApplyMicInfo" // 房间申请上麦相关信息

        const val roomApplyMicStatusApply = 0 // 申请中
        const val roomApplyMicStatusAgree = 1 // 同意
        const val roomApplyMicStatusUp = 2 // 上麦
        const val roomApplyMicStatusDown = 3 // 下麦
        const val roomApplyMicStatusKickDown = 4 // 被房主踢下麦

    }

    /**
     * 聊天类型
     */
    object ChatType {
        const val imChatSingle = 0
        const val imChatGroup = 1
        const val imChatRoom = 2
    }

    /**
     * 消息类型
     */
    object MsgType {
        // 未知类型
        const val imUnknown = 0x00

        // 系统消息
        const val imSystem = 0x01

        // 撤回
        const val imRecall = 0x05

        // 文本
        const val imTextReceive = 0x10
        const val imTextSend = 0x11

        // 图片
        const val imPictureReceive = 0x20
        const val imPictureSend = 0x21

        // 视频
        const val imVideoReceive = 0x30
        const val imVideoSend = 0x31

        // 定位
        const val imLocationReceive = 0x40
        const val imLocationSend = 0x41

        // 语音
        const val imVoiceReceive = 0x50
        const val imVoiceSend = 0x51

        // 文件
        const val imFileReceive = 0x60
        const val imFileSend = 0x61

        // 通话
        const val imCall = 0x100
        const val imCallReceive = 0x101
        const val imCallSend = 0x102

        // 大表情
        const val imBigEmotion = 0x110
        const val imBigEmotionReceive = 0x111
        const val imBigEmotionSend = 0x112
    }

}