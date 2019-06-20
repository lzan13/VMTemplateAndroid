package com.vmloft.develop.app.match.app;

import com.tencent.bugly.crashreport.CrashReport;

import com.vmloft.develop.app.match.common.AConstants;
import com.vmloft.develop.app.match.im.AIMManager;
import com.vmloft.develop.library.tools.base.VMApp;

/**
 * Create by lzan13 on 2019/04/08
 * 程序入口
 */
public class App extends VMApp {

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        initBugly();

        // 初始化 IM
        AIMManager.getInstance().initIM(context);
    }


    /**
     * 初始化 Bugly 日志上报
     */
    private void initBugly() {
        CrashReport.initCrashReport(context, AConstants.APP_BUGLY_ID, false);
    }
}
