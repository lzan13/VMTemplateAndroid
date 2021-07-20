package com.vmloft.develop.app.template.ui.post

import android.net.Uri
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ItemPostDelegateBinding
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.tools.widget.VMRatioLayout

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示发布内容 Item
 */
class ItemPostDelegate(listener: PostItemListener) : BItemDelegate<Post, ItemPostDelegateBinding>(listener) {

    override fun layoutId(): Int = R.layout.item_post_delegate

    override fun onBindView(holder: BItemHolder<ItemPostDelegateBinding>, item: Post) {
        holder.binding.data = item

        if (item.attachments.size > 0) {
            item.attachments[0].path
            updateCoverRatio(holder.binding.itemRatioLayout, item.attachments[0].path)
            IMGLoader.loadCover(holder.binding.itemCoverIV, item.attachments[0].path, isRadius = true, radiusTL = 8, radiusTR = 8)
        }

        holder.binding.itemLikeIV.setOnClickListener { (mItemListener as PostItemListener).onLikeClick(item, getPosition(holder)) }

        holder.binding.executePendingBindings()
    }

    /**
     * 根据图片宽高更新封面比例
     */
    private fun updateCoverRatio(coverLayout: VMRatioLayout, url: String) {
        //将String类型的地址转变为URI类型
        val uri = Uri.parse(url)
        var w = uri.getQueryParameter("w")?.toInt() ?: 0
        var h = uri.getQueryParameter("h")?.toInt() ?: 0
        if (w == 0 || h == 0) {
            return
        }
        if (h > w * 2) {
            h = w * 2
        } else if (w > h * 2) {
            w = h * 2
        }
        coverLayout.setRatio(h / w.toFloat())
    }

    /**
     * 回调给 View 层监听接口
     */
    interface PostItemListener : BItemListener<Post> {
        fun onLikeClick(item: Post, position: Int)
    }
}
