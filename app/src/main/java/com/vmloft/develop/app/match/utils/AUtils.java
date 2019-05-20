package com.vmloft.develop.app.match.utils;

import android.app.Activity;
import android.view.View;

/**
 * Create by lzan13 on 2019/04/08
 *
 * 项目工具类
 */
public class AUtils {

    /**
     * 设置状态栏为黑色图标和文字
     */
    public static void setDarkStatusBar(Activity activity, boolean isDark) {
        if (isDark) {
            // 1、设置状态栏文字深色，同时保留之前的 flag
            int originFlag = activity.getWindow().getDecorView().getSystemUiVisibility();
            activity.getWindow()
                .getDecorView()
                .setSystemUiVisibility(originFlag | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //2、清除状态栏文字深色，同时保留之前的flag
            int originFlag = activity.getWindow().getDecorView().getSystemUiVisibility();
            //使用异或清除SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.getWindow()
                .getDecorView()
                .setSystemUiVisibility(originFlag ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
