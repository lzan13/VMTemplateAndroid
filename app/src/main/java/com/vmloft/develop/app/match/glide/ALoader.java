package com.vmloft.develop.app.match.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.vmloft.develop.app.match.BuildConfig;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.app.App;
import com.vmloft.develop.app.match.common.AConstants;
import com.vmloft.develop.app.match.common.ASPManager;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Create by lzan13 on 2019/5/22 13:24
 *
 * 图片加载简单封装
 */
public class ALoader {

    /**
     * 加载图片
     *
     * @param context   上下文
     * @param options   加载图片配置
     * @param imageView 目标 view
     */
    public static void load(Context context, IPictureLoader.Options options, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions();
        if (options.isCircle) {
            requestOptions.circleCrop();
        } else if (options.isRadius) {
            requestOptions.transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(options.radiusSize)));
        }
        if (options.isBlur) {
            requestOptions.transform(new BlurTransformation());
        }
        GlideApp.with(context)
                .load(options.url)
                .apply(requestOptions)
                .thumbnail(placeholder(context, options))
                .into(imageView);
    }

    /**
     * 统一处理占位图
     *
     * @param context 上下文对象
     * @param options 加载配置
     * @return
     */
    private static RequestBuilder<Drawable> placeholder(Context context, IPictureLoader.Options options) {
        int resId = R.drawable.img_default_match;
        RequestOptions requestOptions = new RequestOptions();
        if (options.isCircle) {
            requestOptions.circleCrop();
        } else if (options.isRadius) {
            requestOptions.transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(options.radiusSize)));
        }
        if (options.isBlur) {
            requestOptions.transform(new BlurTransformation());
        }

        return GlideApp.with(context).load(resId).apply(requestOptions);
    }

    /**
     * 拼装图片路径
     */
    public static String wrapUrl(String name) {
        return BuildConfig.uploadUrl + name;
    }
}
