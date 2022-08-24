package com.vmloft.develop.library.im.common

import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.im.BuildConfig

/**
 * Create by lzan13 on 2019/5/21 15:32
 * 描述：IM 常量类
 */
object IMConstants {

    const val dbName = "vm_db_im_"
    const val dbPass = "vm_db_im_lzan13"

    /**
     * 获取声网 AppId
     */
    fun agoraAppId(): String {
        return BuildConfig.agoraAppId
    }

    /**
     * 获取接口 host 地址，根据 debug 状态返回不同地址
     */
    fun imHost(): String {
        return if (CSPManager.isDebug()) {
            BuildConfig.imHostDebug
        } else {
            BuildConfig.imHostRelease
        }
    }

    /**
     * 通用字段
     */
    object Common {
        // 发送消息 event
        const val wsMessageEvent = "message"
        const val wsSignalEvent = "signal"

        // 链接状态改变事件
        const val connectStatusEvent = "connectStatusEvent"

        const val newMsgEvent = "newMsgEvent" // 新消息
        const val updateMsgEvent = "updateMsgEvent"
        const val changeUnreadCount = "changeUnreadCount"

        // 撤回消息信令
        const val signalRecallMessage = "recallMessage" // 撤回
        const val signalInfoChange = "infoChange" // 联系人信息改变
        const val signalInputStatus = "inputStatus" // 输入状态

        const val signalMatchInfo = "matchInfo" // 匹配信息


        // 扩展字段 key
        // 消息扩展
        const val extType = "extType" // 扩展类型
        const val extSystemTips = "extSystemTips" // 系统消息扩展信息
        const val extSystemUrl = "extSystemUrl" // 系统消息扩展链接
        const val extGift = "msgAttrGift" // 礼物消息扩展 Key

    }

    /**
     * 快速聊天
     */
    object ChatFast {
        const val signalFastInput = "fastInput" // 实时输入内容
        const val extFastInputStatus = "extFastInputStatus" // 快速聊天状态
        const val extFastInputContent = "extFastInputContent" // 快速聊天输入变化内容
        const val extFastInputLen = "extFastInputLen" // 快速聊天输入变化长度

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
        // 通话信令，具体指令通过扩展区分
        const val signalCall = "call"

        const val callStatusEvent = "callStatusEvent" // 实现通话事件
        const val callTimeEvent = "callTimeEvent" // 通话时间事件

        const val extCallStatus = "extCallStatus" // 通话信令状态
        const val extCallType = "extCallType" // 通话类型 0-语音 1-视频

        // 通话类型 0-语音 1-视频
        const val callTypeVoice = 0
        const val callTypeVideo = 1

        const val callStatusApply = 0 // 申请中
        const val callStatusAgree = 1 // 同意
        const val callStatusReject = 2 // 拒绝
        const val callStatusBusy = 3 // 忙碌
        const val callStatusEnd = 9 // 结束

        // 房间上麦相关
        const val signalRoomApplyMic = "roomApplyMic" // 房间申请上麦信令
        const val extRoomApplyMicStatus = "msgAttrApplyMicStatus" // 房间申请上麦相关操作状态
        const val extRoomApplyMicInfo = "msgAttrApplyMicInfo" // 房间申请上麦相关信息

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
        const val imSingle = 0
        const val imGroup = 1
        const val imRoom = 2
    }

    /**
     * 消息状态
     */
    object MsgStatus {
        const val imLoading = 0 // 发送中
        const val imSuccess = 1 // 成功
        const val imFailed = 2 // 失败
    }

    /**
     * 消息类型，这里和后端对应好
     * 0-文本
     * 10-通话
     * 1-系统
     *  100-默认
     *  101-撤回
     *  102-欢迎
     *
     * 2-卡片
     *  200-默认
     *  201-文件
     *  202-红包
     *
     * 3-图片
     * 4-语音
     * 5-视频
     * 6-礼物
     * 7-表情
     * 8-位置
     */
    object MsgType {
        const val imText = 0 // 文本消息
        const val imCall = 10 // 通话消息

        const val imSystem = 1 // 系统消息
        const val imSystemDefault = 100 // 默认
        const val imSystemRecall = 101 // 撤回
        const val imSystemWelcome = 102 // 欢迎

        const val imCard = 2 // 卡片
        const val imCardDefault = 200 // 默认卡片
        const val imCardFile = 201 // 文件卡片
        const val imCardRedPacket = 202 // 红包卡片

        const val imPicture = 3 // 图片
        const val imVoice = 4 // 语音
        const val imVideo = 5 // 视频
        const val imGift = 6 // 礼物
        const val imEmotion = 7 // 表情
        const val imLocation = 8 // 位置
    }

}