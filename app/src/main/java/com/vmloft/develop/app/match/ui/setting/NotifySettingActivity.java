package com.vmloft.develop.app.match.ui.setting;

import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.common.ASPManager;
import com.vmloft.develop.app.match.im.AIMManager;
import com.vmloft.develop.library.tools.widget.VMLineView;

/**
 * Create by lzan13 on 2019/05/14
 *
 * 设置界面
 */
public class NotifySettingActivity extends AppActivity {
    @BindView(R.id.notify_setting) VMLineView mNotifyLine;
    @BindView(R.id.notify_setting_detail) VMLineView mNotifyDetailLine;

    @Override
    protected int layoutId() {
        return R.layout.activity_setting_notify;
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    @Override
    protected void initData() {
        setTopTitle(R.string.setting);
        mNotifyLine.setActivated(AIMManager.getInstance().isNotify());
        mNotifyDetailLine.setActivated(AIMManager.getInstance().isNotifyDetail());
    }

    @OnClick({ R.id.notify_setting, R.id.notify_setting_detail })
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.notify_setting:
            mNotifyLine.setActivated(!mNotifyLine.isActivated());
            AIMManager.getInstance().setNotify(mNotifyLine.isActivated());
            break;
        case R.id.notify_setting_detail:
            mNotifyDetailLine.setActivated(!mNotifyDetailLine.isActivated());
            AIMManager.getInstance().setNotifyDetail(mNotifyDetailLine.isActivated());
            break;
        }
    }
}
