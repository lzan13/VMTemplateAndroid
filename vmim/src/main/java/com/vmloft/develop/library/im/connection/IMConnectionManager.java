package com.vmloft.develop.library.im.connection;

import android.content.Context;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.tools.utils.VMLog;

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

    public void init() {
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
                VMLog.d("与聊天服务器链接成功");
                IMUtils.sendLocalBroadcast(IMUtils.Action.getConnectionChangeAction());
            }

            @Override
            public void onDisconnected(int errorCode) {
                VMLog.d("与聊天服务器断开链接 - %d", errorCode);
                if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 其他设备登录，自己被踢
                    IM.getInstance().signOut(false);
                } else if (errorCode == EMError.USER_REMOVED) {
                    // 账户被强制下线
                    IM.getInstance().signOut(false);
                } else {
                    // 连接不到服务器
                }
                IMUtils.sendLocalBroadcast(IMUtils.Action.getConnectionChangeAction());
            }
        });
    }

    /**
     * 判断是否链接到 IM 服务器
     */
    public boolean isConnected() {
        return EMClient.getInstance().isConnected();
    }
}
