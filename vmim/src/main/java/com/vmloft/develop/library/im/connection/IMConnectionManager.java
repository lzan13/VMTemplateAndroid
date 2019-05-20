package com.vmloft.develop.library.im.connection;

import android.content.Context;

/**
 * Create by lzan13 on 2019/5/9 10:58
 *
 * IM 链接监听管理类
 */
public class IMConnectionManager {

    /**
     * 私有的构造方法
     */
    private IMConnectionManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMConnectionManager INSTANCE = new IMConnectionManager();
    }

    /**
     * 获取单例类实例
     */
    public static IMConnectionManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    public void init(Context context) {

    }
}
