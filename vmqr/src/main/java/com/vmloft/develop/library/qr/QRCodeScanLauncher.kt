package com.vmloft.develop.library.qr

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContract
import com.vmloft.develop.library.base.BResultLauncher

/**
 * Create by lzan13 on 2022/3/10
 * 描述：二维码扫描解析启动器
 */
class QRCodeScanLauncher(caller: ActivityResultCaller) : BResultLauncher<Int, String>(caller, QRCodeScanContract()) {
}

/**
 * 二维码扫描解析跳转协议
 */
class QRCodeScanContract : ActivityResultContract<Int, String>() {
    override fun createIntent(context: Context, input: Int?): Intent {
        return Intent(context, QRCodeScanActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String {
        return intent?.getStringExtra("result") ?: ""
    }
}