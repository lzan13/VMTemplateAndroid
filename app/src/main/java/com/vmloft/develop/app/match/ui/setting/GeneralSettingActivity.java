package com.vmloft.develop.app.match.ui.setting;

import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.common.ASPManager;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.library.tools.widget.VMLineView;

/**
 * Create by lzan13 on 2019/05/14
 *
 * 设置界面
 */
public class GeneralSettingActivity extends AppActivity {
    @BindView(R.id.general_setting_avatar) VMLineView mAvatarLine;

    @Override
    protected int layoutId() {
        return R.layout.activity_setting_general;
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    @Override
    protected void initData() {
        setTopTitle(R.string.setting);
        mAvatarLine.setActivated(ASPManager.getInstance().getCirclerAvatar());
    }

    @OnClick({ R.id.general_setting_avatar })
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.general_setting_avatar:
            mAvatarLine.setActivated(!mAvatarLine.isActivated());
            ASPManager.getInstance().putCirclerAvatar(mAvatarLine.isActivated());
            break;
        }
    }

}
