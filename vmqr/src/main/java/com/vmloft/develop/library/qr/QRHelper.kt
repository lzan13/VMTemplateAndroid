package com.vmloft.develop.library.qr

import android.app.Activity
import android.graphics.Bitmap

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.vmloft.develop.library.base.router.CRouter

import com.vmloft.develop.library.base.utils.CUtils
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.tools.utils.VMDimen

import org.json.JSONObject

/**
 * Create by lzan13 on 2022/3/10
 * 描述：二维码工具类
 */
object QRHelper {
    /**
     * 根据内容生成二维码，生成二维码为耗时操作，需要异步调用
     * @param content 生成二维码内容
     * @param type 二维码内容类型 0-用户信息 1-跳转链接
     */
    fun encodeQRCode(content: String, type: Int = 0): Bitmap {
        val bean = QRCodeBean(content, type)
        val base64Str = CUtils.base64Encode(JsonUtils.toJson(bean))
        val size = VMDimen.screenWidth - VMDimen.dp2px(24) * 2 - VMDimen.dp2px(16) * 4
        return QRCodeEncoder.syncEncodeQRCode(base64Str, size)
    }

    /**
     * 解析二维码结果
     */
    fun decodeQRCodeResult(result: String): QRCodeBean? {
        val content = CUtils.base64Decode(result)
        return JsonUtils.fromJson(content, QRCodeBean::class.java)
    }

    /**
     * 扫描二维码
     */
    fun scanQRCode(activity: Activity) {
        CRouter.goResult(activity, CRouter.qrScanQRCode, CRouter.qrScanQRCodeResCode)
    }
}

/**
 * 二维码数据 bean 类
 */
data class QRCodeBean(
    var content: String = "",
    var type: Int = 0,
) {}