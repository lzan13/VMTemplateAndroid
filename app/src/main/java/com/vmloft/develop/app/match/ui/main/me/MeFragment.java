package com.vmloft.develop.app.match.ui.main.me;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.OnClick;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppLazyFragment;

import butterknife.BindView;

import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.glide.ALoader;
import com.vmloft.develop.app.match.im.AIMManager;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.utils.VMTheme;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

/**
 * Create by lzan13 on 2019/04/12
 *
 * 我的界面
 */
public class MeFragment extends AppLazyFragment {

    @BindView(R.id.me_info_layout) View mInfoContainer;
    @BindView(R.id.me_avatar_img) ImageView mAvatarView;
    @BindView(R.id.me_name_tv) TextView mNameView;
    @BindView(R.id.me_signature_tv) TextView mSignatureView;

    private AUser mUser;

    /**
     * Fragment 的工厂方法，方便创建并设置参数
     */
    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();

        Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = ASignManager.getInstance().getCurrentUser();
        refreshUI();
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initData() {
        VMTheme.changeShadow(mInfoContainer);
        refreshUI();
    }

    @OnClick({ R.id.me_info_layout, R.id.me_collect, R.id.me_setting })
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.me_info_layout:
            ARouter.goMeInfo(getActivity());
            break;
        case R.id.me_friend_layout:
            break;
        case R.id.me_follows_layout:
            break;
        case R.id.me_fans_layout:
            break;
        case R.id.me_collect:
            VMToast.make(getActivity(), "暂未实现").error();
            break;
        case R.id.me_setting:
            ARouter.goSetting(getActivity());
            break;
        }
    }

    /**
     * 刷新 UI
     */
    private void refreshUI() {
        if (mUser == null) {
            return;
        }

        mNameView.setText(mUser.getNickname());
        mSignatureView.setText(mUser.getSignature());

        String url = mUser.getAvatar() != null ? mUser.getAvatar().getUrl() : null;
        // 加载头像
        IPictureLoader.Options options = new IPictureLoader.Options(url);
        if (AIMManager.getInstance().isCircleAvatar()) {
            options.isCircle = true;
        } else {
            options.isRadius = true;
            options.radiusSize = VMDimen.dp2px(4);
        }
        ALoader.load(mContext, options, mAvatarView);
    }
}
