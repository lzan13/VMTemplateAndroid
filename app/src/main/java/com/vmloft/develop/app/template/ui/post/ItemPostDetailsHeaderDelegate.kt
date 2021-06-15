package com.vmloft.develop.app.template.ui.post

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ItemPostDetailsHeaderDelegateBinding
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.router.CRouter

/**
 * Create by lzan13 on 2021/05/05 17:56
 * 描述：内容详情头部 Item
 */
class ItemPostDetailsHeaderDelegate(listener: PostItemListener) : BItemDelegate<Post, ItemPostDetailsHeaderDelegateBinding>(listener) {

    override fun layoutId(): Int = R.layout.item_post_details_header_delegate

    override fun onBindView(holder: BItemHolder<ItemPostDetailsHeaderDelegateBinding>, item: Post) {
        holder.binding.data = item

        if (item.attachments.size > 0) {
            IMGLoader.loadCover(holder.binding.postCoverIV, item.attachments[0].path)
            holder.binding.postCoverIV.setOnClickListener { CRouter.goDisplaySingle(item.attachments[0].path) }
        }
        holder.binding.postAvatarIV.setOnClickListener { AppRouter.goUserInfo(item.owner) }
        holder.binding.postLikeIV.setOnClickListener { (mItemListener as PostItemListener).onLikeClick(item, getPosition(holder)) }

        holder.binding.executePendingBindings()
    }

    /**
     * 回调给 View 层监听接口
     */
    interface PostItemListener : BItemListener<Post> {
        fun onLikeClick(item: Post, position: Int)
    }
}
