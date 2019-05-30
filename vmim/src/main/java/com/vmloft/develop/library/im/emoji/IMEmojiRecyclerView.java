package com.vmloft.develop.library.im.emoji;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.vmloft.develop.library.tools.adapter.VMAdapter;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMLog;

/**
 * Created by lzan13 on 2019/05/29 15:00
 *
 * 表情分组列表页面控件
 */
public class IMEmojiRecyclerView extends RelativeLayout {

    // 实现表情网格布局
    private RecyclerView mRecyclerView;
    private IMEmojiRecyclerAdapter mAdapter;

    // 当前页面展示哪个表情包
    private IMEmojiGroup mEmojiGroup;

    // 动态添加到布局中的空视图
    private View emptyView;
    // 表情网格显示多少列
    private int mColumnCount;

    /**
     * 默认列数的构造方法
     *
     * @param context
     * @param group
     */
    public IMEmojiRecyclerView(Context context, IMEmojiGroup group) {
        this(context, group, IMEmojiManager.IM_EMOJI_COLUMN_COUNT_DEFAULT);
    }

    /**
     * 动态设置网格列的构造方法
     *
     * @param context
     * @param group
     * @param count
     */
    public IMEmojiRecyclerView(Context context, IMEmojiGroup group, int count) {
        super(context);
        mEmojiGroup = group;
        mColumnCount = count;

        init();
    }

    /**
     * 获取当前页面加载的表情组
     */
    public IMEmojiGroup getEmojiGroup() {
        return mEmojiGroup;
    }

    private void init() {
        if (mEmojiGroup.getEmojiCount() <= 0) {
        } else {
            initRecyclerView();
        }
    }

    /**
     * 初始化表情列表，加载表情数据
     */
    private void initRecyclerView() {
        VMLog.d("加载表情 %d", mEmojiGroup.mEmojiItemList.size());
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = VMDimen.dp2px(8);
        params.rightMargin = VMDimen.dp2px(8);
        this.addView(mRecyclerView, params);

        mAdapter = new IMEmojiRecyclerAdapter(getContext(), mEmojiGroup.mEmojiItemList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(new VMAdapter.IClickListener() {
            @Override
            public void onItemAction(int action, Object object) {
                if (mInnerListener != null) {
                    mInnerListener.onEmojiClick(mEmojiGroup, (IMEmojiItem) object);
                }
            }

            @Override
            public boolean onItemLongAction(int action, Object object) {
                return false;
            }
        });
    }

    /**
     * ----------------------- 表情内部点击接口 -----------------------
     */
    private EmojiInnerListener mInnerListener;

    public void setEmojiInnerListener(EmojiInnerListener listener) {
        mInnerListener = listener;
    }

    /**
     * 表情点击回调接口
     */
    public interface EmojiInnerListener {
        void onEmojiClick(IMEmojiGroup group, IMEmojiItem item);
    }
}
