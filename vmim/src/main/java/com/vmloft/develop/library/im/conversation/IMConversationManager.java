package com.vmloft.develop.library.im.conversation;

import android.content.Context;

/**
 * Create by lzan13 on 2019/5/9 10:24
 *
 * IM 最近会话管理类
 */
public class IMConversationManager {

    /**
     * 私有的构造方法
     */
    private IMConversationManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMConversationManager INSTANCE = new IMConversationManager();
    }

    /**
     * 获取单例类实例
     */
    public static IMConversationManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    public void init(Context context) {

    }
}
