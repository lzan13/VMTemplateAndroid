package com.vmloft.develop.app.match.ui.guide;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmloft.develop.app.match.R;

import butterknife.BindView;

import com.vmloft.develop.app.match.base.AppLazyFragment;

/**
 * Create by lzan13 on 2019/04/09
 *
 * 引导界面 Fragment 实现容器
 */
public class GuideFragment extends AppLazyFragment {

    private static final String ARG_IMG_ID = "arg_img_id";
    private static final String ARG_TITLE_ID = "arg_title_id";
    private static final String ARG_BODY_ID = "arg_body_id";

    @BindView(R.id.guide_img) ImageView imgView;
    @BindView(R.id.guide_title) TextView titleView;
    @BindView(R.id.guide_body) TextView bodyView;

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
    protected void initData() {
        imgView.setImageResource(getArguments().getInt(ARG_IMG_ID));
        titleView.setText(getArguments().getInt(ARG_TITLE_ID));
        bodyView.setText(getArguments().getInt(ARG_BODY_ID));

        loadAnim();
    }

    private void loadAnim() {
        // 图片透明度改变动画
        ObjectAnimator imgAlphaAnimator = ObjectAnimator.ofFloat(imgView, "alpha", 0f, 1f);
        imgAlphaAnimator.setDuration(360);
        // 图片旋转动画
        ObjectAnimator imgRotationAnimator = ObjectAnimator.ofFloat(imgView, "rotation", 0, 15, -15, 0);
        imgRotationAnimator.setDuration(360);
        AnimatorSet imgSetAnimator = new AnimatorSet();
        imgSetAnimator.play(imgAlphaAnimator).with(imgRotationAnimator);
        imgSetAnimator.start();

        // 标题透明度变化
        ObjectAnimator titleAlphaAnimator = ObjectAnimator.ofFloat(imgView, "alpha", 0f, 1f);
        // 标题缩放动画
        ObjectAnimator titleScaleXAnimator = ObjectAnimator.ofFloat(titleView, "scaleX", 0, 1.8f, 1f);
        ObjectAnimator titleScaleYAnimator = ObjectAnimator.ofFloat(titleView, "ScaleY", 0, 1.8f, 1f);
        AnimatorSet titleSetAnimator = new AnimatorSet();
        titleSetAnimator.play(titleAlphaAnimator).with(titleScaleXAnimator).with(titleScaleYAnimator);
        titleSetAnimator.setDuration(360);
        titleSetAnimator.start();

        // 显示描述
        bodyView.postDelayed(new Runnable() {
            @Override
            public void run() {
                bodyView.setVisibility(View.VISIBLE);
            }
        }, 360);
    }

    @Override
    protected boolean isNeedLoading() {
        return false;
    }
}
