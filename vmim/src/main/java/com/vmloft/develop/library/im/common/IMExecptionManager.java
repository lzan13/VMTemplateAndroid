package com.vmloft.develop.library.im.common;

import com.hyphenate.EMError;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Create by lzan13 on 2019/5/11 22:42
 *
 * IM 相关错误统一处理类
 */
public class IMExecptionManager {

    /**
     * 私有构造，初始化 ShredPreferences 文件名
     */
    private IMExecptionManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        private static final IMExecptionManager INSTANCE = new IMExecptionManager();
    }

    /**
     * 获取的实例
     */
    public static final IMExecptionManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 统一处理异常情况
     *
     * @param code     错误码
     * @param desc     错误描述
     * @param callback 自定义的回调接口
     */
    public void disposeError(int code, String desc, IMCallback callback) {
        VMLog.e("错误信息: [%d] - %s", code, desc);
        String error;
        switch (code) {
        // 网络异常
        case EMError.NETWORK_ERROR:
            error = VMStr.byRes(R.string.im_error_network_error);
            break;
        // 无效的用户名 101
        case EMError.INVALID_USER_NAME:
            error = VMStr.byRes(R.string.im_error_invalid_user_name);
            break;
        // 无效的密码 102
        case EMError.INVALID_PASSWORD:
            error = VMStr.byRes(R.string.im_error_invalid_password);
            break;
        // 用户认证失败，用户名或密码错误 202
        case EMError.USER_AUTHENTICATION_FAILED:
            error = VMStr.byRes(R.string.im_error_user_authentication_failed);
            break;
        // 用户不存在 204
        case EMError.USER_NOT_FOUND:
            error = VMStr.byRes(R.string.im_error_user_not_found);
            break;
        // 用户已存在
        case EMError.USER_ALREADY_EXIST:
            error = VMStr.byRes(R.string.im_error_user_already_exits);
            break;
        // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
        case EMError.USER_ILLEGAL_ARGUMENT:
            error = VMStr.byRes(R.string.im_error_user_illegal_argument);
            break;
        case EMError.USER_UNBIND_DEVICETOKEN_FAILED:
            error = VMStr.byRes(R.string.im_error_user_unbind_device_token_failed);
            break;
        // 无法访问到服务器 300
        case EMError.SERVER_NOT_REACHABLE:
            error = VMStr.byRes(R.string.im_error_server_not_reachable);
            break;
        // 等待服务器响应超时 301
        case EMError.SERVER_TIMEOUT:
            error = VMStr.byRes(R.string.im_error_server_timeout);
            break;
        // 服务器繁忙 302
        case EMError.SERVER_BUSY:
            error = VMStr.byRes(R.string.im_error_server_busy);
            break;
        // 未知 Server 异常 303
        case EMError.SERVER_UNKNOWN_ERROR:
            error = VMStr.byRes(R.string.im_error_server_unknown);
            break;
        // 注册失败
        case EMError.USER_REG_FAILED:
            error = VMStr.byRes(R.string.im_error_user_reg_failed);
            break;
        default:
            error = VMStr.byRes(R.string.im_error_unknown);
            break;
        }
        if (callback != null) {
            callback.onError(code, error);
        }
    }
}
