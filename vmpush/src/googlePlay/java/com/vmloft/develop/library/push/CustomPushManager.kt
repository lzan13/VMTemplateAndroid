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
        // 初始化 sdk
        PushManager.getInstance().initialize(context, CustomPushService::class.java)
        // 注册自定义推送消息接收处理服务
        PushManager.getInstance().registerPushIntentService(context, CustomPushIntentService::class.java);
        // 设置同意隐私条款
        PushManager.getInstance().setPrivacyPolicyStrategy(context, true)
    }
}