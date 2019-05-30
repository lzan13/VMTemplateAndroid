package com.vmloft.develop.library.im.emoji;

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

    private List<IMEmojiRecyclerView> mDataList = new ArrayList<>();

    public IMEmojiPagerAdapter(List<IMEmojiRecyclerView> dataList) {
        updateData(dataList);
    }

    public void updateData(List<IMEmojiRecyclerView> list) {
        mDataList.clear();
        if (list != null && !list.isEmpty()) {
            mDataList.addAll(list);
        }
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    public IMEmojiRecyclerView getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        IMEmojiRecyclerView pageView = mDataList.get(position);
        pageView.setTag(pageView.getEmojiGroup().mGroupName);

        // 对需要添加的 view 做个判断，防止出现 java.lang.IllegalStateException 错误
        ViewGroup parent = (ViewGroup) pageView.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }

        container.addView(pageView);
        return mDataList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mDataList.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public List<IMEmojiRecyclerView> getDataList() {
        return mDataList;
    }

}
