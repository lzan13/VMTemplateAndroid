package com.vmloft.develop.app.match.common;

/**
 * Create by lzan13 on 2019/5/9 13:47
 *
 * 用户管理类
 */
public class AUMSManager {

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        private static final AUMSManager INSTANCE = new AUMSManager();
    }

    /**
     * 获取的实例
     */
    public static final AUMSManager getInstance() {
        return InnerHolder.INSTANCE;
    }

}
