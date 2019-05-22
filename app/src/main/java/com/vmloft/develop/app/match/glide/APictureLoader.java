package com.vmloft.develop.app.match.glide;

import android.content.Context;
import android.widget.ImageView;

import com.vmloft.develop.library.tools.picker.IPictureLoader;

/**
 * Create by lzan13 on 2019/5/22 11:57
 *
 * 实现图片选择器加载图片接口
 */
public class APictureLoader implements IPictureLoader {
    @Override
    public void loadThumb(Context context, String path, ImageView imageView, int width, int height) {
        ALoader.loadThumb(context, path, imageView);
    }

    @Override
    public void loadFull(Context context, String path, ImageView imageView, int width, int height) {
        ALoader.loadFull(context, path, imageView);
    }

}
