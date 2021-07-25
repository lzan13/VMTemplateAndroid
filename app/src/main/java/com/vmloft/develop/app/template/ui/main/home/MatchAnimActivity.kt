package com.vmloft.develop.app.template.ui.main.home

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.CacheManager
import com.vmloft.develop.app.template.databinding.ActivityMatchAnimBinding
import com.vmloft.develop.app.template.im.IMManager
import com.vmloft.develop.app.template.request.bean.Match
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.tools.animator.VMAnimator
import com.vmloft.develop.library.tools.utils.VMSystem

import kotlinx.android.synthetic.main.activity_match_anim.*

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2019/5/15 23:13
 * 描述：匹配动画过渡界面
 */
@Route(path = AppRouter.appMatchAnim)
class MatchAnimActivity : BVMActivity<MatchViewModel>() {

    // 匹配方式
    @Autowired
    @JvmField
    var type: Int = 0

    // 过滤匹配性别
    @Autowired
    @JvmField
    var gender: Int = 0

    private var mAnimatorWrapRadar: VMAnimator.AnimatorSetWrap? = null
    private var mAnimatorWrap: VMAnimator.AnimatorSetWrap? = null
    private var mAnimatorWrap2: VMAnimator.AnimatorSetWrap? = null

    override fun initVM(): MatchViewModel = getViewModel()

    override fun layoutId() = R.layout.activity_match_anim

    override fun initUI() {
        super.initUI()
        CUtils.setDarkMode(mActivity, false)

        setTopTitle(R.string.match_loading)
        setTopTitleColor(R.color.app_title_display)

        (mBinding as ActivityMatchAnimBinding).viewModel = mViewModel

        mViewModel.getOneMatch(gender)
    }

    override fun initData() {
        ARouter.getInstance().inject(this)
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        startAnim()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        stopAnim()
        if (model.type == "oneMatch") {
            val match = model.data as Match
            CacheManager.putUser(match.user)

            when (type) {
                0 -> IMManager.goChat(match.user.id)
                2 -> IMManager.goChatFast(match.user.id)
                else -> AppRouter.goUserInfo(match.user)
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
     * 开始匹配，需要经自己的信息提交到后端
     */
    private fun startAnim() {
        val radarOptions = VMAnimator.AnimOptions(matchRadarIV, floatArrayOf(-45f, 45f), VMAnimator.rotation, 1000)
        mAnimatorWrapRadar = VMAnimator.createAnimator().play(radarOptions)
        mAnimatorWrapRadar?.start()

        val scaleXOptions = VMAnimator.AnimOptions(matchAnimView1, floatArrayOf(0f, 20f), VMAnimator.scaleX, 2000, repeatMode = 1)
        val scaleYOptions = VMAnimator.AnimOptions(matchAnimView1, floatArrayOf(0f, 20f), VMAnimator.scaleY, 2000, repeatMode = 1)
        val alphaOptions = VMAnimator.AnimOptions(matchAnimView1, floatArrayOf(1.0f, 0.0f), VMAnimator.alpha, 2000, repeatMode = 1)
        mAnimatorWrap = VMAnimator.createAnimator().play(scaleXOptions).with(scaleYOptions).with(alphaOptions)
        mAnimatorWrap?.start()

        val scaleXOptions2 = VMAnimator.AnimOptions(matchAnimView2, floatArrayOf(0f, 20f), VMAnimator.scaleX, 2000, repeatMode = 1)
        val scaleYOptions2 = VMAnimator.AnimOptions(matchAnimView2, floatArrayOf(0f, 20f), VMAnimator.scaleY, 2000, repeatMode = 1)
        val alphaOptions2 = VMAnimator.AnimOptions(matchAnimView2, floatArrayOf(1.0f, 0.0f), VMAnimator.alpha, 2000, repeatMode = 1)
        mAnimatorWrap2 = VMAnimator.createAnimator().play(scaleXOptions2).with(scaleYOptions2).with(alphaOptions2)
        mAnimatorWrap2?.start(delay = 1000)
    }

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

}