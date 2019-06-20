package com.vmloft.develop.app.match.ui.setting;

import android.content.DialogInterface;
import android.view.View;
import butterknife.OnClick;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.im.chat.IMChatManager;
import com.vmloft.develop.library.im.utils.IMDialog;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Create by lzan13 on 2019/05/14
 *
 * 设置界面
 */
public class SettingActivity extends AppActivity {

    @Override
    protected int layoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    @Override
    protected void initData() {
        setTopTitle(R.string.setting);
    }

    @OnClick({ R.id.setting_me_info, R.id.setting_notify, R.id.setting_chat, R.id.setting_about, R.id.setting_sign_out })
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.setting_me_info:
            ARouter.goMeInfo(mActivity);
            break;
        case R.id.setting_notify:
            ARouter.goNotifySetting(mActivity);
            break;
        case R.id.setting_chat:
            ARouter.goChatSetting(mActivity);
            break;
        case R.id.setting_about:
            ARouter.goAboutSetting(mActivity);
            break;
        case R.id.setting_sign_out:
            signOut();
            break;
        }
    }

    /**
     * 退出登录
     */
    private void signOut() {
        String title = VMStr.byRes(R.string.sign_out_hint_title);
        String content = VMStr.byRes(R.string.sign_out_hint_content);
        String cancel = VMStr.byRes(R.string.im_cancel);
        String ok = VMStr.byRes(R.string.im_ok);
        IMDialog.showAlertDialog(mActivity, title, content, cancel, ok, (DialogInterface dialog, int which) -> {
            ASignManager.getInstance().signOut();
            onFinish();
        });
    }
}
