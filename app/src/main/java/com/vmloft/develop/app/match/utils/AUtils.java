package com.vmloft.develop.app.match.utils;

import android.app.Activity;
import android.view.View;

import com.vmloft.develop.library.tools.utils.VMTheme;
import java.util.Random;

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

    /**
     * 生成 0-x 范围内的随机数
     *
     * @param end 最大值
     */
    public static int random(int end) {
        Random random = new Random();
        return random.nextInt(end);
    }

    /**
     * 生成制定范围内的随机数
     *
     * @param start 最小值
     * @param end   最大值
     */
    public static int random(int start, int end) {
        Random random = new Random();
        return random.nextInt(end) - start;
    }
}
