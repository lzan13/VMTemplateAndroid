package com.vmloft.develop.app.template.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ItemPostDetailsCommentDelegateBinding
import com.vmloft.develop.app.template.request.bean.Comment
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.FormatUtils
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2021/05/05 17:56
 * 描述：内容详情评论 Item
 */
class ItemPostDetailsCommentDelegate(listener: BItemListener<Comment>) : BItemDelegate<Comment, ItemPostDetailsCommentDelegateBinding>(listener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ItemPostDetailsCommentDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ItemPostDetailsCommentDelegateBinding>, item: Comment) {
        IMGLoader.loadAvatar(holder.binding.avatarIV, item.owner.avatar)
        // 性别
        when (item.owner.gender) {
            1 -> holder.binding.genderIV.setImageResource(R.drawable.ic_gender_man)
            0 -> holder.binding.genderIV.setImageResource(R.drawable.ic_gender_woman)
            else -> holder.binding.genderIV.setImageResource(R.drawable.ic_gender_unknown)
        }
        holder.binding.nameTV.text = item.owner.nickname
        holder.binding.timeTV.text = FormatUtils.relativeTime(item.createdAt)

        holder.binding.contentTV.text = item.content

    }

}
