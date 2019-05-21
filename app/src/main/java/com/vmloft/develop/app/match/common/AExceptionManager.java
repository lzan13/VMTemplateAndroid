package com.vmloft.develop.app.match.common;

import com.avos.avoscloud.AVException;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.library.im.base.IMException;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Create by lzan13 on 2019/5/11 22:42
 *
 * 异常管理类
 */
public class AExceptionManager {

    /**
     * 私有构造，初始化 ShredPreferences 文件名
     */
    private AExceptionManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        private static final AExceptionManager INSTANCE = new AExceptionManager();
    }

    /**
     * 获取的实例
     */
    public static final AExceptionManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 统一处理异常情况
     *
     * @param e        异常情况
     * @param callback 自定义的回调接口  *
     */
    public void disposeException(Throwable e, ACallback callback) {
        if (e instanceof AVException) {
            disposeAVException((AVException) e, callback);
        } else if (e instanceof IMException) {
            disposeIMException((IMException) e, callback);
        } else {
            callback.onError(AError.UNKNOWN, e.getMessage());
        }
    }

    /**
     * 处理 LeanCloud 异常情况
     *
     * @param e        异常情况
     * @param callback 自定义的回调接口
     */
    private void disposeIMException(IMException e, ACallback callback) {
        switch (e.getCode()) {
            case AError.UNKNOWN:
                callback.onError(AError.UNKNOWN, VMStr.byResArgs(R.string.unknown, e.getCode()));
                break;
            default:
                callback.onError(e.getCode(), e.getDesc());
                break;
        }
    }

    /**
     * 处理 LeanCloud 异常情况
     *
     * @param e        异常情况
     * @param callback 自定义的回调接口
     */
    private void disposeAVException(AVException e, ACallback callback) {
        switch (e.getCode()) {
            case AVException.USERNAME_TAKEN:
                callback.onError(AError.USER_ALREADY_EXIST, VMStr.byRes(R.string.account_username_already_exist));
                break;
            case AVException.EMAIL_TAKEN:
                callback.onError(AError.EMAIL_ALREADY_EXIST, VMStr.byRes(R.string.account_email_already_exist));
                break;
            case AVException.USER_MOBILE_PHONENUMBER_TAKEN:
                callback.onError(AError.PHONE_ALREADY_EXIST, VMStr.byRes(R.string.account_phone_already_exist));
                break;
            case AVException.USERNAME_PASSWORD_MISMATCH:
                callback.onError(AError.USERNAME_PASSWORD_MISMATCH, VMStr.byRes(R.string.account_username_password_mismatch));
                break;
            case AVException.USER_DOESNOT_EXIST:
                callback.onError(AError.USER_DOESNOT_EXIST, VMStr.byRes(R.string.account_user_doesnot_exist));
                break;
            default:
                callback.onError(AError.UNKNOWN, VMStr.byResArgs(R.string.unknown, e.getCode()));
                break;
        }
    }
}
