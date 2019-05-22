package com.vmloft.develop.app.match.ui.main.me;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.OnClick;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.common.AUMSManager;
import com.vmloft.develop.app.match.glide.APictureLoader;
import com.vmloft.develop.library.tools.base.VMConstant;
import com.vmloft.develop.library.tools.picker.VMPicker;
import com.vmloft.develop.library.tools.picker.bean.VMPictureBean;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.widget.VMLineView;
import com.vmloft.develop.library.tools.widget.toast.VMToast;
import java.util.ArrayList;

/**
 * Create by lzan13 on 2019/5/12 22:20
 *
 * 修改个人信息界面之签名
 */
public class MeSignatureActivity extends AppActivity {

    @BindView(R.id.me_signature_et) EditText mSignatureET;
    // 个人用户
    private AUser mUser;

    @Override
    protected int layoutId() {
        return R.layout.activity_me_signature;
    }

    @Override
    protected void initUI() {
        super.initUI();
        getTopBar().setEndBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSignature();
            }
        });
    }

    @Override
    protected void initData() {
        setTopTitle(R.string.me_info);
        mUser = ASignManager.getInstance().getCurrentUser();
    }

    /**
     * 保存头像
     */
    public void saveSignature() {
        String signature = mSignatureET.getText().toString().trim();
        mUser.setSignature(signature);
        AUMSManager.getInstance().saveUserInfo(mUser, new ACallback<AUser>() {
            @Override
            public void onSuccess(AUser user) {
                VMLog.d("用户信息保存成功");
                onFinish();
            }

            @Override
            public void onError(int code, String desc) {
                VMLog.e("用户信息保存失败 %d %s", code, desc);
            }
        });
    }
}
