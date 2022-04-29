package com.vmloft.develop.library.ads

import android.view.LayoutInflater
import android.view.ViewGroup

import com.vmloft.develop.library.ads.databinding.AdsItemDelegateBinding
import com.vmloft.develop.library.base.BItemDelegate

/**
 * Create by lzan13 on 2022/04/25 21:56
 * 描述：展示广告内容 Item
 */
class ADSItemDelegate : BItemDelegate<ADSItem, AdsItemDelegateBinding>() {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = AdsItemDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<AdsItemDelegateBinding>, item: ADSItem) {
        ADSManager.showNativeAD(mContext, holder.binding.adsContainerCL)
    }

}
