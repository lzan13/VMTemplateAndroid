package com.vmloft.develop.library.im;

import android.content.Context;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.call.IMCallManager;
import com.vmloft.develop.library.im.common.IMChatManager;
import com.vmloft.develop.library.im.common.IMExecptionManager;
import com.vmloft.develop.library.im.common.IMExecutor;
import com.vmloft.develop.library.im.common.IMSPManager;
import com.vmloft.develop.library.im.emoji.IMEmojiManager;
import com.vmloft.develop.library.tools.picker.VMPicker;

/**
 * Create by lzan13 on 2019/5/20 22:22
 *
 * 库入口类
 */
public class IM {

    // IM 全局回调接口
    private IMIGlobalListener mGlobalListener;
    // IM 图片加载接口
    private IMIPictureLoader mPictureLoader;

    private Context mContext;

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
        mContext = context;
        IMHelper.getInstance().init(context);
        // 通话管理类的初始化
        IMCallManager.getInstance().init();
        IMChatManager.getInstance().init();
        IMEmojiManager.getInstance().init();
    }

    /**
     * 获取上下文对象
     */
    public Context getIMContext() {
        return mContext;
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
    public void signIn(final String id, String password, final IMCallback callback) {
        signOut(false);
        EMClient.getInstance().login(id, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                IMSPManager.getInstance().putCurrUserId(id);
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
        IMSPManager.getInstance().putCurrUserId("");
        EMClient.getInstance().logout(unbuild, new EMCallBack() {
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
     * 设置 IM 全局回调接口实现
     */
    public void setGlobalListener(IMIGlobalListener listener) {
        mGlobalListener = listener;
    }

    /**
     * 设置 IM 图片加载接口实现
     */
    public void setPictureLoader(IMIPictureLoader loader) {
        mPictureLoader = loader;
        VMPicker.getInstance().setPictureLoader(mPictureLoader);
    }

    /**
     * 获取图片加载器
     */
    public IMIPictureLoader getPictureLoader() {
        return mPictureLoader;
    }

    /**
     * 获取 IM 联系人信息
     *
     * @param id
     */
    public void getIMContact(String id, IMCallback<IMContact> callback) {
        if (mGlobalListener != null) {
            mGlobalListener.getIMContact(id, callback);
        }
    }

    /**
     * 头像点击处理
     */
    public void onHeadClick(Context context, IMContact contact) {
        if (mGlobalListener == null) {
            return;
        }
        mGlobalListener.onHeadClick(context, contact);
    }

    /**
     * IM 头像点击
     *
     * @param context 上下文对象
     * @param contact 点击联系人对象
     */
    public void onIMHeadClick(Context context, IMContact contact) {
        if (mGlobalListener != null) {
            mGlobalListener.onHeadClick(context, contact);
        }
    }
}
