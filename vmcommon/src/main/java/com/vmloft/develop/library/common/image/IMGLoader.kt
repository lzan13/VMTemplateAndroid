package com.vmloft.develop.library.common.image

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.widget.ImageView

import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

import com.vmloft.develop.library.common.R
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.common.CError
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.tools.utils.VMDate
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMFile
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap
import com.vmloft.develop.library.tools.utils.logger.VMLog

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.File
import java.io.FileInputStream
import java.util.concurrent.CountDownLatch


/**
 * Create by lzan13 on 2019/5/22 13:24
 * 图片加载简单封装
 */
object IMGLoader {

    /**
     * 创建控件
     */
    fun createView(context: Context): ImageView {
        return ImageView(context)
    }

    /**
     * 保存图片
     */
    suspend fun savePicture(context: Context, url: String) = withContext(Dispatchers.IO) {
        // 保存图片结果
        var rResult: RResult<String> = RResult.Error(CError.ordinary, VMStr.byRes(R.string.picture_save_toast_failed))
        if (url.isNullOrEmpty()) {
            return@withContext rResult
        }
        val glideUrl = GlideUrl(
            wrapUrl(url),
            headers(url)
        )
        // 阻塞线层将 callback 转为同步
        val countDownLatch = CountDownLatch(1)

        GlideApp.with(context).downloadOnly().listener(object : RequestListener<File> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean): Boolean {
                val error = "${VMStr.byRes(R.string.picture_save_toast_failed)} ${e?.message}"
                VMLog.e(error)
                rResult = RResult.Error(CError.ordinary, error)
                // 解除锁
                countDownLatch.countDown()
                return true
            }

            override fun onResourceReady(
                resource: File?,
                model: Any?,
                target: Target<File>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                val fis = FileInputStream(resource)
                val bmp = BitmapFactory.decodeStream(fis)
                val filename = VMDate.filenameDateTime()
                var result = VMBitmap.saveBitmapToPictures(bmp, CConstants.projectDir, filename)
                val path = "${VMFile.pictures}${CConstants.projectDir}${File.separator}${filename}.jpg"
                if (result != null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        // 保存完成通知相册刷新
                        CUtils.notifyAlbum(context, path)
                    }
                    val tips = "${VMStr.byRes(R.string.picture_save_toast_succeed)} $path"

                    VMLog.d(tips)
                    rResult = RResult.Success(tips)
                } else {
                    val tips = "${VMStr.byRes(R.string.picture_save_toast_failed)} ${path}"
                    rResult = RResult.Error(CError.ordinary, tips)
                }
                // 解除锁
                countDownLatch.countDown()
                return true
            }
        }).load(glideUrl).preload()
        // 等待回调结束
        countDownLatch.await()
        rResult
    }

    /**
     * 加载圆形图，一般是头像
     *
     * @param iv 目标 view
     * @param avatar 图片地址
     */
    fun loadAvatar(
        iv: ImageView,
        avatar: Any?,
        isCircle: Boolean = true,
        isRadius: Boolean = false,
        radiusSize: Int = 4,
    ) {
        val options = Options(avatar, R.drawable.img_default_avatar, isCircle, isRadius, radiusSize)
        load(options, iv)
    }

    /**
     * 加载封面图
     *
     * @param iv 目标 view
     * @param cover 图片地址
     * @param isRadius 是否圆角
     * @param radiusSize 圆角大小
     * @param isBlur 是否模糊
     */
    fun loadCover(
        iv: ImageView,
        cover: Any?,
        isRadius: Boolean = false,
        radiusSize: Int = 8,
        radiusTL: Int = 0,
        radiusTR: Int = 0,
        radiusBL: Int = 0,
        radiusBR: Int = 0,
        isBlur: Boolean = false,
        thumbnailUrl: String = "",
        defaultResId: Int = R.drawable.img_default,
    ) {
        val options = Options(
            cover,
            defaultResId,
            isRadius = isRadius,
            radiusSize = radiusSize,
            radiusTL = radiusTL,
            radiusTR = radiusTR,
            radiusBL = radiusBL,
            radiusBR = radiusBR,
            isBlur = isBlur,
            thumbnailUrl = thumbnailUrl
        )
        load(options, iv)
    }

    /**
     * 加载缩略图
     *
     * @param iv 目标 view
     * @param path 图片地址
     * @param isRadius 是否圆角
     * @param radiusSize 圆角大小
     * @param isBlur 是否模糊
     */
    fun loadThumbnail(
        iv: ImageView,
        path: Any?,
        isRadius: Boolean = true,
        radiusSize: Int = 4,
        size: Int = 256,
    ) {
        val options = Options(path, isRadius = isRadius, radiusSize = radiusSize, isThumbnail = true, thumbnailSize = size)
        load(options, iv)
    }

    /**
     * 加载图片
     *
     * @param options   加载图片配置
     * @param imageView 目标 view
     */
    private fun load(options: Options, imageView: ImageView) {
        val requestOptions = RequestOptions()
        if (options.isCircle) {
            requestOptions.circleCrop()
        } else if (options.isRadius) {
            requestOptions.transform(MultiTransformation(CenterCrop(), RoundedCorners(VMDimen.dp2px(options.radiusSize))))
        }
        if (options.isBlur) {
            requestOptions.transform(BlurTransformation())
        }
        if (options.isThumbnail) {
            requestOptions.format(DecodeFormat.PREFER_RGB_565).override(options.thumbnailSize)
        }

        val wOptions = wrapOptions(options)
        if (options.defaultResId == 0) {
            GlideApp.with(imageView.context)
                .load(wOptions.res)
                .apply(requestOptions)
                .into(imageView)
        } else {
            val thumbnail = thumbnail(imageView.context, wOptions)
            if (options.isCircle || options.isRadius) {
                val placeholder = placeholder(imageView.context, wOptions)
                GlideApp.with(imageView.context)
                    .load(wOptions.res)
                    .thumbnail(thumbnail)
                    .thumbnail(placeholder)
                    .apply(requestOptions)
                    .into(imageView)
            } else {
                GlideApp.with(imageView.context)
                    .load(wOptions.res)
                    .apply(requestOptions)
                    .thumbnail(thumbnail)
                    .placeholder(wOptions.defaultResId)
                    .into(imageView)
            }
        }
    }

    /**
     * 处理占位图
     *
     * @param context 上下文对象
     * @param options 加载配置
     * @return
     */
    private fun placeholder(context: Context, options: Options): RequestBuilder<Drawable> {
        val requestOptions = RequestOptions()
        if (options.isCircle) {
            requestOptions.circleCrop()
        } else if (options.isRadius) {
            requestOptions.transform(MultiTransformation(CenterCrop(), RoundedCorners(VMDimen.dp2px(options.radiusSize))))
        }
        if (options.isBlur) {
            requestOptions.transform(BlurTransformation())
        }
        return GlideApp.with(context).load(options.defaultResId).apply(requestOptions)
    }

    /**
     * 处理缩略图
     *
     * @param context 上下文对象
     * @param options 加载配置
     * @return
     */
    private fun thumbnail(context: Context, options: Options): RequestBuilder<Drawable> {
        val requestOptions = RequestOptions()
        if (options.isCircle) {
            requestOptions.circleCrop()
        } else if (options.isRadius) {
            requestOptions.transform(MultiTransformation(CenterCrop(), RoundedCorners(VMDimen.dp2px(options.radiusSize))))
        }
        if (options.isBlur) {
            requestOptions.transform(BlurTransformation())
        }
        return GlideApp.with(context).load(options.thumbnailUrl).apply(requestOptions)
    }

    /**
     * 统一处理加载图片请求头
     */
    private fun headers(referer: String): LazyHeaders {
        return LazyHeaders.Builder()
            .addHeader("accept-encoding", "gzip, deflate, br")
            .addHeader("accept-language", "zh-CN,zh;q=0.9")
            .addHeader("referer", referer)
            .build()
    }

    /**
     * 包装下图片加载属性
     */
    private fun wrapOptions(options: Options): Options {
        if (options.res is String) {
            if ((options.res as String).indexOf("/uploads/") == 0) {
                options.res = CConstants.mediaHost() + options.res
            } else if ((options.res as String).indexOf("file:///") == 0 || (options.res as String).indexOf("content:///") == 0) {
                options.res = Uri.parse(options.res as String)
            }else if ((options.res as String).indexOf("/storage") == 0) {
                options.res = Uri.parse(options.res as String)
            }
        }
        return options
    }

    /**
     * 包装下图片地址
     */
    private fun wrapUrl(url: String): String {
        var wrapUrl = url
        if (url.indexOf("/uploads/") == 0) {
            wrapUrl = CConstants.mediaHost() + url
        }
        return wrapUrl
    }

    /**
     * 加载图片配置
     */
    data class Options(
        // 图片资源，可以为 Uri/String/resId
        var res: Any?,
        // 默认资源
        var defaultResId: Int = R.drawable.img_default,
        // 是否圆形
        var isCircle: Boolean = false,
        // 圆角
        var isRadius: Boolean = false,
        var radiusSize: Int = 4,
        var radiusTL: Int = 0,
        var radiusTR: Int = 0,
        var radiusBL: Int = 0,
        var radiusBR: Int = 0,
        // 是否模糊
        var isBlur: Boolean = false,
        // 缩略图
        var thumbnailUrl: String = "",
        var isThumbnail: Boolean = false,
        var thumbnailSize: Int = 256,

        // 参考参数，防盗链使用
        var referer: String = "",
    )

}