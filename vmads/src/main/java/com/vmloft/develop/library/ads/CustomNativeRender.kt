package com.vmloft.develop.library.ads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.anythink.nativead.api.ATNativeAdRenderer
import com.anythink.nativead.unitgroup.api.CustomNativeAd
import com.vmloft.develop.library.image.IMGLoader

/**
 * Create by lzan13 on 2022/4/26
 * 描述：自定义原生广告加载
 */
class CustomNativeRender : ATNativeAdRenderer<CustomNativeAd> {
    private val clickViewList = mutableListOf<View>()

    /**
     * 用于创建自定义的Native广告布局的View
     */
    override fun createView(context: Context, firmId: Int): View {
        //布局文件请参考Demo
        val view = LayoutInflater.from(context).inflate(R.layout.ads_custom_native_view, null)
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        return view
    }

    /**
     * 用于实现广告内容渲染的方法，其中 customNativeAd 是广告素材的对象，可提供素材进行渲染
     */
    override fun renderAdView(view: View, ad: CustomNativeAd) {
        val adsMediaContainerCL = view.findViewById<ViewGroup>(R.id.adsMediaContainerCL)
        val coverIV = view.findViewById<ImageView>(R.id.coverIV)
        val adLogoIV = view.findViewById<ImageView>(R.id.adLogoIV)
        val titleTV = view.findViewById<TextView>(R.id.titleTV)
        val contentTV = view.findViewById<TextView>(R.id.contentTV)
        val avatarIV = view.findViewById<ImageView>(R.id.avatarIV)
        val actionTV = view.findViewById<TextView>(R.id.actionTV)
        val nameTV = view.findViewById<TextView>(R.id.nameTV)

        val mediaView = ad.getAdMediaView(adsMediaContainerCL, adsMediaContainerCL.width)

        // 模板渲染（个性化模板、自动渲染）
        if (ad.isNativeExpress) {
            if (mediaView!!.parent != null) {
                (mediaView.parent as ViewGroup).removeView(mediaView)
            }
            adsMediaContainerCL.addView(mediaView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT))
            return
        }

        // 自渲染
        // 加载主图
        IMGLoader.loadAvatar(avatarIV, ad.iconImageUrl)
        if (ad.mainImageUrl.isNullOrEmpty()) {
            coverIV.visibility = View.GONE
        } else {
            coverIV.visibility = View.VISIBLE
            IMGLoader.loadCover(coverIV, ad.mainImageUrl, radiusTL = 8, radiusTR = 8)
        }

        // 加载 ADS logo 图标
        if (ad.adChoiceIconUrl.isNullOrEmpty()) {
            adLogoIV.visibility = View.GONE
        } else {
            adLogoIV.visibility = View.VISIBLE
            IMGLoader.loadCover(adLogoIV, ad.adChoiceIconUrl)
        }

        // 标题
        if (ad.title.isNullOrEmpty()) {
            titleTV.visibility = View.GONE
        } else {
            titleTV.visibility = View.VISIBLE
            titleTV.text = ad.title
        }
        // 描述
        if (ad.descriptionText.isNullOrEmpty()) {
            contentTV.visibility = View.GONE
        } else {
            contentTV.visibility = View.VISIBLE
            contentTV.text = ad.descriptionText
        }
        // 动作
        if (ad.callToActionText.isNullOrEmpty()) {
            actionTV.visibility = View.GONE
        } else {
            actionTV.visibility = View.VISIBLE
            actionTV.text = ad.callToActionText
        }

//        if (!ad.adFrom.isNullOrEmpty()) {
//            adFromView.text = if (ad.adFrom != null) ad.adFrom else ""
//            adFromView.visibility = View.VISIBLE
//        } else {
//            adFromView.visibility = View.GONE
//        }

        clickViewList.add(coverIV)
        clickViewList.add(titleTV)
        clickViewList.add(contentTV)
        clickViewList.add(avatarIV)
        clickViewList.add(actionTV)
    }

    fun getClickView(): List<View> {
        return clickViewList
    }
}