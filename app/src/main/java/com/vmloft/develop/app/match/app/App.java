package com.vmloft.develop.app.match.app;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.vmloft.develop.app.match.bean.AMatch;
import com.vmloft.develop.app.match.bean.AUser;
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
        // 初始化 LeanCloud
        initLeanCloud();

        // 初始化 IM
        AIMManager.getInstance().initIM(context);

    }


    /**
     * 初始化 LeanCloud
     */
    private void initLeanCloud() {
        AVObject.registerSubclass(AMatch.class);
        AVUser.registerSubclass(AUser.class);
        AVUser.alwaysUseSubUserClass(AUser.class);
        AVOSCloud.initialize(context, AConstants.APP_LC_ID, AConstants.APP_LC_KEY);
    }
}
