package com.vmloft.develop.app.match.ui.setting;

import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.im.AIMManager;
import com.vmloft.develop.library.tools.widget.VMLineView;

/**
 * Create by lzan13 on 2019/05/14
 *
 * 设置界面
 */
public class ChatSettingActivity extends AppActivity {
    @BindView(R.id.chat_setting_avatar) VMLineView mAvatarLine;
    @BindView(R.id.chat_setting_voice) VMLineView mVoiceLine;

    @Override
    protected int layoutId() {
        return R.layout.activity_setting_chat;
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    @Override
    protected void initData() {
        setTopTitle(R.string.chat_settings);
        mAvatarLine.setActivated(AIMManager.getInstance().isCircleAvatar());
        mVoiceLine.setActivated(AIMManager.getInstance().isSpeakerVoice());
    }

    @OnClick({ R.id.chat_setting_avatar, R.id.chat_setting_voice, R.id.chat_setting_background })
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.chat_setting_avatar:
            mAvatarLine.setActivated(!mAvatarLine.isActivated());
            AIMManager.getInstance().setCircleAvatar(mAvatarLine.isActivated());
            break;
        case R.id.chat_setting_voice:
            mVoiceLine.setActivated(!mVoiceLine.isActivated());
            AIMManager.getInstance().setSpeakerVoice(mVoiceLine.isActivated());
            break;
        case R.id.chat_setting_background:
            break;
        }
    }
}
