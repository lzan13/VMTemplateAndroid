package com.vmloft.develop.library.im.emoji;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Create by lzan13 on 2019/9/20 18:50
 *
 * 自定义正方形布局
 */
public class VMSquareLayout extends RelativeLayout {

    private boolean interceptEvent;

    public VMSquareLayout(Context context) {
        super(context);
    }

    public VMSquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VMSquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec( childWidthSize, MeasureSpec.EXACTLY);
        // 高度和宽度一样
        heightMeasureSpec = widthMeasureSpec;
        //设定高是宽的比例
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /**
     * 拦截事件向下层传递
     * @param intercept
     */
    public void setInterceptEvent(boolean intercept){
        interceptEvent = intercept;
    }


    /**
     * 拦截事件向下层传递
     * * @param ev
     * @return
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(interceptEvent){
            return false;
        }

        return super.dispatchTouchEvent(ev);
    }

}
