package com.vmloft.develop.library.common.image

import android.graphics.Bitmap
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.vmloft.develop.library.tools.utils.bitmap.VMBlur.stackBlurBitmap
import java.security.MessageDigest

/**
 * Create by lzan13 on 2020/04/06 17:56
 * 描述：使用 RenderScript 模糊图片
 */
class BlurTransformation : BitmapTransformation() {
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val blurBitmap = stackBlurBitmap(toTransform, 15, 10, false)
        return blurBitmap!!
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    companion object {
        private const val ID = "com.vmloft.develop.app.apptemplate.glide.BlurTransformation"
        private val ID_BYTES =
            ID.toByteArray(Key.CHARSET)
    }
}