package com.vmloft.develop.app.template.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ItemPostDetailsCommentDelegateBinding
import com.vmloft.develop.app.template.request.bean.Comment
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.tools.utils.VMColor

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
        // 身份
        if (ConfigManager.clientConfig.vipEntry && item.owner.role.identity in 100..199) {
            holder.binding.nameTV.setTextColor(VMColor.byRes(R.color.app_identity_vip))
            holder.binding.identityIV.visibility = View.VISIBLE
        } else {
            holder.binding.identityIV.visibility = View.GONE
        }


        holder.binding.timeTV.text = FormatUtils.relativeTime(item.createdAt)

        holder.binding.contentTV.text = item.content

    }

}
