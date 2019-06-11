package com.vmloft.develop.app.match.ui.guide;

import android.animation.ArgbEvaluator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.common.ASPManager;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.tools.adapter.VMFragmentPagerAdapter;
import com.vmloft.develop.library.tools.utils.VMColor;
import com.vmloft.develop.library.tools.widget.indicator.VMIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by lzan13 on 2019/04/08
 *
 * 程序引导界面
 */
public class GuideActivity extends AppActivity {
    @BindView(R.id.guide_view_pager) ViewPager mViewPager;
    @BindView(R.id.guide_indicator_view) VMIndicatorView mIndicatorView;
    @BindView(R.id.btn_prev) Button prevBtn;
    @BindView(R.id.btn_next) Button nextBtn;
    @BindView(R.id.btn_finish) Button finishBtn;

    private int mCurrentIndex;
    private List<Fragment> mFragmentList;
    private GuideAdapter mAdapter;

    @Override
    protected int layoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    @Override
    protected void initData() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(GuideFragment.newInstance(R.drawable.img_guide_1, R.string.guide_title_0, R.string.guide_intro_0));
        mFragmentList.add(GuideFragment.newInstance(R.drawable.img_guide_2, R.string.guide_title_1, R.string.guide_intro_1));
        mFragmentList.add(GuideFragment.newInstance(R.drawable.img_guide_3, R.string.guide_title_2, R.string.guide_intro_2));

        mAdapter = new GuideAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setOffscreenPageLimit(mFragmentList.size() - 1);
        mViewPager.setAdapter(mAdapter);
        mIndicatorView.setViewPager(mViewPager);

        /**
         * 通过监听 ViewPager 页面滑动渐变调整 ViewPager 的背景
         */
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex = position;
                prevBtn.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                nextBtn.setVisibility(position == mFragmentList.size() - 1 ? View.GONE : View.VISIBLE);
                finishBtn.setVisibility(position == mFragmentList.size() - 1 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({ R.id.btn_prev, R.id.btn_next, R.id.btn_finish })
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.btn_prev:
            mCurrentIndex -= 1;
            mViewPager.setCurrentItem(mCurrentIndex, true);
            break;
        case R.id.btn_next:
            mCurrentIndex += 1;
            mViewPager.setCurrentItem(mCurrentIndex, true);
            break;
        case R.id.btn_finish:
            ASPManager.getInstance().setGuideState();
            ARouter.goMain(mActivity);
            break;
        }
    }

    /**
     * 引导界面适配器类
     */
    public class GuideAdapter extends VMFragmentPagerAdapter {

        public GuideAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm, list);
        }
    }
}
