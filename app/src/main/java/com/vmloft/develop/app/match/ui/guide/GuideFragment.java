package com.vmloft.develop.app.match.ui.guide;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vmloft.develop.app.match.R;

import butterknife.BindView;

import com.vmloft.develop.app.match.base.AppLazyFragment;
import com.vmloft.develop.library.tools.utils.VMTheme;

/**
 * Create by lzan13 on 2019/04/09
 *
 * 引导界面 Fragment 实现容器
 */
public class GuideFragment extends AppLazyFragment {

    private static final String ARG_IMG_ID = "arg_img_id";
    private static final String ARG_TITLE_ID = "arg_title_id";
    private static final String ARG_BODY_ID = "arg_body_id";

    @BindView(R.id.guide_card_ll) LinearLayout mCardLayout;
    @BindView(R.id.guide_cover_iv) ImageView imgView;
    @BindView(R.id.guide_title_tv) TextView titleView;
    @BindView(R.id.guide_body_tv) TextView bodyView;

    /**
     * Fragment 的工厂方法，方便创建并设置参数
     */
    public static GuideFragment newInstance(int imgId, int titleId, int bodyId) {
        GuideFragment fragment = new GuideFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_IMG_ID, imgId);
        args.putInt(ARG_TITLE_ID, titleId);
        args.putInt(ARG_BODY_ID, bodyId);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_guide;
    }

    @Override
    protected void initView() {
        super.initView();
        VMTheme.changeShadow(mCardLayout);

        imgView.setImageResource(getArguments().getInt(ARG_IMG_ID));
        titleView.setText(getArguments().getInt(ARG_TITLE_ID));
        bodyView.setText(getArguments().getInt(ARG_BODY_ID));
    }

    @Override
    protected void initData() {
        loadAnim();
    }

    private void loadAnim() {

    }

    @Override
    protected boolean isNeedLoading() {
        return false;
    }
}
