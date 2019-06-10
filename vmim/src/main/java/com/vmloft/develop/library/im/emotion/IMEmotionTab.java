package com.vmloft.develop.library.im.emotion;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vmloft.develop.library.im.R;

import com.vmloft.develop.library.tools.utils.VMDimen;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/6/9 10:42
 */
public class IMEmotionTab extends HorizontalScrollView implements ViewPager.OnPageChangeListener {

    private List<Integer> mResList;
    private List<View> mTabList = new ArrayList<>();

    private int mTabSize;
    private ViewPager mViewPager;

    public IMEmotionTab(Context context) {
        this(context, null);
    }

    public IMEmotionTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IMEmotionTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setHorizontalScrollBarEnabled(false);
        mTabSize = VMDimen.dp2px(40);
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        if (mViewPager != null) {
            mViewPager.addOnPageChangeListener(this);
        }
    }

    /**
     * 设 tab 资源 id 集合
     */
    public void setResList(List<Integer> list) {
        mResList = list;
        createTabItem();
    }

    /**
     * 创建 ItemView
     */
    private void createTabItem() {
        if (getChildCount() > 0) {
            this.removeAllViews();
        }

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(mResList.size() * mTabSize, mTabSize));

        for (int i = 0; i < mResList.size(); i++) {
            ImageView imgView = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mTabSize, mTabSize);
            imgView.setLayoutParams(lp);
            int padding = mTabSize / 5;
            imgView.setPadding(padding, padding, padding, padding);
            imgView.setImageResource(mResList.get(i));
            imgView.setBackgroundResource(R.drawable.im_common_selector);
            final int position = i;
            mTabList.add(imgView);
            linearLayout.addView(imgView);
            imgView.setOnClickListener((View v) -> {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(position);
                }
            });
        }
        addView(linearLayout);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mTabList.size(); i++) {
            View view = mTabList.get(i);
            view.setSelected(false);
            if (i == position) {
                view.setSelected(true);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}
