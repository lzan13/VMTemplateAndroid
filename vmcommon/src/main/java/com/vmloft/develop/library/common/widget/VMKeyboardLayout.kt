package com.vmloft.develop.library.common.widget

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.vmloft.develop.library.common.common.CSPManager

import com.vmloft.develop.library.tools.utils.VMDimen

/**
 * Create by lzan13 on 2019/6/4 16:11
 *
 * 监听键盘变化控件
 */
class VMKeyboardLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    // 键盘监听
    private var mListener: KeyboardListener? = null

    /**
     * 判断键盘是否激活
     */
    // 输入法是否激活
    private var isActive = false

    /**
     * 获取输入法高度
     */
    // 输入法高度
    private var keyboardHeight = 0

    init {
        // 通过视图树监听布局变化
        viewTreeObserver.addOnGlobalLayoutListener(KeyboardChangeListener())
    }

    fun isActive(): Boolean {
        return isActive
    }

    // 布局变化监听器
    private inner class KeyboardChangeListener : OnGlobalLayoutListener {
        var mScreenHeight = 0
        var mRect = Rect()
        private val screenHeight: Int
            get() {
                if (mScreenHeight > 0) {
                    return mScreenHeight
                }
                mScreenHeight = VMDimen.screenHeight
                return mScreenHeight
            }

        override fun onGlobalLayout() { // 获取当前页面窗口的显示范围
            getWindowVisibleDisplayFrame(mRect)
            // 屏幕高度
            val screenHeight = screenHeight
            // 计算输入法的高度
            val height = screenHeight - mRect.bottom
            var active = false
            // 超过屏幕五分之一则表示弹出了输入法
            if (Math.abs(height) > screenHeight / 5) {
                active = true
                keyboardHeight = height
                CSPManager.putKeyboardHeight(keyboardHeight)
            }
            isActive = active
            if (mListener != null) {
                mListener!!.onKeyboardActive(isActive, keyboardHeight)
            }
        }
    }

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

    /**
     * 设置是否重新调整布局大小
     */
    fun setResizeLayout(fragment: Fragment, resize: Boolean) {
        setResizeLayout(fragment.activity, resize)
    }

    /**
     * 设置是否重新调整布局大小
     */
    fun setResizeLayout(activity: Activity?, resize: Boolean) {
        if (activity == null) {
            return
        }
        if (resize) {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        } else {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        }
    }

    /**
     * 设置键盘监听
     */
    fun setKeyboardListener(listener: KeyboardListener?) {
        mListener = listener
    }

    interface KeyboardListener {
        /**
         * @param active 输入法是否激活
         * @param height 输入法面板高度
         */
        fun onKeyboardActive(active: Boolean, height: Int)
    }

}