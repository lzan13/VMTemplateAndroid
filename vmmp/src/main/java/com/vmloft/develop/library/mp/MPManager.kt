package com.vmloft.develop.library.mp

import android.content.Context

import com.vmloft.develop.library.tools.utils.logger.VMLog

import io.dcloud.feature.sdk.DCSDKInitConfig
import io.dcloud.feature.sdk.DCUniMPCapsuleButtonStyle
import io.dcloud.feature.sdk.DCUniMPSDK
import io.dcloud.feature.sdk.MenuActionSheetItem
import io.dcloud.feature.unimp.config.IUniMPReleaseCallBack
import io.dcloud.feature.unimp.config.UniMPReleaseConfiguration


/**
 * Create by lzan13 on 2022/5/12
 * 描述：小程序管理类
 */
object MPManager {

    /**
     * 初始化 mp
     */
    fun init(context: Context) {

        // 胶囊按钮样式
        val capsuleStyle = DCUniMPCapsuleButtonStyle()
        capsuleStyle.setBackgroundColor("#f5f6f8") // 胶囊按钮背景颜色
        capsuleStyle.setTextColor("#363636") // 胶囊按钮“···｜x” 的字体颜色
        capsuleStyle.setBorderColor("#f0f0f0") // 胶囊按钮边框颜色
        capsuleStyle.setHighlightColor("#f0f0f0") // 胶囊按钮按下状态背景颜色

        // 胶囊按钮点击弹出菜单
        val item = MenuActionSheetItem("关于", "gy")
        val sheetItems: MutableList<MenuActionSheetItem> = ArrayList()
        sheetItems.add(item)

        // MPSDK 初始化配置
        val config: DCSDKInitConfig = DCSDKInitConfig.Builder()
            .setCapsule(true) // 是否显示胶囊按钮
            .setEnableBackground(true) // 设置是否支持后天运行
            .setUniMPFromRecents(false) // 设置是否显示多任务窗口
            .setCapsuleButtonStyle(capsuleStyle) // 设置胶囊样式
            .setMenuDefFontSize("16px")
            .setMenuDefFontColor("#ff00ff")
            .setMenuDefFontWeight("normal")
            .setMenuActionSheetItems(sheetItems) // 设置菜单按钮
            .build()

        setupMPMenuListener()

        DCUniMPSDK.getInstance().initialize(context, config)
    }

    /**
     * 启动小程序
     */
    fun openMP(context: Context, appId: String) {
        DCUniMPSDK.getInstance().openUniMP(context, appId)
    }

    /**
     * 释放小程序，释放成功后会直接打开
     */
    fun unpackMP(context: Context, appId: String, path: String) {
        val config = UniMPReleaseConfiguration()
        config.wgtPath = path

        DCUniMPSDK.getInstance().releaseWgtToRunPath(appId, config) { code, args ->
            if (code == 1) {
                VMLog.e("小程序包解压成功 $code $args")
                openMP(context, appId)
            } else {
                // 解压失败，可能是文件不完整，请删除重新下载
                VMLog.e("小程序包解压失败 $code $args")
            }
        }
    }

    /**
     * 加载小程序菜单监听
     */
    private fun setupMPMenuListener() {
        DCUniMPSDK.getInstance().setDefMenuButtonClickCallBack { appid, id ->
            when (id) {
                "gy" -> {
                    VMLog.i("用户点击了关于 appId-${appid}")
                }
            }
        }
    }

}