package com.vmloft.develop.library.im.call;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

    private ImageView mAvatarView;
    private TextView mNameView;
    private TextView mTimeView;
    private ImageButton mAnswerBtn;
    private ImageButton mEndBtn;

    // 对方 Id
    private String mId;
    // 是否别人呼叫来的
    private boolean isCall;

    @Override
    protected int layoutId() {
        return R.layout.im_activity_call;
    }

    @Override
    protected void initUI() {
        super.initUI();

        mAvatarView = findViewById(R.id.im_call_avatar_iv);
        mNameView = findViewById(R.id.im_call_name_tv);
        mTimeView = findViewById(R.id.im_call_time_tv);
        mAnswerBtn = findViewById(R.id.im_call_answer_btn);
        mEndBtn = findViewById(R.id.im_call_end_btn);

        mAnswerBtn.setOnClickListener(viewListener);
        mEndBtn.setOnClickListener(viewListener);
    }

    @Override
    protected void initData() {
        mId = getIntent().getStringExtra(IMConstants.IM_CHAT_ID);
        isCall = getIntent().getBooleanExtra(IMConstants.IM_CHAT_IS_CALL, false);
        // 如果不是别人呼叫进来的，就主动发起呼叫
        if (!isCall) {
            startCall();
        }
    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.im_call_answer_btn) {
                callAnswer();
            } else if (v.getId() == R.id.im_call_end_btn) {
                callEnd();
            }
        }
    };

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
     * 通话接听
     */
    private void callAnswer() {
        IMCallManager.getInstance().callAnswer(mId, new IMCallback() {
            @Override
            public void onError(int code, String desc) {
                // TODO 接听失败，退出
            }
        });
        onFinish();
    }

    /**
     * 通话结束
     */
    private void callEnd() {
        IMCallManager.getInstance().callEnd(mId);
        IMCallManager.getInstance().callReject(mId);
        onFinish();
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

    @Override
    public void onFinish() {
        super.onFinish();
    }
}
