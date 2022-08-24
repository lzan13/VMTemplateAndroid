package com.vmloft.develop.app.template.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vmloft.develop.app.template.R

import com.vmloft.develop.app.template.databinding.ItemAppletDelegateBinding
import com.vmloft.develop.library.data.bean.Applet
import com.vmloft.develop.library.data.bean.Attachment
import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.tools.utils.VMDimen

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示程序内容 Item
 */
class ItemAppletDelegate(listener: BItemListener<Applet>, longListener: BItemLongListener<Applet>) : BItemDelegate<Applet, ItemAppletDelegateBinding>(listener, longListener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ItemAppletDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ItemAppletDelegateBinding>, item: Applet) {

        if (item.cover != null && item.cover.path.isNotEmpty()) {
            updateCoverRatio(holder.binding.coverIV, item.cover)
            IMGLoader.loadCover(holder.binding.coverIV, item.cover.path, true)
        }

        holder.binding.titleTV.text = item.title
        holder.binding.contentTV.text = item.content

        if (item.tips.isNotEmpty()) {
            holder.binding.tipsTV.visibility = View.VISIBLE
            holder.binding.tipsTV.text = item.tips
        } else {
            holder.binding.tipsTV.visibility = View.GONE
        }

        holder.binding.vipTV.visibility = if (ConfigManager.appConfig.tradeConfig.vipEntry && item.isNeedVIP) View.VISIBLE else View.GONE
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

}