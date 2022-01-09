package com.vmloft.develop.library.common.ui.widget.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater

import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.simple.SimpleComponent

import com.vmloft.develop.library.common.databinding.WidgetCommonFooterDoubleCircleBinding

/**
 * Create by lzan13 on 2021/9/17
 * 描述：自定义上拉加载更多样式
 */
class DoubleCircleFooter @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : SimpleComponent(context, attrs, defStyleAttr), RefreshFooter {
    val mBinding = WidgetCommonFooterDoubleCircleBinding.inflate(LayoutInflater.from(context), this, true)

    override fun setNoMoreData(noMore: Boolean): Boolean {
        mBinding.commonLoadingView.visibility = if (noMore) GONE else VISIBLE
        mBinding.commonNoMoreTV.visibility = if (noMore) VISIBLE else GONE
        return true
    }
}