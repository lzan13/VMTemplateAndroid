package com.vmloft.develop.app.match.ui.sign;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.bean.UserBean;
import com.vmloft.develop.app.match.common.ASMSManager;

import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.tools.utils.VMStr;

import butterknife.BindView;
import butterknife.OnClick;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

/**
 * Create by lzan13 on 2019/05/09
 *
 * 注册界面
 */
public class SignInActivity extends AppActivity {

    // 输入框
    @BindView(R.id.edit_username) EditText mUsernameView;
    @BindView(R.id.edit_password) EditText mPasswordView;
    @BindView(R.id.sign_in_btn) Button mSignInBtn;

    private String mUsername;
    private String mPassword;

    @Override
    protected void onResume() {
        super.onResume();

        // 读取最后一次登录的账户 Username
        UserBean user = ASignManager.getInstance().getPrevUser();
        if (user != null && VMStr.isEmpty(mUsername)) {
            mUsernameView.setText(user.getUsername());
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_sign_in;
    }

    @Override
    protected void initUI() {
        super.initUI();
        /**
         * 设置输入框监听
         */
        mUsernameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                verifyInputBox();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                verifyInputBox();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void initData() {

    }

    /**
     * 界面内控件的点击事件监听器
     */
    @OnClick({ R.id.sign_in_btn, R.id.sign_up_go_btn })
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.sign_in_btn:
            loginByEmail();
            break;
        case R.id.sign_up_go_btn:
            ARouter.goSignUp(mActivity);
            break;
        }
    }

    /**
     * 校验输入框内容
     */
    private void verifyInputBox() {
        // 将用户名转为消息并修剪
        mUsername = mUsernameView.getText().toString().toLowerCase().trim();
        mPassword = mPasswordView.getText().toString().trim();

        // 检查输入框是否为空是否为空
        if (VMStr.isEmpty(mPassword) || VMStr.isEmpty(mUsername)) {
            mSignInBtn.setEnabled(false);
            mSignInBtn.setAlpha(0.6f);
        } else {
            mSignInBtn.setEnabled(true);
            mSignInBtn.setAlpha(1.0f);
        }
    }

    /**
     * 通过邮箱登录
     */
    private void loginByEmail() {
        ASignManager.getInstance().signInByEmail(mUsername, mPassword, new ACallback<UserBean>() {
            @Override
            public void onSuccess(UserBean user) {
                // 注册成功保存下用户信息，方便回到登录页面输入信息
                ASignManager.getInstance().setPrevUser(user);
                ASignManager.getInstance().setCurrUser(user);
                ARouter.goMain(mActivity);
            }

            @Override
            public void onError(int code, String desc) {
                VMToast.make(mActivity, desc).error();
            }
        });
    }

    /**
     * 请求验证码
     */
    private void getVerificationCode() {
        ASMSManager.getInstance().getVerificationCode("86", "15617021612", new ACallback() {

            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(int code, String desc) {
            }
        });
    }
}
