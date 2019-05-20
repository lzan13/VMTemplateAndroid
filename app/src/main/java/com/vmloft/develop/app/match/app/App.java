package com.vmloft.develop.app.match.app;

import com.avos.avoscloud.AVOSCloud;
import com.vmloft.develop.app.match.common.AConstant;
import com.vmloft.develop.library.im.IM;
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
        // 初始化 IM
        initIM();
        // 初始化 LeanCloud
        initLeanCloud();
    }

    /**
     * 初始化 IM
     */
    private void initIM() {
        IM.getInstance().init(context);
    }

    /**
     * 初始化 LeanCloud
     */
    private void initLeanCloud() {
        AVOSCloud.initialize(context, AConstant.APP_LC_ID, AConstant.APP_LC_KEY);
    }
}
