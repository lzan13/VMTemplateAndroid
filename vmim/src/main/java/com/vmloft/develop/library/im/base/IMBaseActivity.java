package com.vmloft.develop.library.im.base;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.tools.base.VMBActivity;
import com.vmloft.develop.library.tools.utils.VMTheme;
import com.vmloft.develop.library.tools.widget.VMTopBar;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/9 10:15
 *
 * IM Activity 基类
 */
public abstract class IMBaseActivity extends VMBActivity {

    // 统一的 TopBar
    protected VMTopBar mTopBar;

    @Override
    protected void initUI() {
        // 设置深色状态栏
        VMTheme.setDarkStatusBar(mActivity, true);

        setupTopBar();
    }

    /**
     * 装载 TopBar
     */
    protected void setupTopBar() {
        mTopBar = findViewById(R.id.common_top_bar);
        if (mTopBar != null) {
            // 设置状态栏透明主题时，布局整体会上移，所以给头部加上状态栏的 margin 值，保证头部不会被覆盖
            //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTopBar.getLayoutParams();
            //params.topMargin = VMDimen.getStatusBarHeight();
            //mTopBar.setLayoutParams(params);

            mTopBar.setIconListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    /**
     * 通用的获取 TopBar 方法
     */
    protected VMTopBar getTopBar() {
        return mTopBar;
    }

    /**
     * 设置标题
     */
    protected void setTopTitle(int resId) {
        if (mTopBar == null) {
            return;
        }
        mTopBar.setTitle(resId);
    }

    /**
     * 设置标题
     */
    protected void setTopTitle(String title) {
        if (mTopBar == null) {
            return;
        }
        mTopBar.setTitle(title);
    }

    /**
     * 解决Fragment中的onActivityResult()方法无响应问题。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 1.使用 getSupportFragmentManager().getFragments() 获取到当前 Activity 中添加的 Fragment 集合
         * 2.遍历 Fragment 集合，手动调用在当前 Activity 中的 Fragment 中的 onActivityResult() 方法。
         */
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment mFragment : fragments) {
                mFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
