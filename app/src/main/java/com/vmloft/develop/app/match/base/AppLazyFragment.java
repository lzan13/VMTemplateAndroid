package com.vmloft.develop.app.match.base;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.vmloft.develop.library.tools.base.VMLazyFragment;

/**
 * Create by lzan13 on 2019/04/11
 *
 * 定义项目 Fragment 懒加载基类
 */
public abstract class AppLazyFragment extends VMLazyFragment {

    private Unbinder unbinder;

    @Override
    protected void initView() {
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
