package com.vmloft.develop.library.push

import android.content.Context

import com.igexin.sdk.GTIntentService
import com.igexin.sdk.message.GTCmdMessage
import com.igexin.sdk.message.GTNotificationMessage
import com.igexin.sdk.message.GTTransmitMessage

import com.vmloft.develop.library.tools.utils.logger.VMLog

/**
 * Create by lzan13 on 2022/4/20
 * 描述：推送消息接收服务
 */
class CustomPushIntentService : GTIntentService() {
    /**
     * 获取推送服务的Pid
     */
    override fun onReceiveServicePid(context: Context, pid: Int) {
        VMLog.i("-push-onReceiveServicePid $pid")
    }
    /**
     * 透传消息
     */
    override fun onReceiveMessageData(context: Context, message: GTTransmitMessage) {
        VMLog.i("-push-onReceiveMessageData")
    }

    /**
     * 接收 Cid
     */
    override fun onReceiveClientId(context: Context, cid: String) {
        VMLog.i("-push-onReceiveClientId $cid")
    }

    /**
     * cid 离线上线通知
     */
    override fun onReceiveOnlineState(context: Context, online: Boolean) {
        VMLog.i("-push-onReceiveOnlineState $online")

    }

    /**
     * 各种事件处理回执
     */
    override fun onReceiveCommandResult(context: Context, msg: GTCmdMessage) {
        VMLog.i("-push-onReceiveCommandResult")

    }

    /**
     * 通知到达，只有个推通道下发的通知会回调此方法
     */
    override fun onNotificationMessageArrived(context: Context, msg: GTNotificationMessage) {
        VMLog.i("-push-onNotificationMessageArrived")

    }

    /**
     * 通知点击，只有个推通道下发的通知会回调此方法
     */
    override fun onNotificationMessageClicked(context: Context, msg: GTNotificationMessage) {
        VMLog.i("-push-onNotificationMessageClicked")

    }
}