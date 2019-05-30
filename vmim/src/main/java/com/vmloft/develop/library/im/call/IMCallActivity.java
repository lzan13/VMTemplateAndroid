package com.vmloft.develop.library.im.call;

import android.content.Intent;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseActivity;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.router.IMRouter;

/**
 * Create by lzan13 on 2019/5/9 10:46
 *
 * IM 实时音视频通话界面
 */
public class IMCallActivity extends IMBaseActivity {

    // 对方 Id
    private String mId;
    // 是否别人呼叫来的
    private boolean isCall;

    @Override
    protected int layoutId() {
        return R.layout.im_activity_call;
    }

    @Override
    protected void initData() {
        mId = getIntent().getStringExtra(IMConstants.IM_CHAT_ID);
        isCall = getIntent().getBooleanExtra(IMConstants.IM_CHAT_IS_CALL, false);
    }

    /**
     * 开始呼叫
     */
    private void startCall() {
        IMCallManager.getInstance().callSingle(mId, new IMCallback() {
            @Override
            public void onError(int code, String desc) {
                onFinish();
            }
        });
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
            IMRouter.goIMCall(mActivity, mId, isCall);
        }
    }
}
