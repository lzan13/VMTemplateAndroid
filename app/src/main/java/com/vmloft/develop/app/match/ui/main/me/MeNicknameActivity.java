package com.vmloft.develop.app.match.ui.main.me;

import android.view.View;
import android.widget.EditText;

import butterknife.BindView;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.request.bean.AAccount;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.common.AUMSManager;
import com.vmloft.develop.library.im.chat.IMChatManager;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Create by lzan13 on 2019/5/12 22:20
 *
 * 修改个人信息界面之签名
 */
public class MeNicknameActivity extends AppActivity {

    @BindView(R.id.me_nickname_et) EditText mNicknameET;
    // 个人用户
    private AAccount mAccount;

    @Override
    protected int layoutId() {
        return R.layout.activity_me_nickname;
    }

    @Override
    protected void initUI() {
        super.initUI();
        getTopBar().setEndBtnListener(VMStr.byRes(R.string.btn_finish), (View view) -> { saveNickname();});
    }

    @Override
    protected void initData() {
        setTopTitle(R.string.me_info);
        mAccount = ASignManager.getInstance().getCurrentAccount();
        mNicknameET.setText(mAccount.getNickname());
    }

    /**
     * 保存头像
     */
    public void saveNickname() {
        String nickname = mNicknameET.getText().toString().trim();
        mAccount.setNickname(nickname);
        AUMSManager.getInstance().updateAccountDetail(mAccount, new ACallback<AAccount>() {
            @Override
            public void onSuccess(AAccount account) {
                ASignManager.getInstance().setCurrentAccount(account);
                // 发送消息通知其他人更新了信息
                IMChatManager.getInstance().sendContactInfoChange();
                onFinish();
            }

            @Override
            public void onError(int code, String desc) {
                VMLog.e("用户信息保存失败 %d %s", code, desc);
            }
        });
    }
}
