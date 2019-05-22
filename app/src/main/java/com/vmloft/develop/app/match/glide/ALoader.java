package com.vmloft.develop.app.match.glide;

import android.content.Context;
import android.widget.ImageView;

/**
 * Create by lzan13 on 2019/5/22 13:24
 *
 * 图片加载简单封装
 */
public class ALoader {

    /**
     * 加载缩略图，这样不需要加载完整的大图，节省时间
     *
     * @param context   上下文
     * @param url       图片地址
     * @param imageView 目标 view
     */
    public static void loadThumb(Context context, String url, ImageView imageView) {
        GlideApp.with(context).load(url).into(imageView);
    }

    /**
     * 加载完整的图片
     *
     * @param context   上下文
     * @param url       图片地址
     * @param imageView 目标 view
     */
    public static void loadFull(Context context, String url, ImageView imageView) {
        GlideApp.with(context).load(url).into(imageView);
    }

}
