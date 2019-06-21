package com.vmloft.develop.app.match.common;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.app.App;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.im.common.IMException;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

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
        if(e instanceof AException){
            disposeAException((AException) e, callback);
        } else if (e instanceof IMException) {
            disposeIMException((IMException) e, callback);
        } else {
            disposeThrowable(e, callback);
        }
    }


    /**
     * 请求出现异常错误处理
     *
     * @param e        异常
     * @param callback 回调
     */
    public void disposeThrowable(Throwable e, ACallback callback) {
        int code;
        String msg = "未知错误";
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            //httpException.response().errorBody().string()
            code = httpException.code();
            if (code == 500 || code == 404) {
                code = AException.SERVER;
                msg = VMStr.byResArgs(R.string.err_server, code);
            }
        } else if (e instanceof ConnectException) {
            code = AException.SYS_NETWORK;
            msg = VMStr.byRes(R.string.err_network_unusable);
        } else if (e instanceof SocketTimeoutException) {
            code = AException.SYS_TIMEOUT;
            msg = VMStr.byRes(R.string.err_network_timeout);
        } else {
            code = AException.UNKNOWN;
            msg = VMStr.byRes(R.string.unknown) + e.getMessage();
        }
        VMLog.e(msg);
        if (callback != null) {
            callback.onError(code, msg);
        }
    }

    /**
     * 处理 LeanCloud 异常情况
     *
     * @param e        异常情况
     * @param callback 自定义的回调接口
     */
    private void disposeAException(AException e, ACallback callback) {
        int code = e.getCode();
        String desc = e.getDesc();
        switch (code) {
            case AException.UNKNOWN:
                break;
            case AException.SYS_NETWORK:
                break;
            case AException.SYS_TIMEOUT:
                break;
            case AException.SERVER:
                break;
            case AException.SERVER_DB:
                break;
            case AException.INVALID_PARAM:
                break;
            case AException.TOKEN_INVALID:
            case AException.TOKEN_EXPIRED:
                ASignManager.getInstance().signOut();
                ARouter.goMain(App.getTopActivity());
                break;
            case AException.NOT_PERMISSION:
                break;
            case AException.ACCOUNT_EXIST:
                break;
            case AException.ACCOUNT_NAME_EXIST:
                break;
            case AException.ACCOUNT_NOT_EXIST:
                break;
            case AException.ACCOUNT_DELETED:
                break;
            case AException.ACCOUNT_NO_ACTIVATED:
                break;
            case AException.INVALID_ACTIVATE_LINK:
                break;
            case AException.INVALID_PASSWORD:
                break;
            default:
                break;
        }
        //        VMToast.make(msg).showError();
        callback.onError(code, desc);
        VMLog.e("错误信息 [%d] - %s", code, desc);
        if (callback != null) {
            callback.onError(code, desc);
        }
    }

    /**
     * 处理 LeanCloud 异常情况
     *
     * @param e        异常情况
     * @param callback 自定义的回调接口
     */
    private void disposeIMException(IMException e, ACallback callback) {
        int code;
        String desc;
        switch (e.getCode()) {
            case AException.UNKNOWN:
                code = AException.UNKNOWN;
                desc = VMStr.byResArgs(R.string.unknown, e.getCode());
                break;
            default:
                code = e.getCode();
                desc = e.getDesc();
                break;
        }
        VMLog.e("IM 相关错误信息 [%d] - %s", code, desc);
        if (callback != null) {
            callback.onError(code, desc);
        }
    }
}
