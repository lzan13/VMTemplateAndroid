package com.vmloft.develop.library.im.emotion;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.common.IMExecutor;

import com.vmloft.develop.library.tools.utils.VMSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/29 11:10
 *
 * 表情模块儿
 */
public class IMEmotionPager extends RelativeLayout {

    private ImageButton mAddBtn;
    private ImageButton mDeleteBtn;

    private String[] mTitles = {"小","大"};
    private IMEmotionTab mTab;
    private List<View> mCustomTabs = new ArrayList<>();

    private IMEmotionPagerAdapter mAdapter;
    private ViewPager mViewPager;

    // 表情组列表页面集合
    private List<IMEmotionRecyclerView> mPageViewList;
    private IMEmotionRecyclerView.IEmotionListener mInnerListener;

    public IMEmotionPager(Context context) {
        super(context);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.im_emotion_pager, this);

        mAddBtn = findViewById(R.id.im_emotion_add_btn);
        mDeleteBtn = findViewById(R.id.im_emotion_delete_btn);
        mTab = findViewById(R.id.im_emotion_tab);
        //mTabLayout = findViewById(R.id.im_emotion_tab_layout);
        mViewPager = findViewById(R.id.im_emotion_view_pager);

        initEmotionViewPager();
    }

    /**
     * 初始化表情 ViewPager
     */
    private void initEmotionViewPager() {
        mPageViewList = new ArrayList<>();
        mAdapter = new IMEmotionPagerAdapter(mPageViewList);
        mViewPager.setAdapter(mAdapter);

        mDeleteBtn.setOnClickListener((View v) -> {
            if (mEmotionListener != null) {
                mEmotionListener.onDeleteEmotion();
            }
        });
        mInnerListener = (IMEmotionGroup group, IMEmotionItem item) -> {
            if (mEmotionListener != null) {
                mEmotionListener.onInsertEmotion(group, item);
            }
        };
    }

    /**
     * 加载数据
     */
    public void loadData() {
        IMExecutor.asyncSingleTask(() -> {
            mPageViewList = loadEmotionPageView();
            // TODO 这种方式在一些低端设备上回加载不出数据
            //            mViewPager.post(() post-> {
            //                refresh();
            //            });
            // TODO 所以使用这种方式去加载
            VMSystem.runInUIThread(() -> {
                refresh();
            });
        });
    }

    /**
     * 加载表情页
     */
    private List<IMEmotionRecyclerView> loadEmotionPageView() {
        List<IMEmotionRecyclerView> viewList = new ArrayList<>();
        List<IMEmotionGroup> groupList = IMEmotionManager.getInstance().getEmotionGroupList();
        if (groupList == null || groupList.size() <= 0) {
            return viewList;
        }
        for (int i = 0; i < groupList.size(); i++) {
            IMEmotionGroup group = groupList.get(i);
            if (group.mEmotionItemList.size() <= 0) {
                // TODO 表情数量为空时，展示空视图，同时可触发去网络下载的逻辑
                // pageViewList.add(emptyView);
            } else {
                IMEmotionRecyclerView pageView;
                if (group.isInnerEmotion) {
                    if (group.isBigEmotion) {
                        pageView = new IMEmotionRecyclerView(getContext(), group);
                    } else {
                        pageView = new IMEmotionRecyclerView(getContext(), group, IMEmotionManager.IM_EMOTION_COLUMN_COUNT);
                    }
                } else {
                    pageView = new IMEmotionRecyclerView(getContext(), group);
                }
                pageView.setEmotionListener(mInnerListener);
                viewList.add(pageView);
            }
        }
        return viewList;
    }

    /**
     * 刷新表情数据
     */
    private void refresh() {
        if (mAdapter != null) {
            mAdapter.update(mPageViewList);
            setupTabLayout();
        }
    }

    /**
     * 装载 TabLayout
     */
    private void setupTabLayout() {
        List<Integer> resList = new ArrayList<>();
        for (int i = 0; i < mPageViewList.size(); i++) {
            resList.add(mPageViewList.get(i).getEmotionGroup().mResId);
        }
        mTab.setViewPager(mViewPager);
        mTab.setResList(resList);
    }

    /**
     * ----------------------- 表情对外操作接口 -----------------------
     */
    private IIMEmotionListener mEmotionListener;

    public void stEmotionListener(IIMEmotionListener listener) {
        mEmotionListener = listener;
    }

    /**
     * 表情对外操作接口
     */
    public interface IIMEmotionListener {
        /**
         * 插入表情
         *
         * @param group 表情所在组
         * @param item  表情
         */
        void onInsertEmotion(IMEmotionGroup group, IMEmotionItem item);

        /**
         * 删除表情
         */
        void onDeleteEmotion();
    }
}
