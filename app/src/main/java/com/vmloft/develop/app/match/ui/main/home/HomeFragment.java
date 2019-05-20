package com.vmloft.develop.app.match.ui.main.home;

import android.os.Bundle;
import android.view.View;

import butterknife.OnClick;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppLazyFragment;

import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

/**
 * Create by lzan13 on 2019/04/12
 *
 * 主界面
 */
public class HomeFragment extends AppLazyFragment {

    /**
     * Fragment 的工厂方法，方便创建并设置参数
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {

    }

    @OnClick({ R.id.home_pairing_text, R.id.home_pairing_voice })
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.home_pairing_text:
            ARouter.goPairing(getActivity());
            break;
        case R.id.home_pairing_voice:
            VMToast.make(getActivity(), "搜索令你心动的声音").done();
            break;
        }
    }
}
