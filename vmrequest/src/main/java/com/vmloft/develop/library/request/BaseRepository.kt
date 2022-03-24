package com.vmloft.develop.library.request

import com.google.gson.JsonParseException
import com.vmloft.develop.library.base.router.CRouter

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

import java.net.SocketTimeoutException

/**
 * Create by lzan13 on 2020/02/14 15:35
 * 描述：数据请求基类
 */
abstract class BaseRepository {

    suspend fun <T : Any> apiCall(call: suspend () -> RResponse<T>): RResponse<T> {
        return call.invoke()
    }

    suspend fun <T : Any> safeRequest(call: suspend () -> RResult<T>): RResult<T> {
        return try {
            call()
        } catch (e: Exception) {
            handleExceptions(e)
        }
    }

    suspend fun <T : Any> executeResponse(
        response: RResponse<T>,
        successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): RResult<T> {
        return coroutineScope {
            if (response.code != 0) {
                if (response.code == 401) {
                    // 401 错误表示未认证，或者 token 过期，需要重新登录
                    CRouter.goMain(1)
                }
                errorBlock?.let { it() }
                RResult.Error(response.code, response.msg)
            } else {
                successBlock?.let { it() }
                RResult.Success(response.msg, response.data)
            }
        }
    }

    /**
     * 处理请求层的错误,对可能的已知的错误进行处理
     */
    private fun handleExceptions(e: Throwable): RResult.Error {
        var error = when (e) {
            is CancellationException -> "请求取消"
            is SocketTimeoutException -> "连接超时"
            is JsonParseException -> "数据解析错误"
            is NumberFormatException -> "数据类型转换错误"
            else -> "请求失败，请稍后再试"
        }
        e.printStackTrace()
        return RResult.Error(-1, error)
    }

}