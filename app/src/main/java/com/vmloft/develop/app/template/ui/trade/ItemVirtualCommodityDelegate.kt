package com.vmloft.develop.app.template.ui.trade

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup

import com.vmloft.develop.app.template.databinding.ItemVirtualCommodityDelegateBinding
import com.vmloft.develop.app.template.request.bean.Commodity
import com.vmloft.develop.library.base.BItemDelegate

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示虚拟商品内容 Item
 */
class ItemVirtualCommodityDelegate(listener: BItemListener<Commodity>) : BItemDelegate<Commodity, ItemVirtualCommodityDelegateBinding>(listener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ItemVirtualCommodityDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ItemVirtualCommodityDelegateBinding>, item: Commodity) {
        if (item.type == 0) {
            holder.binding.scoreTV.text = (item.price.toFloat().toInt() * 100).toString()
        }else{
            holder.binding.scoreTV.text = item.title
        }

        holder.binding.priceTV.text = "￥${item.price}"
        holder.binding.priceTV.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG

        holder.binding.currPriceTV.text = "￥${item.currPrice}"

        holder.binding.root.isSelected = item.isSelected
    }
}
