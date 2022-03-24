package com.vmloft.develop.app.template.ui.trade

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vmloft.develop.app.template.R

import com.vmloft.develop.app.template.databinding.ItemOrderDelegateBinding
import com.vmloft.develop.app.template.request.bean.Order
import com.vmloft.develop.library.base.BItemDelegate

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示订单内容 Item
 */
class ItemOrderDelegate(listener: BItemListener<Order>) : BItemDelegate<Order, ItemOrderDelegateBinding>(listener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ItemOrderDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ItemOrderDelegateBinding>, item: Order) {
        holder.binding.orderTitleTV.text = item.title
        holder.binding.orderPriceTV.text = '￥' + item.realPrice

        val status = when (item.status) {
            0 -> R.string.order_status_0
            1 -> R.string.order_status_1
            else -> R.string.order_status_2
        }
        holder.binding.orderStatusTV.setText(status)
    }
}
