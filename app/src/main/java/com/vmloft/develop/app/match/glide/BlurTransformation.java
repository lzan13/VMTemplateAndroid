package com.vmloft.develop.app.match.glide;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import com.vmloft.develop.library.tools.utils.bitmap.VMBlur;
import java.security.MessageDigest;

/**
 * Create by lzan13 on 2019/5/23 17:56
 *
 * 使用 RenderScript 模糊图片
 */
public class BlurTransformation extends BitmapTransformation {

    private static final String ID = "com.vmloft.develop.app.match.glide.BlurTransformation";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap blurBitmap = VMBlur.stackBlurBitmap(toTransform, 20, 8, false);
        return blurBitmap;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
