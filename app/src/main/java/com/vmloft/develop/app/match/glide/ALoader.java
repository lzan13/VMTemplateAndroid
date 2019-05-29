package com.vmloft.develop.app.match.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.vmloft.develop.library.tools.utils.VMDimen;

/**
 * Create by lzan13 on 2019/5/22 13:24
 *
 * 图片加载简单封装
 */
public class ALoader {

    /**
     * 加载头像
     *
     * @param context   上下文
     * @param url       图片地址
     * @param imageView 目标 view
     */
    public static void loadAvatar(Context context, String url, ImageView imageView) {
        // 使用 Glide 变换 api 实现原型头像加载
        RequestOptions options = new RequestOptions();
        options.circleCrop();
        GlideApp.with(context).load(url).apply(options).into(imageView);
    }

    /**
     * 加载缩略图，这样不需要加载完整的大图，节省时间
     *
     * @param context   上下文
     * @param url       图片地址
     * @param imageView 目标 view
     */
    public static void loadThumb(Context context, String url, ImageView imageView) {
        int radius = VMDimen.dp2px(8);
        RequestOptions options = new RequestOptions();
        options.transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(radius)));
        GlideApp.with(context).load(url).apply(options).into(imageView);
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
