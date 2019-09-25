package com.vmloft.develop.library.im.emotion;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.tools.adapter.VMAdapter;
import com.vmloft.develop.library.tools.utils.VMLog;

/**
 * Created by lzan13 on 2019/05/29 15:00
 *
 * 表情分组列表页面控件
 */
public class IMEmotionRecyclerView extends RelativeLayout {

    // 实现表情网格布局
    private RecyclerView mRecyclerView;
    private IMEmotionRecyclerAdapter mAdapter;

    // 当前页面展示哪个表情包
    private IMEmotionGroup mEmotionGroup;

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
    public IMEmotionRecyclerView(Context context, IMEmotionGroup group) {
        this(context, group, IMEmotionManager.IM_EMOTION_COLUMN_COUNT_DEFAULT);
    }

    /**
     * 动态设置网格列的构造方法
     *
     * @param context
     * @param group
     * @param count
     */
    public IMEmotionRecyclerView(Context context, IMEmotionGroup group, int count) {
        super(context);
        mEmotionGroup = group;
        mColumnCount = count;

        init();
    }

    /**
     * 获取当前页面加载的表情组
     */
    public IMEmotionGroup getEmotionGroup() {
        return mEmotionGroup;
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.im_emotion_recycler_view, this);
        mRecyclerView = findViewById(R.id.im_emotion_recycler_view);

        if (mEmotionGroup.getEmotionCount() <= 0) {
            // TODO 没有表情数据，添加空提示
            VMLog.d("没有表情数据");
        } else {
            initRecyclerView();
        }
    }

    /**
     * 初始化表情列表，加载表情数据
     */
    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
        mAdapter = new IMEmotionRecyclerAdapter(getContext(), mEmotionGroup);
        mAdapter.setClickListener((VMAdapter.IClickListener<IMEmotionItem>) (action, item) -> {
            if (mInnerListener != null) {
                mInnerListener.onEmotionClick(mEmotionGroup, item);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * ----------------------- 表情内部点击接口 -----------------------
     */
    private IEmotionListener mInnerListener;

    public void setEmotionListener(IEmotionListener listener) {
        mInnerListener = listener;
    }

    /**
     * 表情点击回调接口
     */
    public interface IEmotionListener {
        void onEmotionClick(IMEmotionGroup group, IMEmotionItem item);
    }
}
