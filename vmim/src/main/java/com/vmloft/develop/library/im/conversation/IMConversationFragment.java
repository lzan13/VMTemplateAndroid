package com.vmloft.develop.library.im.conversation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hyphenate.chat.EMConversation;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseFragment;
import com.vmloft.develop.library.im.chat.IMChatManager;
import com.vmloft.develop.library.im.router.IMRouter;
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.tools.adapter.VMAdapter;

import java.util.List;

/**
 * Create by lzan13 on 2019/5/9 10:34
 *
 * IM 最近会话界面，可加载到自己的容器
 */
public class IMConversationFragment extends IMBaseFragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private IMConversationAdapter mAdapter;

    private List<EMConversation> mList;

    /**
     * Fragment 的工厂方法，方便创建并设置参数
     */
    public static IMConversationFragment newInstance() {
        IMConversationFragment fragment = new IMConversationFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshConversation();

        initReceiver();
    }

    @Override
    protected int layoutId() {
        return R.layout.im_fragment_conversation;
    }

    @Override
    protected void init() {
        mRecyclerView = getView().findViewById(R.id.im_conversation_recycler_view);

        initRecyclerView();
    }

    /**
     * 初始化会话列表
     */
    private void initRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.im_conversation_recycler_view);
        mAdapter = new IMConversationAdapter(mContext, mList);
        mLayoutManager = new LinearLayoutManager(mContext);
        // 是否固定在底部
        mLayoutManager.setStackFromEnd(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(new VMAdapter.IClickListener() {
            @Override
            public void onItemAction(int action, Object object) {
                EMConversation conversation = (EMConversation) object;
                IMRouter.goIMChat(mContext, conversation.conversationId());
            }

            @Override
            public boolean onItemLongAction(int action, Object object) {
                return false;
            }
        });
    }

    /**
     * 刷新会话列表
     */
    private void refreshConversation() {
        mList = IMChatManager.getInstance().getAllConversation();
        mAdapter.refresh(mList);
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver();
    }
    /**
     * ------------------------------- 广播接收器部分 -------------------------------
     */
    /**
     * 初始化注册广播接收器
     */
    private void initReceiver() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
        // 新消息广播接收器
        IntentFilter intentFilter = new IntentFilter(IMUtils.Action.getRefreshConversationAction());
        lbm.registerReceiver(mRefreshConversationReceiver, intentFilter);
    }

    /**
     * 取消注册广播接收器
     */
    private void unregisterReceiver() {
        // 取消新消息广播接收器
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mRefreshConversationReceiver);
    }

    private RefreshConversationReceiver mRefreshConversationReceiver = new RefreshConversationReceiver();

    /**
     * 定义广播接收器
     */
    private class RefreshConversationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshConversation();
        }
    }
}
