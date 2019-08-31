package com.vmloft.develop.app.match.ui.user;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.request.bean.AAccount;
import com.vmloft.develop.app.match.common.AUMSManager;
import com.vmloft.develop.app.match.glide.ALoader;
import com.vmloft.develop.app.match.im.AIMManager;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.im.router.IMRouter;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.router.VMParams;
import com.vmloft.develop.library.tools.utils.VMDimen;

/**
 * Create by lzan13 on 2019/6/1 12:44
 *
 * 用户详情界面
 */
public class UserDetailActivity extends AppActivity {

    @BindView(R.id.user_detail_cover_iv) ImageView mCoverView;
    @BindView(R.id.user_detail_avatar_iv) ImageView mAvatarView;
    @BindView(R.id.user_detail_name_tv) TextView mNameView;
    @BindView(R.id.user_detail_signature_tv) TextView mSignatureView;
    @BindView(R.id.user_detail_private_letter_tv) TextView mPrivateLetterView;
    @BindView(R.id.user_detail_follow_tv) TextView mFollowView;

    private String mId;
    private AAccount mAccount;

    @Override
    protected int layoutId() {
        return R.layout.activity_user_detail;
    }

    @Override
    protected void initUI() {
        super.initUI();
        getTopBar().setTitleColor(R.color.app_title_light);
        mPrivateLetterView.setOnClickListener(viewListener);
        mFollowView.setOnClickListener(viewListener);
    }

    @Override
    protected void initData() {
        VMParams params = (VMParams) ARouter.getParams(mActivity);
        mId = params.str0;
        mAccount = AUMSManager.getInstance().getAccount(mId);

        loadUserInfo();
    }

    /**
     * 加载用户信息
     */
    private void loadUserInfo() {
        // 加载头像
        IPictureLoader.Options options = new IPictureLoader.Options(ALoader.wrapUrl(mAccount.getAvatar()));
        if (AIMManager.getInstance().isCircleAvatar()) {
            options.isCircle = true;
        } else {
            options.isRadius = true;
            options.radiusSize = VMDimen.dp2px(4);
        }
        ALoader.load(mActivity, options, mAvatarView);
        // 加载背景
        options = new IPictureLoader.Options(ALoader.wrapUrl(mAccount.getAvatar()));
        options.isBlur = true;
        ALoader.load(mActivity, options, mCoverView);

        mNameView.setText(mAccount.getNickname());
        mSignatureView.setText(mAccount.getSignature());
    }

    private View.OnClickListener viewListener = (View v) -> {
        if (v.getId() == R.id.user_detail_private_letter_tv) {
            IMRouter.goIMChat(mActivity, mId);
        } else if (v.getId() == R.id.user_detail_follow_tv) {
            // TODO 关注对方，如果已关注，取消关注
        }
    };
}
