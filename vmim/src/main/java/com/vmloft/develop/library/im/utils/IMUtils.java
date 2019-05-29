package com.vmloft.develop.library.im.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.vmloft.develop.library.im.IM;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create by lzan13 on 2019/5/28 22:19
 *
 * IM 工具类
 */
public class IMUtils {

    /**
     * 注册本地广播接收器
     *
     * @param context 上下文对象
     * @param action  广播 action
     */
    public static void registerLocalReceiver(Context context, String action) {

    }

    /**
     * 简单的发送一个本地广播
     */
    public static void sendLocalBroadcast(String action) {
        sendLocalBroadcast(new Intent(action));
    }

    /**
     * 简单的发送一个本地广播
     */
    public static void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(IM.getInstance().getIMContext()).sendBroadcast(intent);
    }

    /**
     * IM 模块儿 Action 管理
     */
    public static class Action {

        /**
         * 收到 CMD 新消息
         */
        public static String getCMDMessageAction() {
            return actionPrefix() + "chat.cmd.message";
        }

        /**
         * 收到新消息
         */
        public static String getNewMessageAction() {
            return actionPrefix() + "chat.new.message";
        }

        /**
         * 收到新消息
         */
        public static String getUpdateMessageAction() {
            return actionPrefix() + "chat.new.message";
        }

        /**
         * 刷新会话
         */
        public static String getRefreshConversationAction() {
            return actionPrefix() + "chat.refresh.conversation";
        }

        private static String actionPrefix() {
            return IM.getInstance().getIMContext().getPackageName();
        }
    }
}
