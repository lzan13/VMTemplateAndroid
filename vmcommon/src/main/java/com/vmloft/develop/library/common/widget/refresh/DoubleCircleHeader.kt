package com.vmloft.develop.library.common.widget.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.scwang.smart.refresh.layout.api.RefreshHeader

import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
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
        if (percent < 1.2) {
            mBinding.loadingView.alpha = percent
            mBinding.loadingView.scaleX = percent
            mBinding.loadingView.scaleY = percent
        }
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

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        if (newState == RefreshState.ReleaseToTwoLevel) {
            mBinding.loadingTipsTV.visibility = View.VISIBLE
            mBinding.loadingView.visibility = View.GONE
        } else if (newState == RefreshState.TwoLevel) {
            mBinding.loadingTipsTV.visibility = View.GONE
            mBinding.loadingView.visibility = View.GONE
        } else {
            mBinding.loadingTipsTV.visibility = View.GONE
            mBinding.loadingView.visibility = View.VISIBLE
        }
//        val arrowView: View = mArrowView
//        val updateView: View = mLastUpdateText
//        when (newState) {
//            RefreshState.None -> {
////                updateView.visibility = if (mEnableLastTime) VISIBLE else GONE
////                mTitleText.setText(mTextPulling)
////                arrowView.visibility = VISIBLE
////                arrowView.animate().rotation(0f)
//            }
//            RefreshState.PullDownToRefresh -> {
////                mTitleText.setText(mTextPulling)
////                arrowView.visibility = VISIBLE
////                arrowView.animate().rotation(0f)
//            }
//            RefreshState.Refreshing, RefreshState.RefreshReleased -> {
////                mTitleText.setText(mTextRefreshing)
////                arrowView.visibility = GONE
//            }
//            RefreshState.ReleaseToRefresh -> {
////                mTitleText.setText(mTextRelease)
////                arrowView.animate().rotation(180f)
//            }
//            RefreshState.ReleaseToTwoLevel -> {
////                mTitleText.setText(mTextSecondary)
////                arrowView.animate().rotation(0f)
//                mBinding.loadingTipsTV.visibility = View.VISIBLE
//            }
//            RefreshState.Loading -> {
////                arrowView.visibility = GONE
////                updateView.visibility = if (mEnableLastTime) INVISIBLE else GONE
////                mTitleText.setText(mTextLoading)
//            }
//            else -> mBinding.loadingTipsTV.visibility = View.GONE
//        }
    }
}