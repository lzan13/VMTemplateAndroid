package com.vmloft.develop.library.im.emoji;

import android.content.Context;
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
public class IMEmojiPagerAdapter extends PagerAdapter {

    private List<IMEmojiRecyclerView> mViewList = new ArrayList<>();

    public IMEmojiPagerAdapter(List<IMEmojiRecyclerView> list) {
        mViewList.addAll(list);
    }

    /**
     * 更新里数据集
     */
    public void update(List<IMEmojiRecyclerView> list) {
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
    public IMEmojiRecyclerView instantiateItem(ViewGroup container, int position) {
        IMEmojiRecyclerView view = mViewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        IMEmojiRecyclerView view = (IMEmojiRecyclerView) object;
        container.removeView(view);
    }
}
