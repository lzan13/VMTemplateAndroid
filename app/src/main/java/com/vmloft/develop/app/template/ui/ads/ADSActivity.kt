package com.vmloft.develop.app.template.ui.ads


import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.databinding.ActivityAdsBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BActivity


/**
 * Create by lzan13 on 2022/03/23 15:56
 * 描述：广告界面
 */
@Route(path = AppRouter.appADS)
class ADSActivity : BActivity<ActivityAdsBinding>() {

    override fun initVB() = ActivityAdsBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

    }

    override fun initData() {
    }

}