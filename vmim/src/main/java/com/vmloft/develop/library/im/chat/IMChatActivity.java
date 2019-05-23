package com.vmloft.develop.library.im.chat;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseActivity;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Create on lzan13 on 2019/05/09 10:10
 *
 * IM 默认提供的聊天界面，可直接打开使用
 */
public class IMChatActivity extends IMBaseActivity {

    private IMChatFragment mChatFragment;
    private String mId;
    private IMContact mContact;

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
        IM.getInstance().getIMContact(mId, new IMCallback<IMContact>() {
            @Override
            public void onSuccess(IMContact contact) {
                mContact = contact;
                refreshUI();
            }
        });

        refreshUI();

        initChatFragment();
    }

    /**
     * 加载聊天界面
     */
    private void initChatFragment() {
        mChatFragment = IMChatFragment.newInstance(mId);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.im_chat_container, mChatFragment);
        ft.commit();
    }

    /**
     * 刷新 UI
     */
    private void refreshUI() {
        if (mContact == null) {
            getTopBar().setTitle(mId);
            return;
        }
        if (VMStr.isEmpty(mContact.mNickname)) {
            getTopBar().setTitle(mContact.mUsername);
        } else {
            getTopBar().setTitle(mContact.mNickname);
        }
    }
}
