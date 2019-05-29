package com.vmloft.develop.library.im.emoji;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.utils.IMUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/29 11:10
 *
 * 表情模块儿
 */
public class IMEmojiView extends RelativeLayout {


    private ImageButton mAddBtn;
    private ImageButton mDelBtn;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private IMEmojiPagerAdapter mAdapter;

    // 表情组信息
    private List<IMEmojiGroup> mEmojiGroupList;
    private List<IMEmojiPageView> mPageViewList;
    private IMEmojiPageView.EmojiInnerListener mInnerListener;

    public IMEmojiView(Context context) {
        super(context);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.im_chat_emoji_view, this);

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
        mEmojiGroupList = IMEmojiManager.getInstance().getEmojiGroupList();
        mPageViewList = new ArrayList<>();
        mAdapter = new IMEmojiPagerAdapter(mPageViewList);
        mViewPager.setAdapter(mAdapter);

        mDelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiListener != null) {
                    mEmojiListener.onDeleteEmoji();
                }
            }
        });
        mInnerListener = new IMEmojiPageView.EmojiInnerListener() {
            @Override
            public void onEmojiClick(IMEmojiGroup group, IMEmojiItem item) {
                if (mEmojiListener != null) {
                    mEmojiListener.onInsertEmoji(group, item);
                }
            }
        };

        IMUtils.singleAsync(new Runnable() {
            @Override
            public void run() {
                mPageViewList = bindEmojiPageView();
                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });
    }

    /**
     * 加载表情页
     */
    private List<IMEmojiPageView> bindEmojiPageView() {
        List<IMEmojiPageView> list = new ArrayList<>();
        List<IMEmojiGroup> groups = IMEmojiManager.getInstance().getEmojiGroupList();
        if (groups == null || groups.size() <= 0) {
            return list;
        }
        for (int i = 0; i < groups.size(); i++) {
            IMEmojiGroup group = groups.get(i);
            if (group.mEmojiItemList.size() <= 0) {
                // TODO 表情数量为空时，展示空视图，同时可触发去网络下载的逻辑
                // pageViewList.add(emptyView);
            } else {
                boolean isEmojiGroup = group.isEmoji;
                IMEmojiPageView pageView;
                if (isEmojiGroup) {
                    pageView = new IMEmojiPageView(getContext(), group, IMEmojiManager.IM_EMOJI_COLUMN_COUNT);
                } else {
                    pageView = new IMEmojiPageView(getContext(), group);
                }
                pageView.setEmojiInnerListener(mInnerListener);
                list.add(pageView);
            }
        }
        return list;
    }

    /**
     * 刷新表情数据
     */
    private void refresh() {
        if (mAdapter != null) {

            mAdapter.updateData(mPageViewList);
            mAdapter.notifyDataSetChanged();
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
