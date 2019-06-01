package com.vmloft.develop.app.match.ui.main.me;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.common.AUMSManager;
import com.vmloft.develop.library.tools.base.VMConstant;
import com.vmloft.develop.library.tools.picker.VMPicker;
import com.vmloft.develop.library.tools.picker.bean.VMPictureBean;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.widget.VMLineView;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/12 22:20
 *
 * 个人信息界面
 */
public class MeInfoActivity extends AppActivity {

    @BindView(R.id.me_avatar_line) VMLineView mAvatarLine;
    @BindView(R.id.me_nickname_line) VMLineView mNicknameLine;
    @BindView(R.id.me_username_line) VMLineView mUsernameLine;
    @BindView(R.id.me_qr_code) VMLineView mQRCodeLine;
    @BindView(R.id.me_signature_line) VMLineView mSignatureLine;
    @BindView(R.id.me_gender_line) VMLineView mGenderLine;
    @BindView(R.id.me_birthday) VMLineView mBirthdayLine;
    @BindView(R.id.me_address_line) VMLineView mAddressLine;
    // 个人用户
    private AUser mUser;

    @Override
    protected int layoutId() {
        return R.layout.activity_me_info;
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    @Override
    protected void initData() {
        setTopTitle(R.string.me_info);

        mUser = ASignManager.getInstance().getCurrentUser();

        refreshUI();
    }

    @OnClick({
        R.id.me_avatar_line, R.id.me_nickname_line, R.id.me_qr_code, R.id.me_signature_line, R.id.me_gender_line, R.id.me_birthday,
        R.id.me_address_line
    })
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.me_avatar_line:
            startPickAvatar();
            break;
        case R.id.me_nickname_line:
            startActivity(new Intent(mActivity, MeNicknameActivity.class));
            break;
        case R.id.me_qr_code:
            break;
        case R.id.me_signature_line:
            startActivity(new Intent(mActivity, MeSignatureActivity.class));
            break;
        case R.id.me_gender_line:
            break;
        case R.id.me_birthday:
            break;
        case R.id.me_address_line:
            break;
        }
    }

    /**
     * 开启选择头像
     */
    private void startPickAvatar() {
        VMPicker.getInstance().setMultiMode(false).setShowCamera(true).startPicker(mActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == VMConstant.VM_PICK_RESULT_CODE_PICTURES) {
            if (data != null && requestCode == VMConstant.VM_PICK_REQUEST_CODE) {
                List<VMPictureBean> pictures = VMPicker.getInstance().getResultData();
                saveAvatar(pictures.get(0));
            } else {
                VMToast.make(mActivity, "没有数据").error();
            }
        }
    }

    /**
     * 保存头像
     */
    public void saveAvatar(VMPictureBean bean) {
        AUMSManager.getInstance().saveAvatar(bean, new ACallback<AUser>() {
            @Override
            public void onSuccess(AUser user) {
                VMToast.make(mActivity, "头像设置成功").done();
            }

            @Override
            public void onError(int code, String desc) {
                VMToast.make(mActivity, "头像设置失败 %d %s", code, desc).done();
            }
        });
    }

    /**
     * 刷新 UI
     */
    private void refreshUI() {
        if (mUser == null) {
            return;
        }

        mNicknameLine.setCaption(mUser.getNickname());
        mUsernameLine.setCaption(mUser.getUsername());
        mSignatureLine.setCaption(mUser.getSignature());

        if (mUser.getGender() == 0) {
            mGenderLine.setCaption(VMStr.byRes(R.string.me_gender_unknown));
        } else if (mUser.getGender() == 1) {
            mGenderLine.setCaption(VMStr.byRes(R.string.me_gender_man));
        } else if (mUser.getGender() == 2) {
            mGenderLine.setCaption(VMStr.byRes(R.string.me_gender_woman));
        }
        //mBirthdayLine.setCaption();
        mAddressLine.setCaption(mUser.getAddress());
    }
}
