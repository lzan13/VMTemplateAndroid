package com.vmloft.develop.library.im;

import android.content.Context;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.call.IMCallManager;
import com.vmloft.develop.library.im.chat.IMChatManager;
import com.vmloft.develop.library.im.common.IMExecptionManager;
import com.vmloft.develop.library.im.common.IMExecutor;
import com.vmloft.develop.library.im.common.IMSPManager;
import com.vmloft.develop.library.im.emotion.IMEmotionManager;
import com.vmloft.develop.library.im.notify.IMNotifier;
import com.vmloft.develop.library.tools.picker.VMPicker;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.utils.VMSystem;

/**
 * Create by lzan13 on 2019/5/20 22:22
 *
 * 库入口类
 */
public class IM {

    // IM 全局回调接口
    private IIMGlobalListener mGlobalListener;
    // IM 图片加载接口
    private IIMPictureLoader mPictureLoader;

    private Context mContext;
    // 记录已经初始化
    private boolean isInit;
    // 当前聊天对象 Id
    private String mCurrChatId;

    private IM() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IM INSTANCE = new IM();
    }

    /**
     * 获取单例类实例
     */
    public static IM getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 初始化
     */
    public boolean init(Context context, IIMGlobalListener listener, IIMPictureLoader loader) {
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

        // 对外接口的初始化
        mGlobalListener = listener;
        mPictureLoader = loader;
        VMPicker.getInstance().setPictureLoader(mPictureLoader);

        if (isInit) {
            return isInit;
        }

        // 调用初始化方法初始化sdk
        EMClient.getInstance().init(context, optionConfig());

        // 设置开启debug模式
        EMClient.getInstance().setDebugMode(true);

        // IM 内部相关管理类的初始化
        IMChatManager.getInstance().init();
        IMCallManager.getInstance().init();
        IMEmotionManager.getInstance().init();
        IMNotifier.getInstance().init(context);

        // 初始化完成
        isInit = true;

        return isInit;
    }

    /**
     * 获取上下文对象
     */
    public Context getIMContext() {
        return mContext;
    }

    /**
     * 设置当前聊天对象 Id
     */
    public void setCurrChatId(String chatId) {
        mCurrChatId = chatId;
    }

    /**
     * 判断聊天状态，是否处于聊天中
     */
    public boolean isCurrChat(String chatId) {
        if (VMStr.isEmpty(mCurrChatId) || !mCurrChatId.equals(chatId)) {
            return false;
        }
        return true;
    }

    /**
     * 获取图片加载器
     */
    public IIMPictureLoader getPictureLoader() {
        return mPictureLoader;
    }

    /**
     * 同步获取 IM 自己的信息
     */
    public IMContact getIMSelfContact() {
        String id = IMSPManager.getInstance().getCurrUserId();
        if (mGlobalListener != null) {
            return mGlobalListener.getIMContact(id);
        }
        return new IMContact(id);
    }

    /**
     * 同步获取 IM 联系人信息
     */
    public IMContact getIMContact(String id) {
        if (mGlobalListener != null) {
            return mGlobalListener.getIMContact(id);
        }
        return new IMContact(id);
    }

    /**
     * 异步获取 IM 联系人信息
     */
    public void getIMContact(String id, IMCallback<IMContact> callback) {
        if (mGlobalListener != null) {
            mGlobalListener.getIMContact(id, callback);
        } else {
            callback.onSuccess(new IMContact(id));
        }
    }

    /**
     * IM 头像点击处理
     */
    public void onHeadClick(Context context, IMContact contact) {
        if (mGlobalListener != null) {
            mGlobalListener.onHeadClick(context, contact);
        }
    }

    /**
     * 返回是否成功登录过，同时没有调用 signOut()
     */
    public boolean isSignIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * 登录 IM
     */
    public void signIn(final String id, String password, final IMCallback callback) {
        signOut(false);
        EMClient.getInstance().login(id, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                IMSPManager.getInstance().putCurrUserId(id);
                // 因为这个必须要登录之后才能加载，所以这里也加载一次
                EMClient.getInstance().chatManager().loadAllConversations();
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onError(int code, String error) {
                IMExecptionManager.getInstance().disposeError(code, error, callback);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * 注册 IM
     */
    public void signUp(final String id, final String password, final IMCallback callback) {
        IMExecutor.asyncMultiTask(() -> {
            try {
                EMClient.getInstance().createAccount(id, password);
                if (callback != null) {
                    callback.onSuccess(null);
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
                IMExecptionManager.getInstance().disposeError(e.getErrorCode(), e.getDescription(), callback);
            }
        });
    }

    /**
     * 退出登录
     *
     * @param unbuild 是否解绑推送 Token
     */
    public void signOut(boolean unbuild) {
        signOut(unbuild, null);
    }

    /**
     * 退出登录
     *
     * @param unbuild  是否解绑推送 Token
     * @param callback 退出登录回调
     */
    public void signOut(boolean unbuild, final IMCallback callback) {
        // 退出前检查一下是否已经成功登录
        if (!isSignIn()) {
            return;
        }
        IMSPManager.getInstance().putCurrUserId("");
        EMClient.getInstance().logout(unbuild, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onError(int code, String error) {
                IMExecptionManager.getInstance().disposeError(code, error, callback);
            }

            @Override
            public void onProgress(int progress, String status) {
            }
        });
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
     * ---------------------------------------------------------------------
     * 判断是否打开通知
     */
    public boolean isNotify() {
        return IMSPManager.getInstance().getNotify();
    }

    /**
     * 设置开启通知
     */
    public void setNotify(boolean open) {
        IMSPManager.getInstance().putNotify(open);
    }

    /**
     * 判断是否打开通知
     */
    public boolean isNotifyDetail() {
        return IMSPManager.getInstance().getNotifyDetail();
    }

    /**
     * 设置开启通知
     */
    public void setNotifyDetail(boolean open) {
        IMSPManager.getInstance().putNotifyDetail(open);
    }

    /**
     * ---------------------------------------------------------------------
     * 判断是否开启圆形头像
     */
    public boolean isCircleAvatar() {
        return IMSPManager.getInstance().getCircleAvatar();
    }

    /**
     * 设置开启圆形头像
     */
    public void setCircleAvatar(boolean open) {
        IMSPManager.getInstance().putCircleAvatar(open);
    }
}
