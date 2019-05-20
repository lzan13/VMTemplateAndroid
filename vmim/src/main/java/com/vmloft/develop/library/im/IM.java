package com.vmloft.develop.library.im;

import android.content.Context;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.common.IMErrorManager;

/**
 * Create by lzan13 on 2019/5/20 22:22
 *
 * 库入口类
 */
public class IM {

    private IM() {}

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
     * 登录 IM
     */
    public void signIn(String id, String password, final IMCallback callback) {
        EMClient.getInstance().login(id, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onError(int code, String error) {
                IMErrorManager.getInstance().disposeError(code, error, callback);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * 注册 IM
     */
    public void signUp(String id, String password, final IMCallback callback) {
        try {
            EMClient.getInstance().createAccount(id, password);
            if (callback != null) {
                callback.onSuccess(null);
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
            IMErrorManager.getInstance().disposeError(e.getErrorCode(), e.getDescription(), callback);
        }
    }

    /**
     * 退出登录
     */
    public void signOut() {
        EMClient.getInstance().logout(false);
    }
}
