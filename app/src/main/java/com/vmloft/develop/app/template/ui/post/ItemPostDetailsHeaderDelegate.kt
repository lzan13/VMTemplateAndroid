package com.vmloft.develop.app.template.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ItemPostDetailsHeaderDelegateBinding
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.tools.utils.VMColor
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2021/05/05 17:56
 * 描述：内容详情头部 Item
 */
class ItemPostDetailsHeaderDelegate(listener: PostItemListener) : BItemDelegate<Post, ItemPostDetailsHeaderDelegateBinding>(listener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ItemPostDetailsHeaderDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ItemPostDetailsHeaderDelegateBinding>, item: Post) {
        holder.binding.coverIV.visibility = if (item.attachments.size > 0) View.VISIBLE else View.GONE
        if (item.attachments.size > 0) {
            IMGLoader.loadCover(holder.binding.coverIV, item.attachments[0].path, thumbExt = "!vt512")
            holder.binding.coverIV.setOnClickListener { CRouter.goDisplaySingle(item.attachments[0].path) }
        }

        IMGLoader.loadAvatar(holder.binding.avatarIV, item.owner.avatar)
        holder.binding.avatarIV.setOnClickListener { CRouter.go(AppRouter.appUserInfo, obj0 = item.owner) }

        // 性别
        when (item.owner.gender) {
            1 -> holder.binding.genderIV.setImageResource(R.drawable.ic_gender_man)
            0 -> holder.binding.genderIV.setImageResource(R.drawable.ic_gender_woman)
            else -> holder.binding.genderIV.setImageResource(R.drawable.ic_gender_unknown)
        }
        holder.binding.nameTV.text = item.owner.nickname
        // 身份
        if (ConfigManager.clientConfig.vipEntry && item.owner.role.identity in 100..199) {
            holder.binding.nameTV.setTextColor(VMColor.byRes(R.color.app_identity_vip))
            holder.binding.identityIV.visibility = View.VISIBLE
        } else {
            holder.binding.identityIV.visibility = View.GONE
        }

        holder.binding.categoryTV.text = item.category.title
        holder.binding.timeTV.text = FormatUtils.relativeTime(item.createdAt)

        holder.binding.likeIV.setImageResource(if (item.isLike) R.drawable.ic_like_fill else R.drawable.ic_like_line)
        holder.binding.likeIV.setOnClickListener { (mItemListener as PostItemListener).onLikeClick(item, getPosition(holder)) }
        holder.binding.likeTV.text = item.likeCount.toString()

        holder.binding.contentTV.text = item.content
        holder.binding.commentTitleTV.text = VMStr.byResArgs(R.string.comment_count, item.commentCount)

        holder.binding.reportTV.setOnClickListener { (mItemListener as PostItemListener).onReportClick(item, getPosition(holder)) }


    }

    /**
     * 回调给 View 层监听接口
     */
    interface PostItemListener : BItemListener<Post> {
        fun onLikeClick(item: Post, position: Int)
        fun onReportClick(item: Post, position: Int)
    }
}
