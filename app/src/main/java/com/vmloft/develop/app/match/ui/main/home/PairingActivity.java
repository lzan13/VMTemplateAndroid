package com.vmloft.develop.app.match.ui.main.home;

import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import butterknife.BindView;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppActivity;

/**
 * Create by lzan13 on 2019/5/15 23:13
 *
 * 匹配界面
 */
public class PairingActivity extends AppActivity {

    @BindView(R.id.home_pairing_anim_view) View mPairingView;
    private boolean isAnim;

    @Override
    protected int layoutId() {
        return R.layout.activity_pairing;
    }

    @Override
    protected void initUI() {
        super.initUI();
        startPairing();
    }

    @Override
    protected void initData() {

    }

    private void startPairing() {
        if (isAnim) {
            mPairingView.clearAnimation();
            isAnim = false;
        } else {
            AnimationSet set = (AnimationSet) AnimationUtils.loadAnimation(mActivity, R.anim.home_pairing_scale_anim);
            mPairingView.startAnimation(set);
            isAnim = true;
        }
    }
}
