package com.vmloft.develop.app.match.im;

import android.content.Context;

import com.vmloft.develop.library.im.IM;

/**
 * Create by lzan13 on 2019/5/23 09:39
 */
public class AIMManager {

    private AIMManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        private static final AIMManager INSTANCE = new AIMManager();
    }

    /**
     * 获取的实例
     */
    public static final AIMManager getInstance() {
        return InnerHolder.INSTANCE;
    }


    /**
     * 初始化 IM
     */
    public void initIM(Context context) {
        IM.getInstance().init(context);
        initCallback();
    }

    /**
     * 初始化 IM 回调接口
     */
    private void initCallback() {
        IM.getInstance().setGlobalListener(new AIMGlobalListener());
        IM.getInstance().setPictureLoader(new AIMPictureLoader());
    }
}
