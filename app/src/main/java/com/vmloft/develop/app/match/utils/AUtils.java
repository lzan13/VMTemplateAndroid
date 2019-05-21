package com.vmloft.develop.app.match.utils;

import android.app.Activity;
import android.view.View;

import com.vmloft.develop.library.tools.utils.VMTheme;

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
        VMTheme.setDarkStatusBar(activity, isDark);
    }
}
