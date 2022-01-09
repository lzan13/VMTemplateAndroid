package com.vmloft.develop.library.common.ui.display

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vmloft.develop.library.common.R
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.databinding.DisplayItemDelegateBinding
import com.vmloft.develop.library.common.image.IMGLoader


/**
 * Create by lzan13 on 2020/02/15 17:56
 * 描述：展示图片Item
 */
class DisplayItemDelegate : BItemDelegate<String, DisplayItemDelegateBinding>() {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = DisplayItemDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<DisplayItemDelegateBinding>, item: String) {
        IMGLoader.loadCover(holder.binding.displayItemIV, item,thumbExt = "")
    }
}