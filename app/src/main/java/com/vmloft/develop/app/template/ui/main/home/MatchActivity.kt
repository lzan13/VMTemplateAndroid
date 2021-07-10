package com.vmloft.develop.app.template.ui.main.home

import android.widget.FrameLayout
import android.widget.ImageView
import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityMatchBinding
import com.vmloft.develop.app.template.request.bean.Match
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.tools.animator.VMAnimator
import com.vmloft.develop.library.tools.utils.VMDimen

import kotlinx.android.synthetic.main.activity_match.*

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2019/5/15 23:13
 *
 * 匹配界面
 */
@Route(path = AppRouter.appMatch)
class MatchActivity : BVMActivity<MatchViewModel>() {

    // 自己
    lateinit var mUser: User

    private var mPage: Int = CConstants.defaultPage
    private val mLimit: Int = CConstants.defaultLimit

    // 正在匹配的人，使用 Map 是为了过滤掉重复的信息
    private val matchList = mutableListOf<Match>()
    private var avatarSize = 0

    private var mAnimatorWrap: VMAnimator.AnimatorSetWrap? = null
    private var mAnimatorWrap2: VMAnimator.AnimatorSetWrap? = null

    override fun initVM(): MatchViewModel = getViewModel()

    override fun layoutId() = R.layout.activity_match

    override fun initUI() {
        super.initUI()
        CUtils.setDarkMode(mActivity, false)

        setTopTitle(R.string.match_emotion)
        setTopTitleColor(R.color.app_title_display)

        (mBinding as ActivityMatchBinding).viewModel = mViewModel

        matchChangeBtn.setOnClickListener { mViewModel.getMatchList(mPage) }
    }

    override fun initData() {
        mUser = SignManager.getCurrUser() ?: User()
        avatarSize = VMDimen.dp2px(48)

        // 获取匹配信息
        mViewModel.getMatchList()

        bindInfo()

        startAnim()

    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "matchList") {
            val paging = model.data as RPaging<Match>
            if (paging.currentCount + paging.page * paging.limit >= paging.totalCount) {
                mPage = CConstants.defaultPage
            } else {
                mPage++
            }
            matchList.clear()
            matchList.addAll(paging.data as Collection<Match>)
            setupMatchList()
        }
    }

    /**
     * 装载用户信息
     */
    private fun bindInfo() {
        // 加载头像
        IMGLoader.loadAvatar(matchAvatarIV, mUser.avatar)
    }

    /**
     * 开始匹配，需要经自己的信息提交到后端
     */
    private fun startAnim() {
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

    /**
     * 加载匹配数据
     */
    private fun setupMatchList() {
        if (matchList.size == 0) {
            errorBar(R.string.common_not_more)
        }
        // 清空老数据
        matchContainerFL.removeAllViews()
        for (match in matchList) {
            // 过滤掉自己
            if (match.user.id == mUser.id) continue

            val imageView = ImageView(mActivity)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            val lp = FrameLayout.LayoutParams(avatarSize, avatarSize)
            val x: Int = CUtils.random(matchContainerFL.width - avatarSize)
            val y: Int = CUtils.random(matchContainerFL.height - avatarSize)
            imageView.x = x.toFloat()
            imageView.y = y.toFloat()
            imageView.alpha = 0.0f
            matchContainerFL.addView(imageView, lp)
            // 加载头像
            IMGLoader.loadAvatar(imageView, match.user.avatar)

            imageView.setOnClickListener {
                AppRouter.goUserInfo(match.user)
            }

            // 动画出现
            val delay = CUtils.random(50) * 50.toLong()
            val options = VMAnimator.AnimOptions(imageView, floatArrayOf(0.0f, 1.0f), VMAnimator.alpha, 1500)
            VMAnimator.createAnimator().play(options).start(delay = delay)
        }
    }

    override fun onResume() {
        super.onResume()
        mAnimatorWrap?.resume()
        mAnimatorWrap2?.resume()
    }

    override fun onPause() {
        super.onPause()
        mAnimatorWrap?.pause()
        mAnimatorWrap2?.pause()
    }

    override fun onDestroy() {
        mAnimatorWrap?.cancel()
        mAnimatorWrap2?.cancel()
        super.onDestroy()
    }

}