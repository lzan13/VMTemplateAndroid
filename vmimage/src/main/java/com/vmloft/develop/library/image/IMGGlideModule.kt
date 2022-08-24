package com.vmloft.develop.library.image

import android.content.Context
import android.util.Log

import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule

import com.vmloft.develop.library.base.common.CConstants

/**
 * Create by lzan13 on 2020/04/06 17:56
 * 描述：GlideModele 实现
 */
@GlideModule
class IMGGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        //        super.applyOptions(context, builder);
        val size = 256 * 1024 * 1024
        val dir = CConstants.cacheImageDir
        builder.setDiskCache(ExternalPreferredCacheDiskCacheFactory(context, dir, size.toLong()))
        builder.setLogLevel(Log.ERROR)
    }

}