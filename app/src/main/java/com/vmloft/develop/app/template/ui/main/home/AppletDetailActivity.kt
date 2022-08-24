package com.vmloft.develop.app.template.ui.main.home

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.databinding.ActivityAppletDetailBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.ads.ADSConstants
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.PermissionManager
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.bean.Applet
import com.vmloft.develop.library.data.bean.Attachment
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.viewmodel.AppletViewModel
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.mp.MPManager
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMFile
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2022/5/23 23:13
 * 描述：程序详情界面
 */
@Route(path = AppRouter.appAppletDetail)
class AppletDetailActivity : BVMActivity<ActivityAppletDetailBinding, AppletViewModel>() {

    override var isDarkStatusBar: Boolean = true

    @Autowired(name = CRouter.paramsObj0)
    lateinit var applet: Applet

    private lateinit var user: User

    override fun initVM(): AppletViewModel = getViewModel()

    override fun initVB() = ActivityAppletDetailBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.relaxation_world)

        mBinding.testTV.setOnClickListener { test() }
        mBinding.startTV.setOnClickListener { startVerify() }

        // 视频奖励完成
        LDEventBus.observe(this, Constants.Event.videoReward, String::class.java) {
            startJump()
        }
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        user = SignManager.getSignUser()

        bindInfo()
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        if (model.type == "download") {
            super.onModelLoading(model)
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "download") {
            startMP()
        }
    }

    override fun onModelError(model: BViewModel.UIModel) {
        super.onModelError(model)
    }

    /**
     * 绑定信息展示
     */
    private fun bindInfo() {
        if (applet.cover != null && applet.cover.path.isNotEmpty()) {
            updateCoverRatio(mBinding.coverIV, applet.cover)
            IMGLoader.loadCover(mBinding.coverIV, applet.cover.path)
        }

        mBinding.tipsTV.text = applet.tips

        mBinding.titleTV.text = applet.title
        mBinding.contentTV.text = applet.content

        if (ConfigManager.appConfig.tradeConfig.vipEntry && applet.isNeedVIP) {
            mBinding.vipTV.visibility = View.VISIBLE
            if (user.role.identity < 100) {
                mBinding.testTV.visibility = View.VISIBLE
            }
            mBinding.startTV.text = VMStr.byRes(R.string.relaxation_world_vip_tips)
        } else {
            mBinding.startTV.text = VMStr.byRes(R.string.relaxation_world_start)
        }
    }

    /**
     * 根据图片宽高更新封面比例
     */
    private fun updateCoverRatio(coverView: View, attachment: Attachment) {
        var w = attachment.width
        var h = attachment.height
        if (w == 0 || h == 0) {
            return
        }
        coverView.layoutParams.height = VMDimen.screenWidth * h / w
    }

    /**
     * 试玩
     */
    private fun test() {
        CRouter.go(AppRouter.appRewardVideo, str0 = ADSConstants.ADSIds.videoSceneItemId)
    }

    /**
     * 准备开始，做一下校验
     */
    private fun startVerify() {
        if (ConfigManager.appConfig.tradeConfig.vipEntry && applet.isNeedVIP && user.role.identity < 100) {
            return CRouter.go(AppRouter.appVipTrade)
        }
        startJump()
    }

    /**
     * 开始跳转
     */
    private fun startJump() {
        if (applet.type == 0) {
            CRouter.goWeb("${applet.url}&version=${applet.versionName}")
        } else {
            if (PermissionManager.storagePermission(this)) {
                mViewModel.download(applet)
            }
        }
    }

    /**
     * 启动小程序
     */
    private fun startMP() {
        val filePath = VMFile.filesPath("applet") + applet.body.id + applet.body.extname
        MPManager.unpackMP(this, applet.appId, filePath)
    }
}