package com.vmloft.develop.app.match.im;

import android.content.Context;
import android.widget.ImageView;

import com.vmloft.develop.app.match.glide.ALoader;
import com.vmloft.develop.library.im.IMIPictureLoader;

/**
 * Create by lzan13 on 2019/5/23 09:57
 *
 * 实现 IM 图片加载接口
 */
public class AIMPictureLoader implements IMIPictureLoader {
    @Override
    public void loadAvatar(Context context, String url, ImageView imageView, int width, int height) {
        ALoader.loadAvatar(context, url, imageView);

    }

    @Override
    public void loadThumb(Context context, String url, ImageView imageView, int width, int height) {
        ALoader.loadThumb(context, url, imageView);
    }

    @Override
    public void loadFull(Context context, String url, ImageView imageView, int width, int height) {
        ALoader.loadFull(context, url, imageView);
    }
}
