package com.vmloft.develop.library.im;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.vmloft.develop.library.im.call.IMCallManager;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMSystem;

import java.util.List;

/**
 * Create on lzan13 on 2019/05/09 10:08
 *
 * IM 模块帮助里，主要做一些初始化定义工作
 */
public class IMHelper {

    private Context mContext;
    // 记录已经初始化
    private boolean isInit;
    // 记录是否处于聊天界面
    private boolean isChat = false;

    /**
     * 私有的构造方法
     */
    private IMHelper() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMHelper INSTANCE = new IMHelper();
    }

    /**
     * 获取单例类实例
     */
    public static IMHelper getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * IM 初始化
     *
     * @param context 上下文菜单
     * @return 返回初始化状态是否成功
     */
    public synchronized boolean init(Context context) {
        VMLog.d("im init start -----");
        mContext = context;
        // 获取当前进程 id 并取得进程名
        String processName = VMSystem.getProcessName();
        /**
         * 如果app启用了远程的service，此application:onCreate会被调用2次
         * 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
         * 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
         */
        if (processName == null || !processName.equalsIgnoreCase(context.getPackageName())) {
            // 则此 application 是被 Service 调用的，直接返回
            return true;
        }
        if (isInit) {
            return isInit;
        }

        // 调用初始化方法初始化sdk
        EMClient.getInstance().init(context, optionConfig());

        // 设置开启debug模式
        EMClient.getInstance().setDebugMode(true);

        // 通话管理类的初始化
        IMCallManager.getInstance().init(context);


        // 初始化完成
        isInit = true;
        VMLog.d("im init end =====");
        return isInit;
    }

    /**
     * IM 配置
     */
    private EMOptions optionConfig() {
        /**
         * SDK初始化的一些配置
         * 关于 EMOptions 可以参考官方的 API 文档
         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
         */
        EMOptions options = new EMOptions();

        // 是否启动 DNS 信息配置，如果是私有化部署，这里要设置为 false
        options.enableDNSConfig(true);
        // 设置私有化 IM 地址
        //options.setIMServer("im1.easemob.com");
        // 设置私有化 IM 端口号
        //options.setImPort(443);
        // 设置私有化 Rest 地址+端口号
        //options.setRestServer("a1.easemob.com:80");
        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
        //options.setAppKey("yunshangzhijia#yunyue");

        // 设置只使用 https
        options.setUsingHttpsOnly(false);
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否按照服务器时间排序，false按照本地时间排序，默认 true
        options.setSortMessageByServerTime(false);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执
        options.setRequireDeliveryAck(true);
        // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
        options.setAcceptInvitationAlways(true);
        // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
        options.setAutoAcceptGroupInvitation(true);
        // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
        options.setDeleteMessagesAsExitGroup(false);
        // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
        options.allowChatroomOwnerLeave(true);

        return options;
    }


    /**
     * 设置聊天状态
     *
     * @param chat 是否处于聊天状态
     */
    public void setChatStatus(boolean chat) {
        isChat = chat;
    }

    /**
     * 判断聊天状态，是否处于聊天中
     */
    public boolean isChat() {
        return isChat;
    }

}
