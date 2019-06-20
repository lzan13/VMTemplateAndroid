package com.vmloft.develop.app.match.ui.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.AppFragmentPagerAdapter;
import com.vmloft.develop.app.match.common.APermissionManager;
import com.vmloft.develop.app.match.common.ASPManager;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.common.AUMSManager;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.app.match.ui.main.chat.ConversationFragment;
import com.vmloft.develop.app.match.ui.main.home.HomeFragment;
import com.vmloft.develop.app.match.ui.main.me.MeFragment;

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
    private List<View> mCustomTab = new ArrayList<>();
    private int[] mCustomTabIcons = { R.drawable.ic_explore_selector, R.drawable.ic_chat_selector, R.drawable.ic_mine_selector };
    private int[] mCustomTabNames = { R.string.match, R.string.chat, R.string.mine };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否需要跳转到引导界面
        if (ASPManager.getInstance().isShowGuide()) {
            ARouter.goGuide(mActivity);
            return;
        }
        if (ASignManager.getInstance().isSingIn()) {
            // 已经登录，打开 App 时拉取以下联系人信息
            AUMSManager.getInstance().loadAccountList();
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
        mFragmentList.add(ConversationFragment.newInstance());
        mFragmentList.add(MeFragment.newInstance());

        mAdapter = new AppFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCustomTab.get(tab.getPosition()).setSelected(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mCustomTab.get(tab.getPosition()).setSelected(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(createTab(i));
            }
        }
    }

    @Override
    protected void initData() {

    }

    public View createTab(int position) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.widget_custom_tab_item, null);
        ImageView iconView = view.findViewById(R.id.custom_tab_item_icon_iv);
        TextView nameView = view.findViewById(R.id.custom_tab_item_name_tv);
        iconView.setImageResource(mCustomTabIcons[position]);
        nameView.setText(mCustomTabNames[position]);
        nameView.setVisibility(View.VISIBLE);
        mCustomTab.add(iconView);
        return view;
    }

    /**
     * 重写返回按键会退到桌面，并不结束 app
     */
    @Override
    public void onBackPressed() {
        ARouter.goLauncher(mActivity);
    }
}
