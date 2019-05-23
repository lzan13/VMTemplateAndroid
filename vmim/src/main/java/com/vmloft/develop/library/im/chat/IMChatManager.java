package com.vmloft.develop.library.im.chat;

import android.content.Context;

import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMMessage;

import java.util.List;

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
     * 添加消息监听
     */
    public void addMessageListener() {

    }

    /**
     * 移除消息监听
     */
    public void removeMessageListener() {

    }

    /**
     * 发送文本消息
     *
     * @param text     文本内容
     * @param toId     接受者
     * @param callback 发送回调
     */
    public void sendText(String text, String toId, IMCallback<EMMessage> callback) {
        EMMessage message = EMMessage.createTxtSendMessage(text, toId);
        sendMessage(message, callback);
    }

    /**
     * 发送消息
     *
     * @param message  要发送的消息
     * @param callback 发送回调
     */
    public void sendMessage(EMMessage message, IMCallback<EMMessage> callback) {
        
    }
}
