package com.vmloft.develop.app.match.ui.sign;

import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.request.bean.AAccount;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.library.tools.utils.VMReg;
import com.vmloft.develop.library.tools.utils.VMStr;

import butterknife.BindView;
import butterknife.OnClick;

import com.vmloft.develop.library.tools.utils.VMTheme;
import com.vmloft.develop.library.tools.widget.VMEditView;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

/**
 * Create by lzan13 on 2019/05/09
 *
 * 注册界面
 */
public class SignUpActivity extends AppActivity {

    // 输入框
    @BindView(R.id.sign_account_et) VMEditView mAccountView;
    @BindView(R.id.sign_password_et) VMEditView mPasswordView;
    @BindView(R.id.sign_up_btn) Button mSignUpBtn;

    private String mAccount;
    private String mPassword;

    private Dialog mDialog;

    @Override
    protected int layoutId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initUI() {
        super.initUI();

        VMTheme.changeShadow(mAccountView);
        VMTheme.changeShadow(mPasswordView);

        // 监听输入框变化
        mAccountView.addTextChangedListener(new TextWatcher() {
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
    @OnClick({ R.id.sign_up_btn })
    void onClick(View v) {
        switch (v.getId()) {
        case R.id.sign_up_btn:
            registerByEmail();
            break;
        }
    }

    /**
     * 校验输入框内容
     */
    private void verifyInputBox() {
        // 将用户名转为消息并修剪
        mAccount = mAccountView.getText();
        mPassword = mPasswordView.getText();

        // 检查输入框是否为空是否为空
        if (VMStr.isEmpty(mPassword) || VMStr.isEmpty(mAccount)) {
            mSignUpBtn.setEnabled(false);
        } else {
            mSignUpBtn.setEnabled(true);
        }
    }

    /**
     * 通过邮箱注册
     */
    private void registerByEmail() {
        if (!VMReg.isEmail(mAccount)) {
            VMToast.make(mActivity, VMStr.byRes(R.string.account_is_email)).error();
            return;
        }
        showDialog();
        ASignManager.getInstance().signUpByEmail(mAccount, mPassword, new ACallback<AAccount>() {
            @Override
            public void onSuccess(AAccount account) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                VMToast.make(mActivity, R.string.sign_up_success).show();
                // 注册成功保存下用户信息，方便回到登录页面输入信息
                ASignManager.getInstance().setHistoryAccount(account);
                onFinish();
            }

            @Override
            public void onError(int code, String desc) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                VMToast.make(mActivity, desc).error();
            }
        });
    }

    /**
     * 显示对话框
     */
    private void showDialog() {
        mDialog = new Dialog(mActivity);
        mDialog.setContentView(R.layout.widget_custom_progress_dialog);
        mDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.onDestroy();
    }
}
