package com.vmloft.develop.library.common.ui.display

import com.vmloft.develop.library.common.R
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.databinding.DisplayItemDelegateBinding


/**
 * Create by lzan13 on 2020/02/15 17:56
 * 描述：展示美女
 */
class DisplayItemDelegate : BItemDelegate<String, DisplayItemDelegateBinding>() {

    override fun layoutId(): Int = R.layout.display_item_delegate

    override fun onBindView(holder: BItemHolder<DisplayItemDelegateBinding>, item: String) {
        holder.binding.data = item
        holder.binding.executePendingBindings()
    }
}