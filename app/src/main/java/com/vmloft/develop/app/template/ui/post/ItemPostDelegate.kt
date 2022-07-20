package com.vmloft.develop.app.template.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ItemPostDelegateBinding
import com.vmloft.develop.library.data.bean.Attachment
import com.vmloft.develop.library.data.bean.Post
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.tools.utils.VMColor
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.widget.VMRatioLayout

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示帖子内容 Item
 */
class ItemPostDelegate(listener: PostItemListener, longListener: BItemLongListener<Post>? = null) : BItemDelegate<Post, ItemPostDelegateBinding>(listener, longListener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ItemPostDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ItemPostDelegateBinding>, item: Post) {
        holder.binding.itemCoverIV.visibility = if (item.attachments.isNotEmpty()) View.VISIBLE else View.GONE
        if (item.attachments.isNotEmpty()) {
            item.attachments[0].path
            updateCoverRatio(holder.binding.itemCoverIV, item.attachments[0])
            IMGLoader.loadCover(holder.binding.itemCoverIV, item.attachments[0].path, isRadius = true, thumbExt = "!vt256")
        }

//        holder.binding.itemTimeTV.text = FormatUtils.relativeTime(item.createdAt)
        holder.binding.itemContentTV.text = item.content

        IMGLoader.loadAvatar(holder.binding.itemAvatarIV, item.owner.avatar)

        val nickname = if (item.owner.nickname.isNullOrEmpty()) VMStr.byRes(R.string.info_nickname_default) else item.owner.nickname
        holder.binding.itemNameTV.text = nickname

        if (ConfigManager.clientConfig.tradeConfig.vipEntry && item.owner.role.identity in 100..199) {
            holder.binding.itemNameTV.setTextColor(VMColor.byRes(R.color.app_identity_vip))
        }else{
            holder.binding.itemNameTV.setTextColor(VMColor.byRes(R.color.app_tips))
        }

        holder.binding.itemLikeIV.setImageResource(if (item.isLike) R.drawable.ic_like_fill else R.drawable.ic_like_line)
        holder.binding.itemLikeIV.setOnClickListener { (mItemListener as PostItemListener).onLikeClick(item, getPosition(holder)) }
        holder.binding.itemLikeTV.text = item.likeCount.toString()
    }

    /**
     * 根据图片宽高更新封面比例
     */
    private fun updateCoverRatio(coverView: View, attachment: Attachment) {
        var w = attachment.width
        var h = attachment.height
        if (w == 0 || h == 0) {
            return
        }
        if (h > w * 2) {
            h = w * 2
        } else if (w > h * 2) {
            w = h * 2
        }
        val realW = (VMDimen.screenWidth - VMDimen.dp2px(4) * 3) / 2
        coverView.layoutParams.width = realW
        coverView.layoutParams.height = realW * h / w
    }

    /**
     * 回调给 View 层监听接口
     */
    interface PostItemListener : BItemListener<Post> {
        fun onLikeClick(item: Post, position: Int)
    }
}
