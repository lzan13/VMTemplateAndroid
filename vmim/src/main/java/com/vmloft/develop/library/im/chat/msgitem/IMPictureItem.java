package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.router.IMRouter;
import com.vmloft.develop.library.im.utils.IMChatUtils;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap;

import java.io.File;

/**
 * Create by lzan13 on 2019/5/23 22:17
 *
 * 实现文本消息展示
 */
public class IMPictureItem extends IMNormalItem {

    private ImageView mPictureView;

    // 定义图片缩略图限制
    private int thumbnailsMax;
    private int thumbnailsMin;
    private int mViewWidth;
    private int mViewHeight;

    public IMPictureItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
        thumbnailsMax = VMDimen.getDimenPixel(R.dimen.vm_dimen_128);
        thumbnailsMin = VMDimen.getDimenPixel(R.dimen.vm_dimen_56);
    }

    @Override
    protected boolean isReceiveMessage() {
        return mType == IMConstants.MsgType.IM_IMAGE_RECEIVE;
    }

    @Override
    protected View layoutView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.im_msg_item_picture, null);
        mPictureView = view.findViewById(R.id.im_msg_picture_iv);
        return view;
    }

    @Override
    public void onBind(int position, EMMessage message) {
        super.onBind(position, message);

        EMImageMessageBody body = (EMImageMessageBody) message.getBody();
        // 取出图片原始宽高，这是在发送图片时发送方直接根据图片获得设置到body中的
        int width = body.getWidth();
        int height = body.getHeight();
        float scale = VMBitmap.getZoomScale(width, height, thumbnailsMax);
        // 根据图片原图大小，来计算缩略图要显示的大小，直接设置控件宽高
        if (width <= thumbnailsMax && height <= thumbnailsMax) {
            if (width < thumbnailsMin) {
                mPictureView.getLayoutParams().width = thumbnailsMin;
                mPictureView.getLayoutParams().height = height * thumbnailsMin / width;
            } else {
                mPictureView.getLayoutParams().width = width;
                mPictureView.getLayoutParams().height = height;
            }
        } else {
            mPictureView.getLayoutParams().width = (int) (width / scale);
            mPictureView.getLayoutParams().height = (int) (height / scale);
        }
        mViewWidth = mPictureView.getLayoutParams().width;
        mViewHeight = mPictureView.getLayoutParams().height;

        // 缩略图本地路径
        String thumbnailsPath = "";
        // 原图在本地路径
        String originalPath = "";
        if (mType == IMConstants.MsgType.IM_IMAGE_RECEIVE) {
            // 接收方获取缩略图的路径
            originalPath = body.getLocalUrl();
            thumbnailsPath = body.thumbnailLocalPath();
        } else {
            // 发送方获取图片路径
            originalPath = body.getLocalUrl();
            thumbnailsPath = IMChatUtils.getThumbImagePath(originalPath);
        }
        // 为图片显示控件设置tag，在设置图片显示的时候，先判断下当前的tag是否是当前item的，是则显示图片
        //        imageView.setTag(thumbnailsPath);
        // 设置缩略图的显示
        showThumbnailsImage(thumbnailsPath, originalPath);
    }

    /**
     * 设置缩略图的显示，并将缩略图添加到缓存
     *
     * @param thumbnailsPath 缩略图的路径
     * @param originalPath   原始图片的路径
     */
    private void showThumbnailsImage(String thumbnailsPath, String originalPath) {
        File thumbnailsFile = new File(thumbnailsPath);
        File originalFile = new File(originalPath);
        String url = thumbnailsPath;
        // 根据图片存在情况加载缩略图显示
        if (originalFile.exists()) {
            // 原图存在，直接通过原图加载缩略图
            url = originalPath;
        } else if (!originalFile.exists() && thumbnailsFile.exists()) {
            // 原图不存在，只存在缩略图
            url = thumbnailsPath;
        } else if (!originalFile.exists() && !thumbnailsFile.exists()) {
            // 原图和缩略图都不存在
            url = thumbnailsPath;
        }
        IPictureLoader.Options options = new IPictureLoader.Options(url);
        options.isRadius = true;
        options.radiusSize = VMDimen.dp2px(8);
        IM.getInstance().getPictureLoader().load(mContext, options, mPictureView);
    }

    @Override
    public void onClick() {
        IMRouter.goIMPreview(mContext, mMessage);
    }
}
