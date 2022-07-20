package com.vmloft.develop.library.gift

import android.view.View

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.opensource.svgaplayer.SVGACallback

import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity

import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.PermissionManager
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.data.bean.Gift
import com.vmloft.develop.library.data.viewmodel.GiftViewModel
import com.vmloft.develop.library.gift.databinding.ActivityGiftAnimBinding
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.request.RConstants
import com.vmloft.develop.library.tools.animator.VMAnimator
import com.vmloft.develop.library.tools.utils.VMSystem
import com.vmloft.develop.library.tools.utils.logger.VMLog

import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.net.URL


/**
 * Create by lzan13 on 2019/5/15 23:13
 * 描述：礼物动画界面
 */
@Route(path = GiftRouter.giftAnim)
class GiftAnimActivity : BVMActivity<ActivityGiftAnimBinding, GiftViewModel>() {

    override var isDarkStatusBar: Boolean = false

    // 匹配方式
    @Autowired(name = CRouter.paramsObj0)
    lateinit var gift: Gift


    private var mAnimatorWrapBG: VMAnimator.AnimatorSetWrap? = null
    private var mAnimatorWrapGift: VMAnimator.AnimatorSetWrap? = null

    override fun initVM(): GiftViewModel = getViewModel()

    override fun initVB() = ActivityGiftAnimBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
    }

    override fun initData() {
        ARouter.getInstance().inject(this)
        if (gift.type == 0) {
            startAnim()
        } else if (gift.type == 1 && PermissionManager.storagePermission(this)) {
            mViewModel.download(gift)
        } else {
            finish()
        }

        bindInfo()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "download") {
            playAnimation()
        }
    }

    override fun onModelError(model: BViewModel.UIModel) {
        super.onModelError(model)
        model.error?.let {
            VMSystem.runInUIThread({ finish() }, 1000)
        }
    }

    private fun bindInfo() {
        IMGLoader.loadCover(mBinding.giftIV, gift.cover.path)
    }

    /**
     * 播放动画
     */
    private fun playAnimation() {
//        mBinding.giftSVGAIV.
        mBinding.giftSVGAIV.callback = object : SVGACallback {
            override fun onFinished() {
                VMLog.i("svga onFinished")
                finish()
            }

            override fun onPause() {
                VMLog.i("svga onPause")
            }

            override fun onRepeat() {
                VMLog.i("svga onRepeat")
            }

            override fun onStep(frame: Int, percentage: Double) {
//                VMLog.i("svga onStep $frame $percentage")
            }
        }
        val parser = SVGAParser.shareParser()
        val path = RConstants.mediaHost() + gift.animation.path
        parser.decodeFromURL(URL(path), object : SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                VMLog.i("解析礼物动效成功 $path")
                val drawable = SVGADrawable(videoItem)
                mBinding.giftSVGAIV.setImageDrawable(drawable)
                mBinding.giftSVGAIV.startAnimation()
            }

            override fun onError() {
                VMLog.e("解析礼物动效失败 $path")
            }
        })
    }

    /**
     * 开始动画
     */
    private fun startAnim() {
        mBinding.giftBGIV.visibility = View.VISIBLE
        mBinding.giftIV.visibility = View.VISIBLE

        val bgOptions = VMAnimator.AnimOptions(mBinding.giftBGIV, floatArrayOf(0f, 360f), VMAnimator.rotation, 2000)
        mAnimatorWrapBG = VMAnimator.createAnimator().play(bgOptions)
        mAnimatorWrapBG?.start()

        val giftOptions = VMAnimator.AnimOptions(mBinding.giftIV, floatArrayOf(-5f, 5f), VMAnimator.rotation, 1000)
        mAnimatorWrapGift = VMAnimator.createAnimator().play(giftOptions)
        mAnimatorWrapGift?.start()
        // 两秒后结束
        VMSystem.runInUIThread({
            stopAnim()
            finish()
        }, 2000)
    }

    /**
     * 停止动画
     */
    private fun stopAnim() {
        mAnimatorWrapBG?.cancel()
        mAnimatorWrapGift?.cancel()
    }

    override fun onResume() {
        super.onResume()
        mAnimatorWrapBG?.resume()
        mAnimatorWrapGift?.resume()
    }

    override fun onPause() {
        super.onPause()
        mAnimatorWrapBG?.pause()
        mAnimatorWrapGift?.pause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stopAnim()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_fade_open_enter, R.anim.activity_fade_close_exit)
    }
}