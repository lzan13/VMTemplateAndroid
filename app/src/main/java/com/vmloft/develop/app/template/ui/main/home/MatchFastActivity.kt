package com.vmloft.develop.app.template.ui.main.home

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.CacheManager
import com.vmloft.develop.app.template.databinding.ActivityMatchFastBinding
import com.vmloft.develop.app.template.im.IMManager
import com.vmloft.develop.app.template.request.bean.Match
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.tools.animator.VMAnimator
import com.vmloft.develop.library.tools.utils.VMSystem
import kotlinx.android.synthetic.main.activity_match.*

import kotlinx.android.synthetic.main.activity_match_fast.*
import kotlinx.android.synthetic.main.activity_match_fast.matchAnimView1
import kotlinx.android.synthetic.main.activity_match_fast.matchAnimView2

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2019/5/15 23:13
 *
 * 匹配快速聊天界面
 */
@Route(path = AppRouter.appMatchFast)
class MatchFastActivity : BVMActivity<MatchViewModel>() {

    private var mAnimatorWrapRadar: VMAnimator.AnimatorSetWrap? = null
    private var mAnimatorWrap: VMAnimator.AnimatorSetWrap? = null
    private var mAnimatorWrap2: VMAnimator.AnimatorSetWrap? = null

    override fun initVM(): MatchViewModel = getViewModel()

    override fun layoutId() = R.layout.activity_match_fast

    override fun initUI() {
        super.initUI()
        CUtils.setDarkMode(mActivity, false)

        setTopTitle(R.string.match_fast)
        setTopTitleColor(R.color.app_title_display)

        (mBinding as ActivityMatchFastBinding).viewModel = mViewModel

        matchStartBtn.setOnClickListener {
            startAnim()
            mViewModel.getMatchOne()
        }
    }

    override fun initData() {
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "matchOne") {
            val match = model.data as Match
            CacheManager.putUser(match.user)
            IMManager.goChatFast(match.user.id)
            finish()
        }
    }

    /**
     * 开始匹配，需要经自己的信息提交到后端
     */
    private fun startAnim() {
        val radarOptions = VMAnimator.AnimOptions(matchAnimView1, floatArrayOf(-45f, 45f), VMAnimator.rotation, 1000)
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