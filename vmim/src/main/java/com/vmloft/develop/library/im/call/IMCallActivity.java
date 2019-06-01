package com.vmloft.develop.library.im.call;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseActivity;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.router.IMRouter;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.utils.VMDimen;

/**
 * Create by lzan13 on 2019/5/9 10:46
 *
 * IM 实时音视频通话界面
 */
public class IMCallActivity extends IMBaseActivity {

    private ImageView mCoverView;
    private ImageView mAvatarView;
    private TextView mNameView;
    private ImageView mSelfAvatarView;
    private TextView mSelfNameView;
    private TextView mTimeView;
    private ImageButton mAnswerBtn;
    private ImageButton mEndBtn;

    // 对方 Id
    private String mId;
    // 是否别人呼叫来的
    private boolean isCall;
    // 当前联系人信息
    private IMContact mContact;
    private IMContact mSelfContact;

    @Override
    protected int layoutId() {
        return R.layout.im_activity_call;
    }

    @Override
    protected void initUI() {
        super.initUI();
        getTopBar().setTitleColor(R.color.vm_white);

        mCoverView = findViewById(R.id.im_call_cover_iv);
        mAvatarView = findViewById(R.id.im_call_avatar_iv);
        mNameView = findViewById(R.id.im_call_name_tv);
        mSelfAvatarView = findViewById(R.id.im_call_self_avatar_iv);
        mSelfNameView = findViewById(R.id.im_call_self_name_tv);
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

        mContact = IM.getInstance().getIMContact(mId);
        mSelfContact = IM.getInstance().getIMSelfContact();

        setupContact();

        // 装载通话
        setupCall();
    }

    /**
     * 装载联系人信息
     */
    private void setupContact() {
        IPictureLoader.Options options = new IPictureLoader.Options(mContact.mAvatar);
        options.isBlur = true;
        IM.getInstance().getPictureLoader().load(mActivity, options, mCoverView);

        options = new IPictureLoader.Options(mContact.mAvatar);
        options.isCircle = true;
        IM.getInstance().getPictureLoader().load(mActivity, options, mAvatarView);
        mNameView.setText(mContact.mNickname);

        options = new IPictureLoader.Options(mSelfContact.mAvatar);
        IM.getInstance().getPictureLoader().load(mActivity, options, mSelfAvatarView);
        mSelfNameView.setText(mSelfContact.mNickname);
    }

    private View.OnClickListener viewListener = (View v) -> {
        if (v.getId() == R.id.im_call_answer_btn) {
            callAnswer();
        } else if (v.getId() == R.id.im_call_end_btn) {
            callEnd();
        }
    };

    /**
     * 开始呼叫
     */
    private void setupCall() {
        // 如果不是别人呼叫进来的，就主动发起呼叫
        if (!isCall) {
            mAnswerBtn.setVisibility(View.GONE);
            IMCallManager.getInstance().callSingle(mId, new IMCallback() {
                @Override
                public void onError(int code, String desc) {
                    onFinish();
                }
            });
        } else {
            mAnswerBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 通话接听
     */
    private void callAnswer() {
        IMCallManager.getInstance().callAnswer(mId, new IMCallback() {
            @Override
            public void onError(int code, String desc) {
                // TODO 接听失败，退出
                onFinish();
            }
        });
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

    @Override
    public void onBackPressed() {
        callEnd();
    }
}
