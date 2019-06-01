package com.vmloft.develop.library.im.chat;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.picker.VMPicker;
import com.vmloft.develop.library.tools.utils.VMDimen;

import com.vmloft.develop.library.tools.utils.VMFile;
import java.util.List;

/**
 * Create by lzan13 on 2019/05/16 21:51
 *
 * 图片预览适配器
 */
public class IMPreviewPageAdapter extends PagerAdapter {

    private int screenWidth;
    private int screenHeight;
    private List<EMMessage> mMessages;
    private Activity mActivity;
    public OnPreviewClickListener listener;

    public IMPreviewPageAdapter(Activity activity, List<EMMessage> list) {
        this.mActivity = activity;
        this.mMessages = list;

        screenWidth = VMDimen.getScreenSize().x;
        screenHeight = VMDimen.getScreenSize().x;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mActivity);
        EMMessage message = mMessages.get(position);
        EMImageMessageBody body = (EMImageMessageBody) message.getBody();
        String url;
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            url = body.getRemoteUrl();
        } else {
            url = body.getLocalUrl();
            if (!VMFile.isFileExists(url)) {
                url = body.getRemoteUrl();
            }
        }
        IPictureLoader.Options options = new IPictureLoader.Options(url);
        IM.getInstance().getPictureLoader().load(mActivity, options, photoView);

        photoView.setOnPhotoTapListener((ImageView view, float x, float y) -> {
            if (listener != null) {
                listener.onPreviewClick(view, x, y);
            }
        });
        container.addView(photoView);
        return photoView;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * 设置预览点击监听接口
     */
    public void setPreviewClickListener(OnPreviewClickListener listener) {
        this.listener = listener;
    }

    /**
     * 定义预览点击监听接口
     */
    public interface OnPreviewClickListener {
        void onPreviewClick(View view, float x, float y);
    }
}
