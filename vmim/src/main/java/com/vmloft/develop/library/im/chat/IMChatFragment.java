package com.vmloft.develop.library.im.chat;

import android.os.Bundle;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseFragment;
import com.vmloft.develop.library.im.common.IMConstants;

/**
 * Create by lzan13 on 2019/05/09 10:11
 *
 * IM 可自定义加载的聊天界面
 */
public class IMChatFragment extends IMBaseFragment {

    /**
     * Fragment 的工厂方法，方便创建并设置参数
     */
    public static IMChatFragment newInstance(String id) {
        IMChatFragment fragment = new IMChatFragment();

        Bundle args = new Bundle();
        args.putString(IMConstants.IM_CHAT_KEY_ID, id);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.im_fragment_chat;
    }

    @Override
    protected void init() {

    }
}
