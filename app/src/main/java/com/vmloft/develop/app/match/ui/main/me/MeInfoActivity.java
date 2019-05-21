package com.vmloft.develop.app.match.ui.main.me;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppActivity;

/**
 * Create by lzan13 on 2019/5/12 22:20
 *
 * 个人信息界面
 */
public class MeInfoActivity extends AppActivity {

    @Override
    protected int layoutId() {
        return R.layout.activity_me_info;
    }

    @Override
    protected void initUI() {
        super.initUI();

        startPickAvatar();
    }

    /**
     * 开启选择头像
     */
    private void startPickAvatar() {
        //VMPicker.getInstance()
        //    .setMultiMode(isMultiMode)
        //    .setPictureLoader(new GlideIPictureLoader())
        //    .setCrop(isCrop)
        //    .setCropFocusWidth(mCropFocusWidth)
        //    .setCropFocusHeight(mCropFocusHeight)
        //    .setCropOutWidth(mCropOutWidth)
        //    .setCropOutHeight(mCropOutHeight)
        //    .setCropStyle(mCropStyle)
        //    .setSaveRectangle(isSaveRectangle)
        //    .setSelectLimit(6)
        //    .setShowCamera(isShowCamera)
        //    .setSelectedPictures(mSelectPictures)
        //    .startPicker(mActivity);
    }

    @Override
    protected void initData() {
        setTopTitle(R.string.me_info);
    }
}
