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

import kotlinx.android.synthetic.main.activity_match_fast.*

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
            CacheManager.instance.putUser(match.user)
            IMManager.instance.goChatFast(match.user.id)
            finish()
        }
    }

    /**
     * 开始匹配，需要经自己的信息提交到后端
     */
    private fun startAnim() {
        mAnimatorWrapRadar = VMAnimator.createAnimator().play(VMAnimator.createOptions(matchRadarIV, VMAnimator.ROTATION, 1000, VMAnimator.INFINITE, -45f, 45f))
        mAnimatorWrapRadar?.start()

        mAnimatorWrap = VMAnimator.createAnimator()
            .play(VMAnimator.createOptions(matchAnimView1, VMAnimator.SCALEX, 2000, VMAnimator.INFINITE, 0f, 20f))
            .with(VMAnimator.createOptions(matchAnimView1, VMAnimator.SCALEY, 2000, VMAnimator.INFINITE, 0f, 20f))
            .with(VMAnimator.createOptions(matchAnimView1, VMAnimator.ALPHA, 2000, VMAnimator.INFINITE, 1.0f, 0.0f))
        mAnimatorWrap?.start()
        mAnimatorWrap2 = VMAnimator.createAnimator()
            .play(VMAnimator.createOptions(matchAnimView2, VMAnimator.SCALEX, 2000, VMAnimator.INFINITE, 0f, 20f))
            .with(VMAnimator.createOptions(matchAnimView2, VMAnimator.SCALEY, 2000, VMAnimator.INFINITE, 0f, 20f))
            .with(VMAnimator.createOptions(matchAnimView2, VMAnimator.ALPHA, 2000, VMAnimator.INFINITE, 1.0f, 0.0f))
        mAnimatorWrap2?.startDelay(1000)
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