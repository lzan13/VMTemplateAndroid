package com.vmloft.develop.app.match.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.vmloft.develop.library.tools.adapter.VMFragmentPagerAdapter;

import java.util.List;

/**
 * Create by lzan13 on 2019/04/12
 *
 * 一个通用的 Fragment ViewPager 适配器
 */
public class AppFragmentPagerAdapter extends VMFragmentPagerAdapter {

    public AppFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm, list);
    }
}
