package com.vmloft.develop.library.common.ui.widget.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.scwang.smart.refresh.layout.api.RefreshHeader

import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.simple.SimpleComponent

import com.vmloft.develop.library.common.databinding.WidgetCommonHeaderDoubleCircleBinding

/**
 * Create by lzan13 on 2021/9/17
 * 描述：自定义顶部下拉刷新样式
 */
class DoubleCircleHeader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : SimpleComponent(context, attrs, defStyleAttr), RefreshHeader {
    val mBinding = WidgetCommonHeaderDoubleCircleBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * 手指拖动下拉（会连续多次调用，添加isDragging并取代之前的onPulling、onReleasing）
     * @param isDragging – true 手指正在拖动 false 回弹动画
     * @param percent – 下拉的百分比 值 = offset/footerHeight (0 - percent - (footerHeight+maxDragHeight) / footerHeight )
     * @param offset – 下拉的像素偏移量 0 - offset - (footerHeight+maxDragHeight)
     * @param height – 高度 HeaderHeight or FooterHeight (offset 可以超过 height 此时 percent 大于 1)
     * @param maxDragHeight – 最大拖动高度 offset 可以超过 height 参数 但是不会超过 maxDragHeight
     */
    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
//        super.onMoving(isDragging, percent, offset, height, maxDragHeight)
        mBinding.loadingView.alpha = percent
        mBinding.loadingView.scaleX = percent
        mBinding.loadingView.scaleY = percent

    }

    /**
     * 释放时刻（调用一次，将会触发加载）
     * @param refreshLayout – RefreshLayout
     * @param height – 高度 HeaderHeight or FooterHeight
     * @param maxDragHeight – 最大拖动高度
     */
    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
//        super.onReleased(refreshLayout, height, maxDragHeight)
    }

    /**
     * 开始动画
     * @param refreshLayout – RefreshLayout
     * @param height – HeaderHeight or FooterHeight
     * @param maxDragHeight – 最大拖动高度
     */
    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        super.onStartAnimator(refreshLayout, height, maxDragHeight)
        // 开始动画
//        mBinding.loadingView.startAnimation()
    }

    /**
     * 动画结束
     * @param refreshLayout RefreshLayout
     * @param success 数据是否成功刷新或加载
     * @param Returns 完成动画所需时间 如果返回 Integer.MAX_VALUE 将取消本次完成事件，继续保持原有状态
     */
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
//        mBinding.loadingView.stopAnimation()
        return super.onFinish(refreshLayout, success)
    }
}