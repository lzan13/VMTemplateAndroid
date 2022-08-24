package com.vmloft.develop.library.base.router

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import com.alibaba.android.arouter.launcher.ARouter
import com.vmloft.develop.library.tools.VMTools
import com.vmloft.develop.library.tools.utils.logger.VMLog

/**
 * Create by lzan13 on 2020-02-24 21:57
 * 描述：针对路由注解统一收口
 */
object CRouter {
    // 通用传参 Key
    const val paramsWhat = "paramsWhat"
    const val paramsArg0 = "paramsArg0"
    const val paramsArg1 = "paramsArg1"
    const val paramsStr0 = "paramsStr0"
    const val paramsStr1 = "paramsStr1"
    const val paramsObj0 = "paramsObj0"
    const val paramsObj1 = "paramsObj1"
    const val paramsList = "paramsList"

    const val appMain = "/App/Main"

    // 定义页面路由，这里有有一点需要注意
    const val commonDebug = "/Common/Debug"
    const val commonWeb = "/Common/Web"

    const val imageDisplayMulti = "/Image/DisplayMulti"
    const val imageDisplaySingle = "/Image/DisplaySingle"

    const val qrScanQRCode = "/qr/ScanQRCode"
    const val qrScanQRCodeResCode = 1001

    /**
     * 可复用通用跳转方法
     */
    fun go(path: String) {
        ARouter.getInstance().build(path).navigation()
    }

    /**
     * 可复用通用跳转方法，可携带参数
     */
    fun go(
        path: String,
        what: Int = -1,
        arg0: Int = -1,
        arg1: Int = -1,
        str0: String? = null,
        str1: String? = null,
        obj0: Parcelable? = null,
        obj1: Parcelable? = null,
        list: ArrayList<Any>? = null,
        flags: Int = 0,
    ) {
        val postcard = ARouter.getInstance().build(path)

        if (what != -1) postcard.withInt(paramsWhat, what)
        if (arg0 != -1) postcard.withInt(paramsArg0, arg0)
        if (arg1 != -1) postcard.withInt(paramsArg1, arg1)

        str0?.let { postcard.withString(paramsStr0, str0) }
        str1?.let { postcard.withString(paramsStr1, str1) }
        obj0?.let { postcard.withParcelable(paramsObj0, obj0) }
        obj1?.let { postcard.withParcelable(paramsObj1, obj1) }

        list?.let {
            when {
                list.any { it is Integer } -> postcard.withIntegerArrayList(paramsList, list as ArrayList<Int>)
                list.any { it is String } -> postcard.withStringArrayList(paramsList, list as ArrayList<String>)
                list.any { it is Parcelable } -> postcard.withParcelableArrayList(paramsList, list as ArrayList<Parcelable>)
                else -> VMLog.d("暂不支持的类型")
            }
        }
        if (flags != 0) postcard.withFlags(flags)

        postcard.navigation()
    }

    /**
     * 复用跳转，需要接收返回值
     */
    fun goResult(activity: Activity, path: String, requestCode: Int) {
        ARouter.getInstance().build(path).navigation(activity, requestCode)
    }

    /**
     * 回到手机桌面
     */
    fun goHome() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        VMTools.context.startActivity(intent)
    }

    /**
     * 主界面
     * @param type 跳转类型 0-普通 1-清空登录信息
     */
    fun goMain(type: Int = 0) {
        ARouter.getInstance().build(appMain)
            .withInt("type", type)
            .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .navigation()
    }

    /**
     * 打开 Web 页面
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
     * 展示多图
     */
    fun goDisplayMulti(index: String, list: List<String>) {
        ARouter.getInstance().build(imageDisplayMulti)
            .withString("index", index)
            .withObject("pictureList", list)
            .navigation()
    }

    /**
     * 展示单图
     */
    fun goDisplaySingle(url: String) {
        ARouter.getInstance().build(imageDisplaySingle)
            .withString("url", url)
            .navigation()
    }

}