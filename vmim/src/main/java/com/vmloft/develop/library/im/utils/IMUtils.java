package com.vmloft.develop.library.im.utils;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.tools.utils.VMUtils;

/**
 * Create by lzan13 on 2019/5/28 22:19
 *
 * IM 工具类
 */
public class IMUtils extends VMUtils {

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
         * 收到新消息
         */
        public static String getNewMessageAction() {
            return actionPrefix() + "chat.new.message";
        }

        /**
         * 收到 CMD 新消息
         */
        public static String getCMDMessageAction() {
            return actionPrefix() + "chat.cmd.message";
        }

        /**
         * 消息状态更新
         */
        public static String getUpdateMessageAction() {
            return actionPrefix() + "chat.update.message";
        }

        /**
         * 刷新会话
         */
        public static String getRefreshConversationAction() {
            return actionPrefix() + "chat.refresh.conversation";
        }

        /**
         * 通话状态改变
         */
        public static String getCallStatusChangeAction() {
            return actionPrefix() + "call.status.change";
        }

        /**
         * 连接状态改变
         */
        public static String getConnectionChangeAction() {
            return actionPrefix() + "connection.status.change";
        }

        /**
         * 广播 action 前缀，其实就是 App 包名
         */
        private static String actionPrefix() {
            return IM.getInstance().getIMContext().getPackageName();
        }
    }
}
