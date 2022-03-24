package com.vmloft.develop.app.template.ui.trade

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.databinding.ActivityGoldDescBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.router.CRouter

/**
 * Create by lzan13 on 2021/8/11
 * 描述：金币相关界面
 */
@Route(path = AppRouter.appGoldDesc)
class GoldDescActivity: BActivity<ActivityGoldDescBinding>() {

    override fun initVB() = ActivityGoldDescBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.gold_desc)

        mBinding.goFeedbackTV.setOnClickListener { CRouter.go(AppRouter.appFeedback, Constants.FeedbackType.opinion) }
    }

    override fun initData() {

    }


}