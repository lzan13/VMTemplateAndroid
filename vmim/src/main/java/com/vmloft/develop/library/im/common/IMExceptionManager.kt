package com.vmloft.develop.library.im.common

import com.hyphenate.EMError
import com.vmloft.develop.library.common.common.CError
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.logger.VMLog

/**
 * Create by lzan13 on 2019/5/11 22:42
 *
 * IM 相关错误统一处理类
 */
object IMExceptionManager {

    /**
     * 统一处理异常情况
     *
     * @param code     错误码
     * @param desc     错误描述
     */
    fun disposeError(code: Int, desc: String): RResult<String> {
        VMLog.e("错误信息: $code, $desc")
        val error: String
        when (code) {
            EMError.NETWORK_ERROR -> error = VMStr.byRes(R.string.im_error_network_error)
            EMError.INVALID_USER_NAME -> error = VMStr.byRes(R.string.im_error_invalid_user_name)
            EMError.INVALID_PASSWORD -> error = VMStr.byRes(R.string.im_error_invalid_password)
            EMError.USER_AUTHENTICATION_FAILED -> error = VMStr.byRes(R.string.im_error_user_authentication_failed)
            EMError.USER_NOT_FOUND -> error = VMStr.byRes(R.string.im_error_user_not_found)
            EMError.USER_ALREADY_EXIST -> error = VMStr.byRes(R.string.im_error_user_already_exits)
            EMError.USER_ILLEGAL_ARGUMENT -> error = VMStr.byRes(R.string.im_error_user_illegal_argument)
            EMError.USER_UNBIND_DEVICETOKEN_FAILED -> error = VMStr.byRes(R.string.im_error_user_unbind_device_token_failed)
            EMError.SERVER_NOT_REACHABLE -> error = VMStr.byRes(R.string.im_error_server_not_reachable)
            EMError.SERVER_TIMEOUT -> error = VMStr.byRes(R.string.im_error_server_timeout)
            EMError.SERVER_BUSY -> error = VMStr.byRes(R.string.im_error_server_busy)
            EMError.SERVER_UNKNOWN_ERROR -> error = VMStr.byRes(R.string.im_error_server_unknown)
            EMError.USER_REG_FAILED -> error = VMStr.byRes(R.string.im_error_user_reg_failed)
            else -> error = VMStr.byResArgs(R.string.im_error_unknown, code, desc)
        }
        return RResult.Error(code, error)
    }
}