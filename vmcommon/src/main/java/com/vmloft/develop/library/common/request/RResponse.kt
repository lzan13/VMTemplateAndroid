package com.vmloft.develop.library.common.request

/**
 * Create by lzan13 on 2020/02/13 18:56
 * 描述：统一请求结果数据 bean
 */
data class RResponse<out T>(val code: Int, val msg: String, val data: T)
