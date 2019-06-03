package com.vmloft.develop.library.im.emoji;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
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
public class IMEmojiPager extends RelativeLayout {

    private ImageButton mAddBtn;
    private ImageButton mDelBtn;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private IMEmojiPagerAdapter mAdapter;

    // 表情组列表页面集合
    private List<IMEmojiRecyclerView> mPageViewList;
    private IMEmojiRecyclerView.EmojiInnerListener mInnerListener;

    public IMEmojiPager(Context context) {
        super(context);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.im_emoji_pager, this);

        mAddBtn = findViewById(R.id.im_emoji_add_btn);
        mDelBtn = findViewById(R.id.im_emoji_delete_btn);
        mTabLayout = findViewById(R.id.im_emoji_tab_layout);
        mViewPager = findViewById(R.id.im_emoji_view_pager);

        initEmojiViewPager();
    }

    /**
     * 初始化表情 ViewPager
     */
    private void initEmojiViewPager() {
        mPageViewList = new ArrayList<>();
        mAdapter = new IMEmojiPagerAdapter(mPageViewList);
        mViewPager.setAdapter(mAdapter);

        mDelBtn.setOnClickListener((View v) -> {
            if (mEmojiListener != null) {
                mEmojiListener.onDeleteEmoji();
            }
        });
        mInnerListener = (IMEmojiGroup group, IMEmojiItem item) -> {
            if (mEmojiListener != null) {
                mEmojiListener.onInsertEmoji(group, item);
            }
        };
    }

    /**
     * 加载数据
     */
    public void loadData() {
        IMExecutor.asyncSingleTask(() -> {
            mPageViewList = bindEmojiPageView();
            VMSystem.runInUIThread(() -> {refresh();});
        });
    }

    /**
     * 加载表情页
     */
    private List<IMEmojiRecyclerView> bindEmojiPageView() {
        List<IMEmojiRecyclerView> viewList = new ArrayList<>();
        List<IMEmojiGroup> groupList = IMEmojiManager.getInstance().getEmojiGroupList();
        if (groupList == null || groupList.size() <= 0) {
            return viewList;
        }
        for (int i = 0; i < groupList.size(); i++) {
            IMEmojiGroup group = groupList.get(i);
            if (group.mEmojiItemList.size() <= 0) {
                // TODO 表情数量为空时，展示空视图，同时可触发去网络下载的逻辑
                // pageViewList.add(emptyView);
            } else {
                IMEmojiRecyclerView pageView;
                if (group.isEmoji && group.isEmojiBig) {
                    pageView = new IMEmojiRecyclerView(getContext(), group);
                } else {
                    pageView = new IMEmojiRecyclerView(getContext(), group, IMEmojiManager.IM_EMOJI_COLUMN_COUNT);
                }
                pageView.setEmojiInnerListener(mInnerListener);
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
        }
    }

    /**
     * ----------------------- 表情对外操作接口 -----------------------
     */
    private IIMEmojiListener mEmojiListener;

    public void stEmojiListener(IIMEmojiListener listener) {
        mEmojiListener = listener;
    }

    /**
     * 表情对外操作接口
     */
    public interface IIMEmojiListener {
        /**
         * 插入表情
         *
         * @param group 表情所在组
         * @param item  表情
         */
        void onInsertEmoji(IMEmojiGroup group, IMEmojiItem item);

        /**
         * 删除表情
         */
        void onDeleteEmoji();
    }
}
