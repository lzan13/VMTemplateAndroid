package com.vmloft.develop.library.common.ui.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Create by lzan13 2020/12/11
 * 自定义 RecyclerView 分隔线
 */
class StaggeredItemDecoration(val space: Int, val column: Int = 2, val startCount: Int = 0) : RecyclerView.ItemDecoration() {

    /**
     * 自定义实现边距
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (startCount == 0) {
            setSpace(view, outRect)
        } else {
            val position = parent.getChildLayoutPosition(view)
            if (position >= startCount) {
                setSpace(view, outRect)
            }
        }
    }

    private fun setSpace(view: View, outRect: Rect) {
        // 瀑布流专属分割线
        val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        outRect.top = space
        /**
         * 根据params.getSpanIndex()来判断左右边确定分割线
         * 第一列设置左边距为space，右边距为space/2  （第二列反之）
         */
        if (params.spanIndex % column == 0) {
            outRect.left = space * 2
            outRect.right = space / 2
        } else {
            outRect.left = space / 2
            outRect.right = space * 2
        }
    }
}