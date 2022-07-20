package com.vmloft.develop.app.template.app

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Create by lzan13 on 2020-02-13 18:02
 * 描述：协同程序调度代理类
 */
data class CoroutinesDispatcherProvider(
    val main: CoroutineDispatcher = Dispatchers.Main,
    val computation: CoroutineDispatcher = Dispatchers.Default,
    val io: CoroutineDispatcher = Dispatchers.IO
) {
    constructor() : this(Dispatchers.Main, Dispatchers.Default, Dispatchers.IO)
}
