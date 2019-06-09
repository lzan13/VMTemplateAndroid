package com.vmloft.develop.app.match.router;

import android.content.Context;

import android.content.Intent;

import com.vmloft.develop.app.match.ui.guide.GuideActivity;
import com.vmloft.develop.app.match.ui.main.MainActivity;
import com.vmloft.develop.app.match.ui.main.home.MatchActivity;
import com.vmloft.develop.app.match.ui.main.me.MeInfoActivity;
import com.vmloft.develop.app.match.ui.setting.AboutSettingActivity;
import com.vmloft.develop.app.match.ui.setting.ChatSettingActivity;
import com.vmloft.develop.app.match.ui.setting.NotifySettingActivity;
import com.vmloft.develop.app.match.ui.setting.SettingActivity;
import com.vmloft.develop.app.match.ui.sign.SignInActivity;
import com.vmloft.develop.app.match.ui.sign.SignUpActivity;
import com.vmloft.develop.app.match.ui.user.UserDetailActivity;
import com.vmloft.develop.library.tools.router.VMParams;
import com.vmloft.develop.library.tools.router.VMRouter;

/**
 * Create by lzan13 on 2019/04/09
 *
 * 项目路由
 */
public class ARouter extends VMRouter {

    /**
     * 跳转到主界面
     */
    public static void goMain(Context context) {
        forward(context, MainActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    /**
     * 跳转到引导页
     */
    public static void goGuide(Context context) {
        forward(context, GuideActivity.class);
    }

    /**
     * 跳转到登录界面
     */
    public static void goSignIn(Context context) {
        forward(context, SignInActivity.class);
    }

    /**
     * 跳转到注册界面
     */
    public static void goSignUp(Context context) {
        overlay(context, SignUpActivity.class);
    }

    /**
     * 个人信息
     */
    public static void goMeInfo(Context context) {
        overlay(context, MeInfoActivity.class);
    }

    /**
     * 用户信息
     */
    public static void goUserDetail(Context context, String id) {
        VMParams params = new VMParams();
        params.str0 = id;
        overlay(context, UserDetailActivity.class, params);
    }

    /**
     * 去匹配
     */
    public static void goMatch(Context context, VMParams params) {
        overlay(context, MatchActivity.class, params);
    }

    /**
     * 设置界面
     */
    public static void goSetting(Context context) {
        overlay(context, SettingActivity.class);
    }

    /**
     * 设置通知界面
     */
    public static void goNotifySetting(Context context) {
        overlay(context, NotifySettingActivity.class);
    }

    /**
     * 设置关于界面
     */
    public static void goChatSetting(Context context) {
        overlay(context, ChatSettingActivity.class);
    }

    /**
     * 设置关于界面
     */
    public static void goAboutSetting(Context context) {
        overlay(context, AboutSettingActivity.class);
    }
}
