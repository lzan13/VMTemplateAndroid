package com.vmloft.develop.app.template.ui.main.home

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivityMatchAnimBinding
import com.vmloft.develop.app.template.im.IMManager
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.bean.Match
import com.vmloft.develop.library.data.viewmodel.MatchViewModel
import com.vmloft.develop.library.tools.animator.VMAnimator
import com.vmloft.develop.library.tools.utils.VMSystem

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2019/5/15 23:13
 * 描述：匹配动画过渡界面
 */
@Route(path = AppRouter.appMatchAnim)
class MatchAnimActivity : BVMActivity<ActivityMatchAnimBinding, MatchViewModel>() {

    override var isDarkStatusBar: Boolean = false

    // 匹配方式
    @Autowired(name = CRouter.paramsWhat)
    @JvmField
    var type: Int = 0

    // 过滤匹配性别
    lateinit var selfMatch: Match

    private var mAnimatorWrap: VMAnimator.AnimatorSetWrap? = null
    private var mAnimatorWrap2: VMAnimator.AnimatorSetWrap? = null
    private var mAnimatorWrapRadar: VMAnimator.AnimatorSetWrap? = null

    override fun initVM(): MatchViewModel = getViewModel()

    override fun initVB() = ActivityMatchAnimBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

//        setTopTitle(R.string.match_loading)
//        setTopTitleColor(R.color.app_title_display)

    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        selfMatch = SignManager.getSelfMatch()

        // 获取随机匹配
        mViewModel.randomMatch(selfMatch.filterGender, type)

        if (type == 0) {
            mBinding.matchIconIV.setImageResource(R.drawable.ic_puzzle_line)
        } else {
            mBinding.matchIconIV.setImageResource(R.drawable.ic_paper_crane)
        }
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        if (model.isLoading) {
            startAnim()
        } else {
            stopAnim()
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "randomMatch") {
            val match = model.data as Match
            CacheManager.putUser(match.user)
            when (type) {
                0 -> IMManager.goChat(match.user.id, match.content)
                1 -> IMManager.goChatFast(match.user.id)
                else -> CRouter.go(AppRouter.appUserInfo, obj0 = match.user)
            }
            finish()
        }
    }

    override fun onModelError(model: BViewModel.UIModel) {
        super.onModelError(model)
        model.error?.let {
            VMSystem.runInUIThread({ finish() }, 1000)
        }
    }

    /**
     * 开始动画
     */
    private fun startAnim() {
        val scaleXOptions = VMAnimator.AnimOptions(mBinding.matchAnimView1, floatArrayOf(0f, 10f), VMAnimator.scaleX, 2000, repeatMode = 1)
        val scaleYOptions = VMAnimator.AnimOptions(mBinding.matchAnimView1, floatArrayOf(0f, 10f), VMAnimator.scaleY, 2000, repeatMode = 1)
        val alphaOptions = VMAnimator.AnimOptions(mBinding.matchAnimView1, floatArrayOf(1.0f, 0.0f), VMAnimator.alpha, 2000, repeatMode = 1)
        mAnimatorWrap = VMAnimator.createAnimator().play(scaleXOptions).with(scaleYOptions).with(alphaOptions)
        mAnimatorWrap?.start()

        val scaleXOptions2 = VMAnimator.AnimOptions(mBinding.matchAnimView2, floatArrayOf(0f, 10f), VMAnimator.scaleX, 2000, repeatMode = 1)
        val scaleYOptions2 = VMAnimator.AnimOptions(mBinding.matchAnimView2, floatArrayOf(0f, 10f), VMAnimator.scaleY, 2000, repeatMode = 1)
        val alphaOptions2 = VMAnimator.AnimOptions(mBinding.matchAnimView2, floatArrayOf(1.0f, 0.0f), VMAnimator.alpha, 2000, repeatMode = 1)
        mAnimatorWrap2 = VMAnimator.createAnimator().play(scaleXOptions2).with(scaleYOptions2).with(alphaOptions2)
        mAnimatorWrap2?.start(delay = 1000)

        val radarOptions = VMAnimator.AnimOptions(mBinding.matchIconIV, floatArrayOf(-15f, 15f), VMAnimator.rotation, 2000)
        mAnimatorWrapRadar = VMAnimator.createAnimator().play(radarOptions)
        mAnimatorWrapRadar?.start()

    }

    /**
     * 停止动画
     */
    private fun stopAnim() {
        mAnimatorWrapRadar?.cancel()
        mAnimatorWrap?.cancel()
        mAnimatorWrap2?.cancel()
    }

    override fun onResume() {
        super.onResume()
        mAnimatorWrapRadar?.resume()
        mAnimatorWrap?.resume()
        mAnimatorWrap2?.resume()
    }

    override fun onPause() {
        super.onPause()
        mAnimatorWrapRadar?.pause()
        mAnimatorWrap?.pause()
        mAnimatorWrap2?.pause()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_fade_open_enter, R.anim.activity_fade_close_exit)
    }
}