package com.vmloft.develop.library.im.chat.msg

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.vmloft.develop.library.base.utils.CUtils
import com.vmloft.develop.library.data.bean.Attachment
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.databinding.ImItemMsgPictureReceiveDelegateBinding
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：展示图片消息 Item
 */
class MsgPictureReceiveDelegate(listener: BItemListener<IMMessage>, longListener: BItemLongListener<IMMessage>) :
    MsgCommonDelegate<ImItemMsgPictureReceiveDelegateBinding>(listener, longListener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemMsgPictureReceiveDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemMsgPictureReceiveDelegateBinding>, item: IMMessage) {
        super.onBindView(holder, item)

        val attachment = item.attachments[0]
        bindPictureSize(attachment, holder.binding.imMsgPictureIV)

        if (attachment.uri != null && CUtils.isFileExists(mContext, attachment.uri!!)) {
            IMGLoader.loadCover(holder.binding.imMsgPictureIV, attachment.uri, true, 16)
        } else {
            IMGLoader.loadCover(holder.binding.imMsgPictureIV, attachment.path, true, 16)
        }
    }

    /**
     * 计算图片展示大小
     */
    private fun bindPictureSize(attachment: Attachment, view: View) {
        val maxSize = VMDimen.getDimenPixel(R.dimen.vm_dimen_128)
        val minSize = VMDimen.getDimenPixel(R.dimen.vm_dimen_32)

        // 取出图片原始宽高，这是在发送图片时发送方直接根据图片获得设置到body中的
        var width = attachment.width
        var height = attachment.height
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
    private fun loadPicture(iv: ImageView, thumbnailUrl: String, remoteUrl: String, localUri: Uri) {
        if (CUtils.isFileExists(mContext, localUri)) {
            IMGLoader.loadCover(iv, localUri, true, 16)
        } else {
            IMGLoader.loadCover(iv, remoteUrl, true, 16, thumbnailUrl = thumbnailUrl)
        }
    }
}
