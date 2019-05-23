package com.vmloft.develop.library.im;

import android.content.Context;
import android.widget.ImageView;

import com.vmloft.develop.library.tools.picker.IPictureLoader;

/**
 * Create by lzan13 on 2019/5/23 09:42
 *
 * IM 图片加载接口，需 app 实现，减少 IM 引入三方库
 */
public interface IMIPictureLoader extends IPictureLoader {
    /**
     * 加载头像
     *
     * @param context   上下文对象
     * @param url       图片地址
     * @param imageView 加载图片控件
     * @param width     加载宽高
     * @param height
     */
    void loadAvatar(Context context, String url, ImageView imageView, int width, int height);

}
