package com.vmloft.develop.app.match.ui.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppFragmentPagerAdapter;
import com.vmloft.develop.app.match.common.APermissionManager;
import com.vmloft.develop.app.match.common.ASPManager;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.app.match.ui.main.home.HomeFragment;
import com.vmloft.develop.app.match.ui.main.me.MeFragment;

import com.vmloft.develop.library.im.conversation.IMConversationFragment;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Create by lzan13 on 2019/04/08
 *
 * 项目主界面容器
 */
public class MainActivity extends AppActivity {

    @BindView(R.id.main_view_pager) ViewPager mViewPager;
    @BindView(R.id.main_tab_layout) TabLayout mTabLayout;

    private AppFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否需要跳转到引导界面
        if (ASPManager.getInstance().isShowGuide()) {
            ARouter.goGuide(mActivity);
            return;
        }
        APermissionManager.getInstance().requestPermissions(mActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ASignManager.getInstance().isSingIn()) {
            ARouter.goSignIn(mActivity);
            return;
        }
    }

    @Override
    protected int layoutId() {
        // 加载 UI 前先设置一下正常的主题
        setTheme(R.style.AppTheme);
        return R.layout.activity_main;
    }

    @Override
    protected void initUI() {
        super.initUI();

        mFragmentList = new ArrayList<>();
        mFragmentList.add(HomeFragment.newInstance());
        mFragmentList.add(IMConversationFragment.newInstance());
        mFragmentList.add(MeFragment.newInstance());

        mAdapter = new AppFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void initData() {

    }

    /**
     * 重写返回按键会退到桌面，并不结束 app
     */
    @Override
    public void onBackPressed() {
        ARouter.goLauncher(mActivity);
    }
}
