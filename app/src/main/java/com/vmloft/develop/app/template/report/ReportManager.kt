package com.vmloft.develop.app.template.report

import com.tencent.bugly.crashreport.CrashReport
import com.tencent.wcdb.BuildConfig
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.vmloft.develop.app.template.app.App


/**
 * Create by lzan13 on 2020/4/29 21:51
 * 描述：上报管理类
 */
class ReportManager {

    // 定义事件 Key，需和后端一致
    private val eventKeyXXX = "eventKeyXXX"

    /**
     * 伴生对象实现单例效果
     */
    companion object {
        val instance: ReportManager by lazy {
            ReportManager()
        }
    }

    fun init() {
        // 初始化 Bugly

        // 设置是否为开发设备
        CrashReport.setIsDevelopmentDevice(App.appContext, BuildConfig.DEBUG)
        CrashReport.initCrashReport(App.appContext)

        // 设置友盟统计日志开关 默认为 false
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        // 初始化
        UMConfigure.init(App.appContext, UMConfigure.DEVICE_TYPE_PHONE, null)
        // 关闭错误统计功能
        MobclickAgent.setCatchUncaughtExceptions(false)
        // 选用 AUTO 页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        // 支持在子进程中统计自定义事件
        UMConfigure.setProcessEvent(true)
        // 输出 devicesId mac 等信息，添加测试设备
//        VMLog.d(
//            "{'device_id':'${DeviceConfig.getDevicesIdForGeneral(appContext)}', 'mac':'${DeviceConfig.getMac(
//                appContext
//            )}'}"
//        )
    }

    /**
     * 上报自定义事件
     * @param key 事件类型
     * @param params 事件参数
     */
    fun reportEvent(key: String, params: Map<String, Any>) {
        MobclickAgent.onEventObject(App.appContext, key, params)
    }

}