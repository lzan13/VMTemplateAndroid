package com.vmloft.develop.app.match.common;

import com.vmloft.develop.library.tools.utils.VMSPUtil;
import com.vmloft.develop.library.tools.utils.VMSystem;

/**
 * Create by lzan13 on 2019/04/09
 *
 * SharedPreferences 配置管理类
 */
public class ASPManager {

    private final String KEY_RUN_VERSION = "key_run_version";
    private final String KEY_PREV_USER = "key_prev_user";
    private final String KEY_CURR_USER = "key_curr_user";

    /**
     * 私有构造，初始化 ShredPreferences 文件名
     */
    private ASPManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        private static final ASPManager INSTANCE = new ASPManager();
    }

    /**
     * 获取的实例
     */
    public static final ASPManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 保存当前运行版本
     */
    public void putRunVersion(long version) {
        VMSPUtil.put(KEY_RUN_VERSION, version);
    }

    /**
     * 获取当前运行的版本号
     */
    public long getRunVersion() {
        return (long) VMSPUtil.get(KEY_RUN_VERSION, 0l);
    }

    /**
     * 判断启动时是否需要展示引导界面
     */
    public boolean isShowGuide() {
        // 上次运行保存的版本号
        long runVersion = getRunVersion();
        // 程序当前版本
        long version = VMSystem.getVersionCode();
        if (version > runVersion) {
            return true;
        }
        return false;
    }

    /**
     * 设置 Guide 状态
     */
    public void setGuideState() {
        // 保存新的版本
        putRunVersion(VMSystem.getVersionCode());
    }

    /**
     * 上一个账户登录记录
     *
     * @return 如果为空，说明没有登录记录
     */
    public String getPrevUser() {
        return (String) VMSPUtil.get(KEY_PREV_USER, "");
    }

    public void putPrevUser(String account) {
        VMSPUtil.put(KEY_PREV_USER, account);
    }

    /**
     * 当前账户登录记录
     *
     * @return 如果为空，说明没有登录记录
     */
    public String getCurrUser() {
        return (String) VMSPUtil.get(KEY_CURR_USER, "");
    }
    public void putCurrUser(String account) {
        VMSPUtil.put(KEY_CURR_USER, account);
    }


}
