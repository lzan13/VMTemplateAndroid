package com.vmloft.develop.library.common.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * Create by lzan13 on 2020/8/12 20:34
 * 描述：
 */
object BroadcastUtils {

    /**
     * 注册广播
     */
    fun send(context: Context, action: String) {
        val intent = Intent(action)
        context.sendBroadcast(intent)
    }

    /**
     * 注册广播
     */
    fun register(context: Context, action: String, receiver: BroadcastReceiver) {
        val filter = IntentFilter(action)
        context.registerReceiver(receiver, filter)
    }

    /**
     * 取消注册广播
     */
    fun unregister(context: Context, receiver: BroadcastReceiver) {
        context.unregisterReceiver(receiver)
    }


    /**
     * 注册本地广播
     */
    fun sendLocal(context: Context, action: String) {
        val intent = Intent(action)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
    /**
     * 注册本地广播
     */
    fun registerLocal(context: Context, action: String, receiver: BroadcastReceiver) {
        val filter = IntentFilter(action)
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter)
    }

    /**
     * 取消注册本地广播
     */
    fun unregisterLocal(context: Context, receiver: BroadcastReceiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
    }


}