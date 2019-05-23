package com.vmloft.develop.app.match.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

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
        GlideApp.with(context)
                .load(url)
//                .apply(new RequestOptions().circleCrop())
                .transform(new CircleCrop(), new BlurTransformation(context))
                .into(imageView);
    }

    /**
     * 加载缩略图，这样不需要加载完整的大图，节省时间
     *
     * @param context   上下文
     * @param url       图片地址
     * @param imageView 目标 view
     */
    public static void loadThumb(Context context, String url, ImageView imageView) {
        GlideApp.with(context)
                .load(url)
                .apply(new RequestOptions().bitmapTransform(new RoundedCorners(24)))
                .into(imageView);
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
