package com.vmloft.develop.app.match.ui.setting;

import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.im.AIMManager;
import com.vmloft.develop.library.tools.widget.VMLineView;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

/**
 * Create by lzan13 on 2019/05/14
 *
 * 设置关于界面
 */
public class AboutSettingActivity extends AppActivity {

    @Override
    protected int layoutId() {
        return R.layout.activity_setting_about;
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    @Override
    protected void initData() {
        setTopTitle(R.string.about);
    }

    @OnClick({ R.id.about_setting_check_new })
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.about_setting_check_new:
            VMToast.make(mActivity, "已是最新版本").done();
            break;
        }
    }
}
