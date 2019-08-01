package com.vmloft.develop.library.im.chat;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseActivity;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.router.IMRouter;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Create on lzan13 on 2019/05/09 10:10
 *
 * IM 默认提供的聊天界面，可直接打开使用
 */
public class IMChatActivity extends IMBaseActivity {

    private IMChatFragment mChatFragment;
    private String mId;
    private int mChatType;
    private IMContact mContact;

    @Override
    protected int layoutId() {
        return R.layout.im_activity_chat;
    }

    @Override
    protected void initUI() {
        super.initUI();
        mTopSpaceView.getLayoutParams().height = 0;
    }

    @Override
    protected void initData() {
        mId = getIntent().getStringExtra(IMConstants.IM_CHAT_ID);
        mChatType = getIntent().getIntExtra(IMConstants.IM_CHAT_TYPE, IMConstants.ChatType.IM_SINGLE_CHAT);

        mContact = IM.getInstance().getIMContact(mId);

        refreshUI();

        initChatFragment();
    }

    /**
     * 加载聊天界面
     */
    private void initChatFragment() {
        mChatFragment = IMChatFragment.newInstance(mId, mChatType);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.im_chat_container, mChatFragment);
        ft.commit();
    }

    /**
     * 刷新 UI
     */
    private void refreshUI() {
        if (VMStr.isEmpty(mContact.mNickname)) {
            getTopBar().setTitle(mContact.mUsername);
        } else {
            getTopBar().setTitle(mContact.mNickname);
        }
    }

    /**
     * 重写父类的onNewIntent方法，防止打开两个聊天界面
     *
     * @param intent 带有参数的intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        String id = intent.getStringExtra(IMConstants.IM_CHAT_ID);
        // 判断 intent 携带的数据是否是当前聊天对象
        if (this.mId.equals(id)) {
            super.onNewIntent(intent);
        } else {
            onFinish();
            IMRouter.goIMChat(mActivity, mId);
        }
    }
}
