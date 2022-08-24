package com.vmloft.develop.app.template.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ItemPostDetailsCommentDelegateBinding
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.data.bean.Comment
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.tools.utils.VMColor
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2021/05/05 17:56
 * 描述：内容详情评论 Item
 */
class ItemPostDetailsCommentDelegate(listener: CommentItemListener, longListener: BItemLongListener<Comment>) :
    BItemDelegate<Comment, ItemPostDetailsCommentDelegateBinding>(listener, longListener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ItemPostDetailsCommentDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ItemPostDetailsCommentDelegateBinding>, item: Comment) {
        IMGLoader.loadAvatar(holder.binding.avatarIV, item.owner.avatar)
        // 性别
        when (item.owner.gender) {
            1 -> holder.binding.genderIV.setImageResource(R.drawable.ic_gender_man)
            0 -> holder.binding.genderIV.setImageResource(R.drawable.ic_gender_woman)
            else -> holder.binding.genderIV.setImageResource(R.drawable.ic_gender_unknown)
        }

        val nickname = if (item.owner.nickname.isNullOrEmpty()) VMStr.byRes(R.string.info_nickname_default) else item.owner.nickname
        holder.binding.nameTV.text = nickname
        // 身份
        if (ConfigManager.appConfig.tradeConfig.vipEntry && item.owner.role.identity in 100..199) {
            holder.binding.nameTV.setTextColor(VMColor.byRes(R.color.app_identity_special))
            holder.binding.identityIV.visibility = View.VISIBLE
        } else {
            holder.binding.identityIV.visibility = View.GONE
        }

        holder.binding.timeTV.text = FormatUtils.relativeTime(item.createdAt)

        holder.binding.likeIV.setImageResource(if (item.isLike) R.drawable.ic_like_fill else R.drawable.ic_like_line)
        holder.binding.likeIV.setOnClickListener { (mItemListener as CommentItemListener).onLikeClick(item, getPosition(holder)) }
        holder.binding.likeTV.text = item.likeCount.toString()

        holder.binding.contentTV.text = item.content

    }

    /**
     * 回调给 View 层监听接口
     */
    interface CommentItemListener : BItemListener<Comment> {
        fun onLikeClick(item: Comment, position: Int)
    }
}
