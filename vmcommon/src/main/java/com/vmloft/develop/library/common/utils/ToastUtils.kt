package com.vmloft.develop.library.common.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.widget.tips.VMTips

/**
 * Create by lzan13 on 2020/4/12 11:18
 * 描述：统一处理 Toast 提醒
 */

/**
 * Activity 调用
 */
fun Activity.showBar(resId: Int, duration: Long = VMTips.durationShort) {
    VMTips.showBar(this, VMStr.byRes(resId), duration)
}

fun Activity.showBar(content: String, duration: Long = VMTips.durationShort) {
    VMTips.showBar(this, content, duration)
}

fun Activity.darkBar(content: String, duration: Long = VMTips.durationShort) {
    VMTips.darkBar(this, content, duration)
}

fun Activity.errorBar(resId: Int, duration: Long = VMTips.durationShort) {
    VMTips.errorBar(this, VMStr.byRes(resId), duration)
}

fun Activity.errorBar(content: String, duration: Long = VMTips.durationShort) {
    VMTips.errorBar(this, content, duration)
}


/**
 * Fragment 调用
 */
fun Fragment.showBar(resId: Int, duration: Long = VMTips.durationShort) {
    VMTips.showBar(activity!!, VMStr.byRes(resId), duration)
}

fun Fragment.showBar(content: String, duration: Long = VMTips.durationShort) {
    VMTips.showBar(activity!!, content, duration)
}

fun Fragment.errorBar(resId: Int, duration: Long = VMTips.durationShort) {
    VMTips.errorBar(activity!!, VMStr.byRes(resId), duration)
}

fun Fragment.errorBar(content: String, duration: Long = VMTips.durationShort) {
    VMTips.errorBar(activity!!, content, duration)
}

/**
 * 任意调用，这里是调用系统的 toast 提醒
 */
fun Context.show(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    VMTips.show(this, resId, duration)
}

fun Context.show(content: String, duration: Int = Toast.LENGTH_SHORT) {
    VMTips.show(this, content, duration)
}
