package com.vmloft.develop.library.gift

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.data.bean.Gift
import com.vmloft.develop.library.gift.databinding.ItemGiftDelegateBinding
import com.vmloft.develop.library.image.IMGLoader

/**
 * Create by lzan13 on 2021/05/22 17:56
 * 描述：展示最近会话内容 Item
 */
class ItemGiftDelegate(listener: BItemListener<Gift>, val showCount: Boolean = false) : BItemDelegate<Gift, ItemGiftDelegateBinding>(listener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ItemGiftDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ItemGiftDelegateBinding>, item: Gift) {

        IMGLoader.loadCover(holder.binding.coverIV, item.cover.path)

        holder.binding.nameTV.text = item.title
        if (showCount) {
            holder.binding.priceIV.visibility = View.GONE
            holder.binding.priceTV.text = "${item.count} 个"
        } else {
            holder.binding.priceIV.visibility = View.VISIBLE
            holder.binding.priceTV.text = item.price.toString()
        }

        holder.binding.root.isSelected = item.isSelected
    }

}
