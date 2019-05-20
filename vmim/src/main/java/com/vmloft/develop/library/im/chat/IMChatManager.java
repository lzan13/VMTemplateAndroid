package com.vmloft.develop.library.im.chat;

import android.content.Context;

/**
 * Create by lzan13 on 2019/5/9 10:38
 *
 * IM 聊天管理类
 */
public class IMChatManager {

    /**
     * 私有的构造方法
     */
    private IMChatManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMChatManager INSTANCE = new IMChatManager();
    }

    /**
     * 获取单例类实例
     */
    public static IMChatManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    public void init(Context context) {

    }

    /**
     *
     */
    public void addMessageListener() {

    }

    public void removeMessageListener() {

    }

}
