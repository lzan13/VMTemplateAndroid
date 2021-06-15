package com.vmloft.develop.app.template.ui.post

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ItemPostDetailsCommentDelegateBinding
import com.vmloft.develop.app.template.request.bean.Comment
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BItemDelegate

/**
 * Create by lzan13 on 2021/05/05 17:56
 * 描述：内容详情评论 Item
 */
class ItemPostDetailsCommentDelegate : BItemDelegate<Comment, ItemPostDetailsCommentDelegateBinding>() {

    override fun layoutId(): Int = R.layout.item_post_details_comment_delegate

    override fun onBindView(holder: BItemHolder<ItemPostDetailsCommentDelegateBinding>, item: Comment) {
        holder.binding.data = item

        holder.binding.postAvatarIV.setOnClickListener { AppRouter.goUserInfo(item.owner) }

        holder.binding.executePendingBindings()
    }

}
