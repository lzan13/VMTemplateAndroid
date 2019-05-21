package com.vmloft.develop.library.im;

import android.content.Context;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.common.IMExecptionManager;
import com.vmloft.develop.library.im.common.IMExecutor;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Create by lzan13 on 2019/5/20 22:22
 *
 * 库入口类
 */
public class IM {

    private IM() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IM INSTANCE = new IM();
    }

    /**
     * 获取单例类实例
     */
    public static IM getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        IMHelper.getInstance().init(context);
    }

    /**
     * 返回是否成功登录过，同时没有调用 signOut()
     */
    public boolean isSignIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * 登录 IM
     */
    public void signIn(String id, String password, final IMCallback callback) {
        signOut(false);
        EMClient.getInstance().login(id, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onError(int code, String error) {
                IMExecptionManager.getInstance().disposeError(code, error, callback);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * 注册 IM
     */
    public void signUp(final String id, final String password, final IMCallback callback) {
        IMExecutor.asyncMultiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(id, password);
                    if (callback != null) {
                        callback.onSuccess(null);
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    IMExecptionManager.getInstance().disposeError(e.getErrorCode(), e.getDescription(), callback);
                }
            }
        });
    }

    /**
     * 退出登录
     *
     * @param unbuild 是否解绑推送 Token
     */
    public void signOut(boolean unbuild) {
        signOut(unbuild, null);
    }

    /**
     * 退出登录
     *
     * @param unbuild  是否解绑推送 Token
     * @param callback 退出登录回调
     */
    public void signOut(boolean unbuild, final IMCallback callback) {
        // 退出前检查一下是否已经成功登录
        if (!isSignIn()) {
            return;
        }
        EMClient.getInstance().logout(unbuild, new EMCallBack() {
            @Override
            public void onSuccess() {
                callback.onSuccess(null);
            }

            @Override
            public void onError(int code, String error) {
                IMExecptionManager.getInstance().disposeError(code, error, callback);
            }

            @Override
            public void onProgress(int progress, String status) {}
        });
    }
}
