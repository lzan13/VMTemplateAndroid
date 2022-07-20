package com.vmloft.develop.app.template.ui.guide

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SPManager
import com.vmloft.develop.app.template.databinding.ActivityGuideBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.tools.adapter.VMFragmentPagerAdapter


/**
 * Create by lzan13 on 2020/05/02 15:56
 * 描述：引导界面
 */
@Route(path = AppRouter.appGuide)
class GuideActivity: BActivity<ActivityGuideBinding>() {

    private var mCurrentIndex = 0
    private var mFragmentList: MutableList<Fragment> = mutableListOf()
    private var mAdapter: VMFragmentPagerAdapter? = null


    override fun initVB() = ActivityGuideBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

//        mBinding.guidePrevBtn.setOnClickListener {
//            mCurrentIndex -= 1
//            mBinding.guideViewPager.setCurrentItem(mCurrentIndex, true)
//        }
//        mBinding.guideNextBtn.setOnClickListener {
//            mCurrentIndex += 1
//            mBinding.guideViewPager.setCurrentItem(mCurrentIndex, true)
//        }
        mBinding.guideEnterBtn.setOnClickListener {
            SPManager.setGuideHide()
            CRouter.goMain()
            finish()
        }
    }

    override fun initData() {
        mFragmentList.add(GuideFragment.newInstance(R.drawable.img_guide_0, R.string.guide_title_0, R.string.guide_intro_0))
        mFragmentList.add(GuideFragment.newInstance(R.drawable.img_guide_1, R.string.guide_title_1, R.string.guide_intro_1))
        mFragmentList.add(GuideFragment.newInstance(R.drawable.img_guide_2, R.string.guide_title_2, R.string.guide_intro_2))
        mFragmentList.add(GuideFragment.newInstance(R.drawable.img_guide_3, R.string.guide_title_3, R.string.guide_intro_3))
        mFragmentList.add(GuideFragment.newInstance(R.drawable.img_guide_4, R.string.guide_title_4, R.string.guide_intro_4))

        mAdapter = VMFragmentPagerAdapter(supportFragmentManager, mFragmentList)

        mBinding.guideViewPager.offscreenPageLimit = mFragmentList.size - 1
        mBinding.guideViewPager.adapter = mAdapter
        mBinding.guideIndicatorView.setViewPager(mBinding.guideViewPager)
        /**
         * 通过监听 ViewPager 页面滑动渐变调整 ViewPager 的背景
         */
        mBinding.guideViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                mCurrentIndex = position
                mBinding.guideEnterBtn.visibility = if (position == mFragmentList.size - 1) View.VISIBLE else View.GONE
//                mBinding.guidePrevBtn.visibility = if (position == 0) View.GONE else View.VISIBLE
//                mBinding.guideNextBtn.visibility = if (position == mFragmentList.size - 1) View.GONE else View.VISIBLE
//                mBinding.guideFinishBtn.visibility = if (position == mFragmentList.size - 1) View.VISIBLE else View.GONE
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

}