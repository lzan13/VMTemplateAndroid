package com.vmloft.develop.library.im.call;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.widget.IMCallView;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMLog;

/**
 * Created by lzan13 on 2017/3/27.
 *
 * 音视频通话悬浮窗操作类
 */
public class IMCallFloatWindow {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    // 悬浮窗需要显示的布局
    private View mFloatView;
    private TextView mTimeView;

    private IMCallView mCallLView;
    private IMCallView mCallRView;

    private IMCallFloatWindow() {
        mWindowManager = (WindowManager) IM.getInstance().getIMContext().getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 内部类是吸纳单例模式
     */
    private static class InnerHolder {
        public static IMCallFloatWindow INSTANCE = new IMCallFloatWindow();
    }

    public static IMCallFloatWindow getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 开始展示悬浮窗
     */
    public void addFloatWindow() {
        if (mFloatView != null) {
            return;
        }
        mLayoutParams = new WindowManager.LayoutParams();
        // 位置为右侧顶部
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        // 设置宽高自适应
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // 设置悬浮窗透明
        mLayoutParams.format = PixelFormat.TRANSPARENT;

        // 设置窗口类型
        if (Build.VERSION.SDK_INT > 25) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        // 设置窗口标志类型，其中 FLAG_NOT_FOCUSABLE 是放置当前悬浮窗拦截点击事件，造成桌面控件不可操作
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;

        // 获取要现实的布局
        mFloatView = LayoutInflater.from(IM.getInstance().getIMContext()).inflate(R.layout.im_call_float_window, null);
        // 添加悬浮窗 View 到窗口
        mWindowManager.addView(mFloatView, mLayoutParams);
        if (IMCallManager.getInstance().getCallType() == IMCallManager.CallType.VIDEO) {
            setupCallView();
        } else {
            mFloatView.findViewById(R.id.im_call_video_rl).setVisibility(View.GONE);
            mFloatView.findViewById(R.id.im_call_voice_ll).setVisibility(View.VISIBLE);
            mTimeView = mFloatView.findViewById(R.id.text_call_time);
        }

        // 当点击悬浮窗时，返回到通话界面
        mFloatView.setOnClickListener((View v) -> {
            Intent intent = new Intent();
            if (IMCallManager.getInstance().getCallType() == IMCallManager.CallType.VIDEO) {
                intent.setClass(IM.getInstance().getIMContext(), IMCallVideoActivity.class);
            } else {
                intent.setClass(IM.getInstance().getIMContext(), IMCallVoiceActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            IM.getInstance().getIMContext().startActivity(intent);
        });
        initFloatTouchListener();
    }

    /**
     * 设置本地与远程画面显示控件
     */
    private void setupCallView() {
        mFloatView.findViewById(R.id.im_call_video_rl).setVisibility(View.VISIBLE);
        mFloatView.findViewById(R.id.im_call_voice_ll).setVisibility(View.GONE);

        RelativeLayout callContainer = mFloatView.findViewById(R.id.im_call_video_rl);

        // 将 SurfaceView设置给 SDK
        callContainer.removeAllViews();

        mCallLView = new IMCallView(IM.getInstance().getIMContext());
        mCallRView = new IMCallView(IM.getInstance().getIMContext());

        int lw = VMDimen.dp2px(24);
        int lh = VMDimen.dp2px(32);
        int ow = VMDimen.dp2px(96);
        int oh = VMDimen.dp2px(128);
        RelativeLayout.LayoutParams localParams = new RelativeLayout.LayoutParams(lw, lh);
        RelativeLayout.LayoutParams oppositeParams = new RelativeLayout.LayoutParams(ow, oh);
        // 设置本地图像靠右
        localParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        // 设置本地预览图像显示在最上层
        mCallLView.setZOrderOnTop(false);
        mCallLView.setZOrderMediaOverlay(true);
        // 将 view 添加到界面
        callContainer.addView(mCallLView, localParams);
        callContainer.addView(mCallRView, oppositeParams);

        IMCallManager.getInstance().setCallView(mCallLView, mCallRView);
    }

    /**
     * 设置触摸监听
     */
    private void initFloatTouchListener() {
        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            boolean result = false;

            float x = 0;
            float y = 0;
            float startX = 0;
            float startY = 0;
            float statusBar = VMDimen.getStatusBarHeight();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    result = false;
                    x = event.getX();
                    y = event.getY();
                    startX = event.getRawX();
                    startY = event.getRawY();
                    VMLog.d("start x: %f, y: %f", startX, startY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    VMLog.d("move x: %f, y: %f", event.getRawX(), event.getRawY());
                    // 当移动距离大于特定值时，表示是多动悬浮窗，则不触发后边的点击监听
                    if (Math.abs(event.getRawX() - startX) > 20 || Math.abs(event.getRawY() - startY) > 20) {
                        result = true;
                    }
                    // getRawX 获取触摸点相对于屏幕的坐标，getX 相对于当前悬浮窗坐标
                    // 根据当前触摸点 X 坐标计算悬浮窗 X 坐标，
                    mLayoutParams.x = (int) (event.getRawX() - x);
                    // 根据当前触摸点 Y 坐标计算悬浮窗 Y 坐标，减25为状态栏的高度
                    mLayoutParams.y = (int) (event.getRawY() - y - statusBar);
                    // 刷新悬浮窗
                    mWindowManager.updateViewLayout(mFloatView, mLayoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                }
                return result;
            }
        });
    }

    /**
     * 停止悬浮窗
     */
    public void removeFloatWindow() {
        if (mWindowManager != null && mFloatView != null) {
            mWindowManager.removeView(mFloatView);
            mFloatView = null;
            mTimeView = null;
        }
    }

    /**
     * 刷新通话时间显示
     */
    public void refreshCallTime() {
        if (mTimeView != null) {
            if (!mTimeView.isShown()) {
                mTimeView.setVisibility(View.VISIBLE);
            }
            mTimeView.setText(IMCallManager.getInstance().getCallTime());
        }
    }
}
