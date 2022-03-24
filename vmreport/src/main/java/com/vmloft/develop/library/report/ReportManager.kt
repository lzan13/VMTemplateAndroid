package com.vmloft.develop.library.report

import android.content.Context
import com.tencent.bugly.crashreport.CrashReport

import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

import com.vmloft.develop.library.tools.VMTools

/**
 * Create by lzan13 on 2020/4/29 21:51
 * 描述：上报管理类
 */
object ReportManager {

    /**
     * 预初始化
     */
    fun preInit(context: Context) {

        CrashReport.setIsDevelopmentDevice(context, BuildConfig.DEBUG);
        CrashReport.initCrashReport(context, "", BuildConfig.DEBUG)

        UMConfigure.preInit(context, "", "")
        // 设置友盟统计日志开关 默认为 false
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        // 选用 AUTO 页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
        // 支持在子进程中统计自定义事件
        UMConfigure.setProcessEvent(true)
    }

    /**
     * 正式初始化
     */
    fun init(context: Context) {
        // 初始化
        UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, null)
    }

    /**
     * 上报页面开始，这里只需要在非 Activity 界面调用，因为 Activity 会自动统计
     */
    fun reportPageStart(page: String) {
        MobclickAgent.onPageStart(page)
    }

    /**
     * 上报页面结束，需要和上边的 reportPageStart 成对使用
     */
    fun reportPageEnd(page: String) {
        MobclickAgent.onPageEnd(page)
    }

    /**
     * 上报自定义事件
     * @param key 事件类型
     * @param params 事件参数
     */
    fun reportEvent(key: String, params: Map<String, Any>? = null) {
        if (params == null) {
            MobclickAgent.onEvent(VMTools.context, key)
        } else {
            MobclickAgent.onEventObject(VMTools.context, key, params)
        }
    }

    /**
     * 自定义异常上报
     */
    fun reportCrash(e: Throwable) {
        CrashReport.postCatchedException(e)
    }

    /**
     * 自定义日志上报
     */
    fun reportCrash(msg: String) {
        CrashReport.postCatchedException(Throwable(msg))
    }

}