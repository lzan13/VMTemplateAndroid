package com.vmloft.develop.app.match.ui.user;

import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.common.AUMSManager;
import com.vmloft.develop.app.match.glide.ALoader;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.tools.router.VMParams;

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

    private String mId;
    private AUser mUser;

    @Override
    protected int layoutId() {
        return R.layout.activity_user_detail;
    }

    @Override
    protected void initUI() {
        super.initUI();
        getTopBar().setTitleColor(R.color.app_title_light);
    }

    @Override
    protected void initData() {
        VMParams params = ARouter.getParams(mActivity);
        mId = params.str0;
        mUser = AUMSManager.getInstance().getUser(mId);

        loadUserInfo();
    }

    /**
     * 加载用户信息
     */
    private void loadUserInfo() {
        String url = "";
        if (mUser.getAvatar() != null) {
            url = mUser.getAvatar().getUrl();
        }
        ALoader.loadBlur(mActivity, url, mCoverView);
        ALoader.loadAvatar(mActivity, url, mAvatarView);

        mNameView.setText(mUser.getNickname());
        mSignatureView.setText(mUser.getSignature());
    }
}
