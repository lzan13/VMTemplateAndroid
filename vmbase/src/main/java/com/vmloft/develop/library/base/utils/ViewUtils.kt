package com.vmloft.develop.library.base.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Create by lzan13 on 2020/4/12 15:27
 * 描述：View 相关工具类
 */
object ViewUtils {

    /**
     * ------------------------------- 键盘部分 -------------------------------
     * 显示键盘
     */
    fun showKeyboard(activity: Activity?, view: View?) {
        if (activity == null) {
            return
        }
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 切换软键盘的显示与隐藏
        // imm.toggleSoftInputFromWindow(mInputET.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.HIDE_NOT_ALWAYS);
        // 显示软键盘
        imm.showSoftInput(view, InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }

    /**
     * 隐藏键盘
     */
    fun hideKeyboard(activity: Activity?, view: View) {
        if (activity == null) {
            return
        }
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 切换软键盘的显示与隐藏
        // imm.toggleSoftInputFromWindow(mInputET.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.HIDE_NOT_ALWAYS);
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

}