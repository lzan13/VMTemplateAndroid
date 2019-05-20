package com.vmloft.develop.app.match.base;


import com.vmloft.develop.library.tools.base.VMFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Create by lzan13 on 2019/04/11
 * 定义项目 Fragment 基类
 */
public abstract class AppFragment extends VMFragment {

    private Unbinder unbinder;

    @Override
    protected void init() {
        unbinder = ButterKnife.bind(this, getView());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
