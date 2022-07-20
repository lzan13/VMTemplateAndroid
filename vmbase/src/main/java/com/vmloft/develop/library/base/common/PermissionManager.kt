package com.vmloft.develop.library.base.common

import android.Manifest
import android.content.Context

import com.vmloft.develop.library.tools.permission.VMPermission
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
        val bean = VMPermissionBean(Manifest.permission.WRITE_EXTERNAL_STORAGE, "读写手机存储", "选择图片需要读写手机存储，请允许我们访问读写手机存储权限，否则你将无法使用应用部分功能")
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
        val bean = VMPermissionBean(Manifest.permission.CAMERA, "访问相机", "拍摄照片需要访问相机，请允许我们获取访问相机权限，否则你将无法使用应用部分功能")
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
            "语音消息与通话需要使用麦克风的录音功能，请允许我们访问麦克风权限，否则你将无法使用应用部分功能"
        )
        val result = checkPermission(context, bean)
        if (!result) {
            // 没有权限，去请求权限
            requestPermission(context, bean)
        }
        return result
    }

    /**
     * 请求单个权限，这个主要在调用某项功能检查权限未被授予的情况下调用
     *
     * @param context 上下文对象
     * @param bean    需要请求的权限实体类
     */
    fun requestPermission(context: Context, bean: VMPermissionBean, callback: (Boolean) -> Unit = {}) {
        VMPermission.addPermission(bean).requestPermission(context, callback)
    }

    /**
     * 请求项目需要的权限
     */
    fun requestPermissions(context: Context, callback: (Boolean) -> Unit = {}) {
        val list: MutableList<VMPermissionBean> = ArrayList()
        list.add(VMPermissionBean(Manifest.permission.CAMERA, "访问相机", "拍摄照片需要访问相机，请允许我们获取访问相机权限，否则你将无法使用应用部分功能"))
        list.add(VMPermissionBean(Manifest.permission.WRITE_EXTERNAL_STORAGE, "读写手机存储", "选择图片需要读写手机存储，请允许我们访问读写手机存储权限，否则你将无法使用应用部分功能"))
        list.add(VMPermissionBean(Manifest.permission.RECORD_AUDIO, "访问麦克风", "语音消息与通话需要使用麦克风的录音功能，请允许我们访问麦克风权限，否则你将无法使用应用部分功能"))
        VMPermission.setEnableDialog(true)
            .setDialogTitle("权限申请")
            .setDialogContent("为了带跟你更好的使用体验，应用需要以下权限")
            .addPermissionList(list)
            .requestPermission(context, callback)
    }

    /**
     * 统一检查权限实现
     *
     * @param context 上下文对象
     * @param bean    权限实体
     * @return 是否授权
     */
    private fun checkPermission(context: Context, bean: VMPermissionBean): Boolean {
        return VMPermission.addPermission(bean).checkPermission(bean.permission)
    }
}