package com.vmloft.develop.library.im.common;

import com.vmloft.develop.library.tools.utils.VMSPUtil;
import com.vmloft.develop.library.tools.utils.VMSystem;

/**
 * Create by lzan13 on 2019/05/21
 *
 * SharedPreferences 配置管理类
 */
public class IMSPManager {

    private final String KEY_CURR_USER_ID = "key_curr_user_id";

    /**
     * 私有构造，初始化 ShredPreferences 文件名
     */
    private IMSPManager() {}

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        private static final IMSPManager INSTANCE = new IMSPManager();
    }

    /**
     * 获取的实例
     */
    public static final IMSPManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 保存当前登录账户 Id
     *
     * @param userId 当前账户 Id
     */
    public void putCurrUserId(String userId) {
        VMSPUtil.put(KEY_CURR_USER_ID, userId);
    }

    /**
     * 获取当前登录账户 Id
     *
     * @return 如果为空，说明没有登录
     */
    public String getCurrUserId() {
        return (String) VMSPUtil.get(KEY_CURR_USER_ID, "");
    }
}
