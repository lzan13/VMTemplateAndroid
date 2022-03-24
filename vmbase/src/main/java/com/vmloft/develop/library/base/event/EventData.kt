package com.vmloft.develop.library.base.event

/**
 * Create by lzan13 on 2022/3/22
 * 描述：生命周期事件总线通用 Event 数据传递类
 */
data class EventData(
    var what: Int = 0, // 简单事件类型
    var data: Any? = null, // 自定义数据 bean 数据类型
) {
}