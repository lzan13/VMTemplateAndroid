package com.vmloft.develop.library.im.chat.msg

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.hyphenate.chat.EMImageMessageBody
import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.base.utils.CUtils
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.databinding.ImItemMsgPictureSendDelegateBinding
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示图片消息 Item
 */
class MsgPictureSendDelegate(listener: BItemListener<EMMessage>, longListener: BItemLongListener<EMMessage>) : MsgCommonDelegate<ImItemMsgPictureSendDelegateBinding>(listener, longListener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemMsgPictureSendDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemMsgPictureSendDelegateBinding>, item: EMMessage) {
        super.onBindView(holder, item)

        val body = item.body as EMImageMessageBody
        bindPictureSize(body, holder.binding.imMsgPictureIV)

        // 图片路径
        var originalRemoteUrl = body.remoteUrl
        var originalLocalUri = body.localUri

        loadPicture(holder.binding.imMsgPictureIV, originalRemoteUrl, originalLocalUri)
    }

    /**
     * 计算图片展示大小
     */
    private fun bindPictureSize(body: EMImageMessageBody, view: View) {
        val maxSize = VMDimen.getDimenPixel(R.dimen.vm_dimen_128)
        val minSize = VMDimen.getDimenPixel(R.dimen.vm_dimen_32)

        // 取出图片原始宽高，这是在发送图片时发送方直接根据图片获得设置到body中的
        var width = body.width
        var height = body.height
        val scale = VMBitmap.getZoomScale(width, height, maxSize)


        // 根据图片原图大小，来计算缩略图要显示的大小，直接设置控件宽高
        if (width <= maxSize && height <= maxSize) {
            if (width < minSize) {
                width = minSize
                height = height * minSize / width;
            }
        } else {
            width /= scale
            height /= scale
        }
        view.layoutParams.width = width
        view.layoutParams.height = height
    }

    /**
     * 设置缩略图的显示，并将缩略图添加到缓存
     *
     *
     * @param remoteUrl   图片的远程路径
     * @param localUri   图片的本地路径
     */
    private fun loadPicture(iv: ImageView, remoteUrl: String, localUri: Uri) {
        if (CUtils.isFileExists(mContext, localUri)) {
            IMGLoader.loadCover(iv, localUri, true, 16)
        } else {
            IMGLoader.loadCover(iv, remoteUrl, true, 16)
        }
    }

}
