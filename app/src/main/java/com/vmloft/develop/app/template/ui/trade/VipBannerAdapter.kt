package com.vmloft.develop.app.template.ui.trade


import com.vmloft.develop.app.template.R

import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 * Create by lzan13 2022/06/19
 * 描述：自动轮播 Banner 适配器
 */
class BannerAdapter : BaseBannerAdapter<VipBannerItem>() {

    override fun bindData(holder: BaseViewHolder<VipBannerItem>, data: VipBannerItem, position: Int, pageSize: Int) {
        holder.setImageResource(R.id.iconIV, data.icon)
        holder.setText(R.id.descTV, data.desc)
    }

    override fun getLayoutId(viewType: Int) = R.layout.item_vip_banner

}

class VipBannerItem(val icon: Int, val desc: String) {}
