package com.vmloft.develop.library.common.image

import android.widget.ImageView
import androidx.databinding.BindingAdapter


/**
 * Create by lzan13 on 2020/4/7 16:08
 * 描述：databinding 绑定图片加载
 */
object IMGLoaderAdapter {

    @BindingAdapter("android:src")
    @JvmStatic
    fun setSrc(view: ImageView, resId: Int) {
        view.setImageResource(resId)
    }

    @BindingAdapter("avatarUrl")
    @JvmStatic
    fun loadAvatar(view: ImageView, url: String?) {
        IMGLoader.loadAvatar(view, url)
    }

    /**
     * 加载封面图
     */
    @BindingAdapter("coverUrl")
    @JvmStatic
    fun loadCover(view: ImageView, url: String) {
        IMGLoader.loadCover(view, url)
    }

}