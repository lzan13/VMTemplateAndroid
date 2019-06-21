package com.vmloft.develop.app.match.ui.main.chat;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppLazyFragment;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.im.conversation.IMConversationFragment;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.widget.VMTopBar;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

import butterknife.OnClick;

/**
 * Create by lzan13 on 2019/05/28 12:54
 *
 * 会话列表界面
 */
public class ConversationFragment extends AppLazyFragment {

    private IMConversationFragment mConversationFragment;

    protected View mTopSpaceView;
    protected VMTopBar mTopBar;

    /**
     * Fragment 的工厂方法，方便创建并设置参数
     */
    public static ConversationFragment newInstance() {
        ConversationFragment fragment = new ConversationFragment();

        Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_conversation;
    }

    @Override
    protected void initView() {
        super.initView();

        mTopSpaceView = getView().findViewById(R.id.common_top_space);
        mTopBar = getView().findViewById(R.id.common_top_bar);

        // 设置状态栏透明主题时，布局整体会上移，所以给头部 View 设置 StatusBar 的高度
        mTopSpaceView.getLayoutParams().height = VMDimen.getStatusBarHeight();
        mTopBar.setTitle("聊天会话");

    }

    @Override
    protected void initData() {
        mConversationFragment = IMConversationFragment.newInstance();
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.conversation_container, mConversationFragment);
        ft.commit();
    }
}
