package com.vmloft.develop.library.common.common

import android.Manifest
import android.content.Context
import com.vmloft.develop.library.tools.permission.VMPermission
import com.vmloft.develop.library.tools.permission.VMPermission.PCallback
import com.vmloft.develop.library.tools.permission.VMPermissionBean
import java.util.*

/**
 * Create by lzan13 2020/02/23
 * 描述：权限管理类
 */
object PermissionManager {

    /**
     * 检查写入权限
     */
    fun storagePermission(context: Context): Boolean {
        val bean = VMPermissionBean(Manifest.permission.WRITE_EXTERNAL_STORAGE, "读写手机存储", "发送和保存图片需要读写手机存储，请允许我们访问读写手机存储权限，否则你将无法使用应用")
        val result = checkPermission(context, bean)
        if (!result) {
            // 没有权限，去请求读写存储权限
            requestPermission(context, bean)
        }
        return result
    }

    /**
     * 检查相机权限
     */
    fun cameraPermission(context: Context): Boolean {
        val bean = VMPermissionBean(Manifest.permission.CAMERA, "访问相机", "拍摄照片需要访问相机，请允许我们获取访问相机权限，否则你将无法使用应用")
        val result = checkPermission(context, bean)
        if (!result) {
            // 没有权限，去请求相机权限
            requestPermission(context, bean)
        }
        return result
    }

    /**
     * 检查录音权限
     */
    fun recordPermission(context: Context): Boolean {
        val bean = VMPermissionBean(
            Manifest.permission.RECORD_AUDIO,
            "访问麦克风",
            "发送语音消息需要录音，请允许我们访问麦克风权限，否则你将无法使用应用"
        )
        val result = checkPermission(context, bean)
        if (!result) {
            // 没有权限，去请求权限
            requestPermission(context, bean)
        }
        return result
    }

    /**
     * 请求项目需要的权限
     */
    fun requestPermissions(context: Context?) {
        val list: MutableList<VMPermissionBean> = ArrayList()
        list.add(VMPermissionBean(Manifest.permission.CAMERA, "访问相机", "拍摄照片需要访问相机，请允许我们获取访问相机权限，否则你将无法使用应用"))
        list.add(VMPermissionBean(Manifest.permission.WRITE_EXTERNAL_STORAGE, "读写手机存储", "发送和保存图片需要读写手机存储，请允许我们访问读写手机存储权限，否则你将无法使用应用"))
        list.add(VMPermissionBean(Manifest.permission.RECORD_AUDIO, "访问麦克风", "发送语音消息需要录音，请允许我们访问麦克风权限，否则你将无法使用应用"))
        VMPermission.getInstance(context).setEnableDialog(false).setPermissionList(list).requestPermission(object : PCallback {
            override fun onReject() {}
            override fun onComplete() {}
        })
    }

    /**
     * 统一检查权限实现
     *
     * @param context 上下文对象
     * @param bean    权限实体
     * @return 是否授权
     */
    private fun checkPermission(context: Context, bean: VMPermissionBean): Boolean {
        return VMPermission.getInstance(context).setPermission(bean).checkPermission(bean.permission)
    }

    /**
     * 请求单个权限，这个主要在调用某项功能检查权限未被授予的情况下调用
     *
     * @param context 上下文对象
     * @param bean    需要请求的权限实体类
     */
    private fun requestPermission(context: Context, bean: VMPermissionBean) {
        VMPermission.getInstance(context)
            .setPermission(bean)
            .requestPermission(object : PCallback {
                override fun onReject() {}
                override fun onComplete() {}
            })
    }

}