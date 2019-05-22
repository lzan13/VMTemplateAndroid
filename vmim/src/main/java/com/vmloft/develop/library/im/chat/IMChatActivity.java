package com.vmloft.develop.library.im.chat;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseActivity;
import com.vmloft.develop.library.im.common.IMConstants;

/**
 * Create on lzan13 on 2019/05/09 10:10
 *
 * IM 默认提供的聊天界面，可直接打开使用
 */
public class IMChatActivity extends IMBaseActivity {

    private String mId;

    private IMChatFragment mChatFragment;

    @Override
    protected int layoutId() {
        return R.layout.im_activity_chat;
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    @Override
    protected void initData() {
        mId = getIntent().getStringExtra(IMConstants.IM_CHAT_KEY_ID);

        mChatFragment = IMChatFragment.newInstance(mId);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.im_chat_container, mChatFragment);
        ft.commit();
    }
}
