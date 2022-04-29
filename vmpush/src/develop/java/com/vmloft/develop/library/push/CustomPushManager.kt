package com.vmloft.develop.library.push

import android.content.Context

import com.igexin.sdk.PushManager

import com.vmloft.develop.library.tools.utils.logger.VMLog


/**
 * Create by lzan13 on 2022/4/20
 * 描述：推送管理类
 */
object CustomPushManager {

    fun init(context: Context) {
        PushManager.getInstance().initialize(context)
        if (BuildConfig.DEBUG) {
            PushManager.getInstance().setDebugLogger(context) { msg -> VMLog.i("pushLog $msg") }
        }
    }
}