package com.vmloft.develop.library.im.call;

import android.content.Context;

/**
 * Create by lzan13 on 2019/5/9 10:46
 *
 * IM 实时音视频通话管理类
 */
public class IMCallManager {

    /**
     * 私有的构造方法
     */
    private IMCallManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMCallManager INSTANCE = new IMCallManager();
    }

    /**
     * 获取单例类实例
     */
    public static IMCallManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    public void init(Context context) {

    }

}
