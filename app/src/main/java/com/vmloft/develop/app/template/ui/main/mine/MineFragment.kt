package com.vmloft.develop.app.template.ui.main.mine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

import com.google.android.material.tabs.TabLayoutMediator

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.FragmentMineBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.ui.post.PostFallsFragment
import com.vmloft.develop.app.template.ui.post.PostLikesFragment
import com.vmloft.develop.library.base.BFragment
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.DConstants
import com.vmloft.develop.library.gift.GiftRouter
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.tools.utils.VMColor
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：我的
 */
class MineFragment : BFragment<FragmentMineBinding>() {

    override var isDarkStatusBar: Boolean = false

    private lateinit var user: User

    private val fragmentList = arrayListOf<Fragment>()
    private lateinit var publishFragment: PostFallsFragment
    private lateinit var likesFragment: PostLikesFragment

    private val titles = listOf("我发布的", "我喜欢的")


    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = FragmentMineBinding.inflate(inflater, parent, false)

    override fun initUI() {
        super.initUI()

        mBinding.tabTopSpaceView.layoutParams.height = VMDimen.dp2px(48) + VMDimen.statusBarHeight
        setTopTitleColor(R.color.app_title_display)

        mBinding.mineCoverIV.setOnClickListener { CRouter.goDisplaySingle(if (user.cover.isNullOrEmpty()) user.avatar else user.cover) }
        mBinding.mineAvatarIV.setOnClickListener { CRouter.goDisplaySingle(user.avatar) }
        mBinding.mineScoreTV.setOnClickListener { CRouter.go(AppRouter.appGold) }
        mBinding.mineFansLL.setOnClickListener { CRouter.go(AppRouter.appMineRelation, what = 1) }
        mBinding.mineFollowLL.setOnClickListener { CRouter.go(AppRouter.appMineRelation, what = 0) }
        mBinding.mineLikeLL.setOnClickListener { }

        mBinding.mineGiftIV.setOnClickListener { CRouter.go(GiftRouter.giftMine, str0 = user.id) }
        mBinding.mineInfoTV.setOnClickListener { CRouter.go(AppRouter.appPersonalInfo) }
        mBinding.mineSettingsIV.setOnClickListener { CRouter.go(AppRouter.appSettings) }

        LDEventBus.observe(this, DConstants.Event.userInfo, User::class.java) {
            user = it
            bindInfo()
        }
    }

    override fun initData() {
        user = SignManager.getSignUser()

        initFragmentList()
        initViewPager()

        bindInfo()
    }

    /**
     * 初始化 Fragment 集合
     */
    private fun initFragmentList() {
        publishFragment = PostFallsFragment.newInstance(user.id)
        likesFragment = PostLikesFragment.newInstance(user.id)

        fragmentList.run {
            add(publishFragment)
            add(likesFragment)
        }
    }

    /**
     * 初始化 ViewPager
     */
    private fun initViewPager() {
        mBinding.viewPager.offscreenPageLimit = titles.size - 1
        mBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int) = fragmentList[position]

            override fun getItemCount() = fragmentList.size
        }
        // 将 TabLayout 与 ViewPager 进行绑定
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    /**
     * 绑定用户数据
     */
    private fun bindInfo() {
        if (user.cover.isNullOrEmpty()) {
            IMGLoader.loadCover(mBinding.mineCoverIV, user.avatar, isBlur = true, thumbExt = "!vt192")
        } else {
            IMGLoader.loadCover(mBinding.mineCoverIV, user.cover, isBlur = true, thumbExt = "!vt192")
        }

        val avatar: String = user.avatar
        IMGLoader.loadAvatar(mBinding.mineAvatarIV, avatar)
        // 身份
        if (ConfigManager.appConfig.tradeConfig.vipEntry && user.role.identity in 100..199) {
            mBinding.mineNameTV.setTextColor(VMColor.byRes(R.color.app_identity_special))
            mBinding.mineIdentityIV.visibility = View.VISIBLE
        } else {
            mBinding.mineIdentityIV.visibility = View.GONE
        }
        // 性别
        when (user.gender) {
            1 -> mBinding.mineGenderIV.setImageResource(R.drawable.ic_gender_man)
            0 -> mBinding.mineGenderIV.setImageResource(R.drawable.ic_gender_woman)
            else -> mBinding.mineGenderIV.setImageResource(R.drawable.ic_gender_unknown)
        }
        val nickname = if (user.nickname.isNullOrEmpty()) VMStr.byRes(R.string.info_nickname_default) else user.nickname
        setTopTitle(nickname)
        mBinding.mineNameTV.text = nickname
        mBinding.mineScoreTV.text = FormatUtils.wrapBig(user.score)
        mBinding.mineAddressTV.text = if (user.address.isNullOrEmpty()) VMStr.byRes(R.string.info_address_default) else user.address
        mBinding.mineSignatureTV.text = if (user.signature.isNullOrEmpty()) VMStr.byRes(R.string.info_signature_default) else user.signature

        mBinding.mineLikeTV.text = user.likeCount.toString()
        mBinding.mineFollowTV.text = user.followCount.toString()
        mBinding.mineFansTV.text = user.fansCount.toString()

        mBinding.mineHeaderLayout.updateLayout()

        // 根据配置控制一些入口
        mBinding.mineScoreTV.visibility = if (ConfigManager.appConfig.tradeConfig.scoreEntry && user.role.identity > 8) View.VISIBLE else View.GONE
        mBinding.mineGiftIV.visibility = if (ConfigManager.appConfig.chatConfig.giftEntry) View.VISIBLE else View.GONE

    }


}