package com.vmloft.develop.library.im.emotion;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2019/05/29 14:50
 *
 * 表情组页面适配器
 */
public class IMEmotionPagerAdapter extends PagerAdapter {

    private List<IMEmotionRecyclerView> mViewList = new ArrayList<>();

    public IMEmotionPagerAdapter(List<IMEmotionRecyclerView> list) {
        mViewList.addAll(list);
    }

    /**
     * 更新里数据集
     */
    public void update(List<IMEmotionRecyclerView> list) {
        mViewList.clear();
        mViewList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public IMEmotionRecyclerView instantiateItem(ViewGroup container, int position) {
        IMEmotionRecyclerView view = mViewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        IMEmotionRecyclerView view = (IMEmotionRecyclerView) object;
        container.removeView(view);
    }
}
