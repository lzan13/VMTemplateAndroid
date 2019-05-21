package com.vmloft.develop.library.im.conversation;

import android.support.v7.widget.RecyclerView;

import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseFragment;

import butterknife.BindView;

/**
 * Create by lzan13 on 2019/5/9 10:34
 *
 * IM 最近会话界面，可加载到自己的容器
 */
public class IMConversationFragment extends IMBaseFragment {

    @BindView(R.id.im_conversation_recycler_view)
    RecyclerView mRecyclerView;




    @Override
    protected int layoutId() {
        return R.layout.im_fragment_conversation;
    }

    @Override
    protected void init() {
    }
}
