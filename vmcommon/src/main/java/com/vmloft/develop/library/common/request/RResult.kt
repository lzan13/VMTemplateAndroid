package com.vmloft.develop.library.common.request

/**
 * Create by lzan13 on 2020/02/13 18:56
 * 描述：协程回调结果
 */
sealed class RResult<out T : Any> {

    data class Success<out T : Any>(val msg: String? = null, val data: T? = null) : RResult<T>()
    data class Error(val code: Int, val error: String) : RResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "请求成功 - msg:$msg, data: $data"
            is Error -> "请求失败 - code:$code, error: $error"
        }
    }

}
