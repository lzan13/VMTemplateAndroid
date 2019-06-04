package com.vmloft.develop.library.im.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.vmloft.develop.library.im.common.IMSPManager;
import com.vmloft.develop.library.tools.utils.VMDimen;

/**
 * Create by lzan13 on 2019/6/4 16:11
 *
 * 监听键盘变化控件
 */
public class IMKeyboardLayout extends RelativeLayout {

    // 键盘监听
    private KeyboardListener mListener;
    // 输入法是否激活
    private boolean isActive = false;
    // 输入法高度
    private int mHeight = 0;

    public IMKeyboardLayout(Context context) {
        this(context, null, 0);
    }

    public IMKeyboardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IMKeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 通过视图树监听布局变化
        getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardChangeListener());
    }

    // 布局变化监听器
    private class KeyboardChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

        int mScreenHeight = 0;
        Rect mRect = new Rect();

        private int getScreenHeight() {
            if (mScreenHeight > 0) {
                return mScreenHeight;
            }
            mScreenHeight = VMDimen.getScreenSize().y;
            return mScreenHeight;
        }

        @Override
        public void onGlobalLayout() {
            // 获取当前页面窗口的显示范围
            getWindowVisibleDisplayFrame(mRect);

            // 屏幕高度
            int screenHeight = getScreenHeight();
            // 计算输入法的高度
            int height = screenHeight - mRect.bottom;
            boolean active = false;
            // 超过屏幕五分之一则表示弹出了输入法
            if (Math.abs(height) > screenHeight / 5) {
                active = true;
                mHeight = height;
                IMSPManager.getInstance().putKeyboardHeight(mHeight);
            }
            isActive = active;
            if (mListener != null) {
                mListener.onKeyboardActive(isActive, mHeight);
            }
        }
    }

    /**
     * 判断键盘是否激活
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * 获取输入法高度
     */
    public int getKeyboardHeight() {
        return mHeight;
    }

    /**
     * ------------------------------- 键盘部分 -------------------------------
     * 显示键盘
     */
    public void showKeyboard(Activity activity, View view) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 切换软键盘的显示与隐藏
        // imm.toggleSoftInputFromWindow(mInputET.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager
        // .HIDE_NOT_ALWAYS);
        // 显示软键盘
        imm.showSoftInput(view, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboard(Activity activity, View view) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 切换软键盘的显示与隐藏
        // imm.toggleSoftInputFromWindow(mInputET.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager
        // .HIDE_NOT_ALWAYS);
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 设置是否重新调整布局大小
     */
    public void setResizeLayout(Fragment fragment, boolean resize) {
        setResizeLayout(fragment.getActivity(), resize);
    }

    /**
     * 设置是否重新调整布局大小
     */
    public void setResizeLayout(Activity activity, boolean resize) {
        if (activity == null) {
            return;
        }
        if (resize) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
    }

    /**
     * 设置键盘监听
     */
    public void setKeyboardListener(KeyboardListener listener) {
        mListener = listener;
    }

    public interface KeyboardListener {
        /**
         * @param active 输入法是否激活
         * @param height 输入法面板高度
         */
        void onKeyboardActive(boolean active, int height);
    }
}
