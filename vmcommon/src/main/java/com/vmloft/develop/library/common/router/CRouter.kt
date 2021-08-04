package com.vmloft.develop.library.common.router

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.alibaba.android.arouter.launcher.ARouter
import com.vmloft.develop.library.common.ui.display.DisplayMultiActivity
import com.vmloft.develop.library.common.ui.display.DisplaySingleActivity
import com.vmloft.develop.library.common.ui.web.WebActivity
import com.vmloft.develop.library.tools.VMTools

/**
 * Create by lzan13 on 2020-02-24 21:57
 * 描述：针对路由注解统一收口
 */
object CRouter {

    const val appMain = "/App/Main"

    const val commonDebug = "/Common/Debug"
    const val commonWeb = "/Common/Web"
    const val commonDisplayMulti = "/Common/DisplayMulti"
    const val commonDisplaySingle = "/Common/DisplaySingle"

    /**
     * 复用跳转
     */
    fun go(path: String) {
        ARouter.getInstance().build(path).navigation()
    }

    /**
     * 复用跳转，需要接收返回值
     */
    fun goResult(activity: Activity, path: String, requestCode: Int) {
        ARouter.getInstance().build(path).navigation(activity, requestCode)
    }

    /**
     * 主界面[MainActivity]
     * @param type 跳转类型 0-普通 1-清空登录信息
     */
    fun goMain(type: Int = 0) {
        ARouter.getInstance().build(appMain)
            .withInt("type", type)
            .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .navigation()
    }

    /**
     * 跳转 debug 页面
     */
    fun goDebug() {
        ARouter.getInstance().build(commonDebug).navigation()
    }

    /**
     * 打开 Web 页面[WebActivity]
     */
    fun goWeb(url: String, system: Boolean = false) {
        if (system) {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            VMTools.context.startActivity(intent)
        } else {
            ARouter.getInstance().build(commonWeb).withString("url", url).navigation()
        }
    }

    /**
     * 展示多图[DisplayMultiActivity]
     */
    fun goDisplayMulti(index: String, list: List<String>) {
        ARouter.getInstance().build(commonDisplayMulti)
            .withString("index", index)
            .withObject("pictureList", list)
            .navigation()
    }

    /**
     * 展示单图[DisplaySingleActivity]
     */
    fun goDisplaySingle(url: String) {
        ARouter.getInstance().build(commonDisplaySingle)
            .withString("url", url)
            .navigation()
    }

}