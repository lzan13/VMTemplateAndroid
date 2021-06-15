package com.vmloft.develop.app.template.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

import com.alibaba.android.arouter.launcher.ARouter

import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.vmloft.develop.app.template.BuildConfig

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SPManager
import com.vmloft.develop.app.template.im.IMManager
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.app.template.report.ReportManager
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.common.CSPManager
import com.vmloft.develop.library.common.notify.NotifyManager
import com.vmloft.develop.library.tools.VMTools
import com.vmloft.develop.library.tools.utils.VMFile
import com.vmloft.develop.library.tools.utils.VMTheme
import com.vmloft.develop.library.tools.utils.logger.VMLog

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

import kotlin.properties.Delegates


/**
 * Create by lzan13 on 2020-02-13 18:02
 * 描述：程序入口
 */
class App : Application() {

    companion object {
        var appContext: Context by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        initApp()
    }

    private fun initApp() {
        initCommon()

        initKoin()

        initRouter()

        // 初始化通知管理
        initNotify()

        // 初始化 IM
        initIM()

        // 初始化上报，这里包含 bugly 错误日志上报以及 UMeng 统计上报
        initReport()

        initRefresh()

        // 初始化短信
//        SMSManager.instance.init()

        // 初始化广告管理
        // ADSManager.instance.init(appContext)

        // 初始化事件总线
        LDEventBus.init()
    }


    /**
     * 初始化通用工具
     */
    private fun initCommon() {
        VMTools.init(appContext)
        val level = if (CSPManager.instance.isDebug()) VMLog.Level.DEBUG else VMLog.Level.ERROR
        VMLog.init(level, "VMMatchAndroid")

        // 设置暗色主题模式
        if (SPManager.instance.isDarkModeSystemSwitch()) {
            VMTheme.setDarkTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else {
            VMTheme.setDarkTheme(SPManager.instance.getDarkModeManual())
        }

        val path = "${VMFile.pictures}${CConstants.projectDir}"
        if (!VMFile.isDirExists(path)) {
            VMFile.createDirectory(path)
        }
    }


    /**
     * 初始化 Koin
     */
    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }

    /**
     * 初始化注解路由
     */
    private fun initRouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }

    /**
     * 初始化通知
     */
    private fun initNotify() {
        NotifyManager.instance.init(appContext)
    }

    /**
     * 初始化 IM
     */
    private fun initIM() {
        // 初始化 IM
        IMManager.instance.init(appContext);
    }

    /**
     * 初始化上报
     */
    private fun initReport() {
        ReportManager.instance.init()
    }

    /**
     * 初始化下拉刷新控件
     */
    private fun initRefresh() {
        // 设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer { _, layout ->
            layout.setPrimaryColorsId(R.color.app_primary, R.color.app_accent) // 全局设置主题颜色

            layout.setDragRate(0.6f) // 显示拖动高度/真实拖动高度（默认0.5，阻尼效果）

            layout.setReboundDuration(300) // 设置回弹时间

            layout.setHeaderHeight(96.0f) // Header标准高度（显示下拉高度>=标准高度 触发刷新）
            layout.setFooterHeight(56.0f) // Footer标准高度（显示上拉高度>=标准高度 触发加载）

            layout.setHeaderMaxDragRate(2.0f) // 最大显示下拉高度/Header标准高度
            layout.setHeaderTriggerRate(1.0f) // 触发刷新距离 与 HeaderHeight 的比率1.0.4
            layout.setFooterMaxDragRate(2.0f) // 最大显示上拉高度/Footer标准高度
            layout.setFooterTriggerRate(1.0f) // 触发加载距离 与 FooterHeight 的比率1.0.4

            //            layout.setEnableNestedScroll(true) // 是否开启嵌套滚动NestedScrolling（默认false-智能开启）
            layout.setEnableOverScrollBounce(false) // 是否启用越界回弹
            layout.setEnablePureScrollMode(false) // 是否启用纯滚动模式

            layout.setEnableScrollContentWhenRefreshed(true) // 是否在刷新成功之后滚动内容显示新数据（默认-true）
            layout.setEnableScrollContentWhenLoaded(true) // 是否在加载完成之后滚动内容显示新数据（默认-true）

            layout.setEnableRefresh(true) // 是否启用下拉刷新功能
            layout.setEnableLoadMore(true) // 是否启用上拉加载功能
        }
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            // 指定为经典Header，默认是 贝塞尔雷达Header
            ClassicsHeader(context)
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            // 指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context).setDrawableSize(16f)
        }
    }
}