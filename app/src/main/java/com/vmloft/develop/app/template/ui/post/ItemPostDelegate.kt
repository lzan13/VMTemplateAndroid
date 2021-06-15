package com.vmloft.develop.app.template.ui.post

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ItemPostDelegateBinding
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.image.IMGLoader

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示发布内容 Item
 */
class ItemPostDelegate(listener: PostItemListener) : BItemDelegate<Post, ItemPostDelegateBinding>(listener) {

    override fun layoutId(): Int = R.layout.item_post_delegate

    override fun onBindView(holder: BItemHolder<ItemPostDelegateBinding>, item: Post) {
        holder.binding.data = item

        if (item.attachments.size > 0) {
            IMGLoader.loadCover(holder.binding.itemCoverIV, item.attachments[0].path, isRadius = true, radiusTL = 8, radiusTR = 8)
        }

        holder.binding.itemLikeIV.setOnClickListener { (mItemListener as PostItemListener).onLikeClick(item, getPosition(holder)) }

        holder.binding.executePendingBindings()
    }

    /**
     * 回调给 View 层监听接口
     */
    interface PostItemListener : BItemListener<Post> {
        fun onLikeClick(item: Post, position: Int)
    }
}
