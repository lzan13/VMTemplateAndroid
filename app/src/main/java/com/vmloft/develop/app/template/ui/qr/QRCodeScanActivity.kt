package com.vmloft.develop.app.template.ui.qr

import cn.bingoogolapple.qrcode.core.QRCodeView

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BaseActivity
import com.vmloft.develop.library.common.image.IMGChoose
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap
import com.vmloft.develop.library.tools.utils.logger.VMLog

import kotlinx.android.synthetic.main.activity_qr_code_scan.*

/**
 * Create by lzan13 on 2021/6/6
 * 描述：二维码扫描界面
 */
@Route(path = AppRouter.appQRScan)
class QRCodeScanActivity : BaseActivity(), QRCodeView.Delegate {

    override fun layoutId(): Int = R.layout.activity_qr_code_scan


    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.qr_scan)

        initQRCodeScan()

        qrMineIV.setOnClickListener { CRouter.go(AppRouter.appQRMine) }
        qrAlbumIV.setOnClickListener { openAlbum() }
    }

    override fun initData() {

    }

    private fun initQRCodeScan() {
        // 设置识别结果代理
        qrCodeView.setDelegate(this)
        qrCodeView.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
//        qrCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        qrCodeView.startSpotAndShowRect() // 显示扫描框，并开始识别
    }

    /**
     * 打开相册识别二维码
     */
    private fun openAlbum() {
        IMGChoose.singlePicture(this) {
            val bitmap = VMBitmap.loadBitmapByFile(it)
            bitmap?.let {
                qrCodeView.decodeQRCode(bitmap)
            }
        }
    }

    /**
     * 处理扫描结果
     *
     * @param result 摄像头扫码时只要回调了该方法 result 就一定有值，不会为 null。解析本地图片或 Bitmap 时 result 可能为 null
     */
    override fun onScanQRCodeSuccess(result: String?) {
        VMLog.i("qr scan success $result")
        if (result.isNullOrEmpty()) {
            errorBar(R.string.qr_scan_err)
        } else {
            showBar(result)
        }
    }

    /**
     * 摄像头环境亮度发生变化
     *
     * @param isDark 是否变暗
     */
    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        VMLog.i("qr scan light $isDark")
    }

    /**
     * 处理打开相机出错
     */
    override fun onScanQRCodeOpenCameraError() {
        VMLog.i("qr scan open camera error")
        errorBar(R.string.qr_scan_camera_err)
    }

    override fun onRestart() {
        qrCodeView.startCamera()
        qrCodeView.startSpotAndShowRect()
        super.onRestart()
    }

    override fun onStop() {
        qrCodeView.stopCamera()
        super.onStop()
    }

    override fun onDestroy() {
        qrCodeView.onDestroy()
        super.onDestroy()
    }
}