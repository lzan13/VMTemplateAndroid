package com.vmloft.develop.app.template.ui.main.mine.relation

import android.view.LayoutInflater
import android.view.ViewGroup

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ItemRelationDelegateBinding
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2022/04/07 23:56
 * 描述：展示关系 Item
 */
class ItemRelationDelegate(listener: RelationItemListener, val isBlacklist: Boolean = false) : BItemDelegate<User, ItemRelationDelegateBinding>(listener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ItemRelationDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ItemRelationDelegateBinding>, item: User) {
        IMGLoader.loadAvatar(holder.binding.avatarIV, item.avatar)

        val nickname = if (item.nickname.isNullOrEmpty()) VMStr.byRes(R.string.info_nickname_default) else item.nickname
        holder.binding.nameTV.text = nickname

        if (isBlacklist) {
            holder.binding.relationTV.text = when (item.relation) {
                0 -> VMStr.byRes(R.string.blacklist_remove)
                1 -> VMStr.byRes(R.string.blacklist_add)
                2 -> VMStr.byRes(R.string.blacklist_remove)
                else -> VMStr.byRes(R.string.blacklist_add)
            }
        } else {
            holder.binding.relationTV.text = when (item.relation) {
                0 -> VMStr.byRes(R.string.follow_status_0)
                1 -> VMStr.byRes(R.string.follow_status_1)
                2 -> VMStr.byRes(R.string.follow_status_2)
                else -> VMStr.byRes(R.string.follow)
            }
        }

        holder.binding.relationTV.setOnClickListener { (mItemListener as RelationItemListener).onRelationClick(item, getPosition(holder)) }
    }

    /**
     * 回调给 View 层监听接口
     */
    interface RelationItemListener : BItemListener<User> {
        fun onRelationClick(item: User, position: Int)
    }
}
