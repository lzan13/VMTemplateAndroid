package com.vmloft.develop.app.match.ui.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
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
        setTopTitle(R.string.notify);
        mNotifyLine.setActivated(AIMManager.getInstance().isNotify());
        mNotifyDetailLine.setActivated(AIMManager.getInstance().isNotifyDetail());
    }

    @OnClick({ R.id.notify_setting, R.id.notify_setting_detail, R.id.notify_setting_system })
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
        case R.id.notify_setting_system:
            openSystemNotify();
            break;
        }
    }

    /**
     * 打开系统设置通知界面
     */
    private void openSystemNotify() {
        String packageName = mActivity.getPackageName();
        int uid = mActivity.getApplicationInfo().uid;
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName);
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, uid);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", packageName);
            intent.putExtra("app_uid", uid);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + packageName));
        } else {
            intent.setAction(Settings.ACTION_SETTINGS);
        }
        startActivity(intent);
    }
}
