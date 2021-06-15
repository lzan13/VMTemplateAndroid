package com.vmloft.develop.library.common.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Base64

import com.vmloft.develop.library.tools.utils.VMTheme

import java.io.File
import java.io.FileNotFoundException
import java.net.URI
import java.util.*


/**
 * Create by lzan13 on 2020-02-15 19:29
 * 描述：通用工具类
 */
object CUtils {
    /**
     * 设置状态栏黑色模式
     */
    fun setDarkMode(activity: Activity, dark: Boolean) {
        VMTheme.setDarkStatusBar(activity, dark)
    }

    /**
     * 生成 [0, max) 范围内的随机数
     *
     * @param max 最大值（不包含）
     */
    fun random(max: Int): Int {
        return random(0, max)
    }

    /**
     * 生成制定范围内的随机数 范围 [start, end)
     *
     * @param min 最小值（包含）
     * @param max 最大值（不包含）
     */
    fun random(min: Int, max: Int): Int {
        val random = Random()
        return min + random.nextInt(max - min)
    }

    /**
     * 通知相册刷新
     */
    fun notifyAlbum(context: Context, path: String) {
        var intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(path)))
        context.sendBroadcast(intent)
    }

    /**
     * 判断文件是否存在，Android Q 以上需要特殊处理
     */
    fun isFileExists(context: Context, uri: Uri): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || uri.toString().indexOf("content://") != -1) {
            try {
                var pfd: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(uri, "r")
                pfd?.close()
            } catch (e: FileNotFoundException) {
                return false
            }
            return true
        } else {
            val file = File(URI.create(uri.toString()))
            return file.exists()
        }
    }

    /**
     * 编码
     */
    fun base64Encode(str: String): String {
        return Base64.encodeToString(str.toByteArray(), Base64.DEFAULT)
    }

    /**
     * 解码
     */
    fun base64Decode(str: String): String {
        return String(Base64.decode(str, Base64.DEFAULT))
    }

}