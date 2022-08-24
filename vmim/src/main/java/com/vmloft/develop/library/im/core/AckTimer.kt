package com.vmloft.develop.library.im.core

import io.socket.client.Ack
import java.util.*

/**
 * Create by lzan13 on 2022/8/11
 * 描述：Ack 定时器
 */
abstract class AckTimer(val timeout: Long = 5000) : Ack {
    private var timer: Timer? = null
    private var called = false

    init {
        startTimer()
    }

    fun startTimer() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (called) return
                called = true
                cancelTimer()
                call(-1, "timeout")
            }
        }, timeout)
    }

    fun cancelTimer() {
        timer?.cancel()
        timer = null
    }

    override fun call(vararg args: Any) {
        callback(args[0] as Int, args[1])
    }

    /**
     * 结果回调
     */
    abstract fun callback(code: Int, obj: Any)
}