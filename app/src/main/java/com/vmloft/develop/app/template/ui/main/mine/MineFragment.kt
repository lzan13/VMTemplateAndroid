package com.vmloft.develop.app.template.ui.main.mine

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

import com.google.android.material.tabs.TabLayoutMediator

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.ui.post.PostFallsFragment
import com.vmloft.develop.app.template.ui.post.PostLikesFragment
import com.vmloft.develop.library.common.base.BaseFragment
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr

import kotlinx.android.synthetic.main.fragment_mine.*


/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：我的
 */
class MineFragment : BaseFragment() {

    private lateinit var user: User

    private val fragmentList = arrayListOf<Fragment>()
    private lateinit var publishFragment: PostFallsFragment
    private lateinit var likesFragment: PostLikesFragment

    private val titles = listOf("我发布的", "我喜欢的")


    override fun layoutId() = R.layout.fragment_mine

    override fun initUI() {
        super.initUI()
        CUtils.setDarkMode(requireActivity(), false)

        tabTopSpaceView.layoutParams.height = VMDimen.dp2px(48) + VMDimen.statusBarHeight
        setTopTitleColor(R.color.app_title_display)

        mineCoverIV.setOnClickListener { CRouter.goDisplaySingle(if (user.cover.isNullOrEmpty()) user.avatar else user.cover) }
        mineAvatarIV.setOnClickListener { CRouter.goDisplaySingle(user.avatar) }
        mineFansLL.setOnClickListener { }
        mineFollowLL.setOnClickListener { }
        mineLikeLL.setOnClickListener { }

        mineEditInfoTV.setOnClickListener { CRouter.go(AppRouter.appPersonalInfo) }
        mineSettingsBtn.setOnClickListener { CRouter.go(AppRouter.appSettings) }

        LDEventBus.observe(this, Constants.userInfoEvent, User::class.java, {
            user = it
            bindInfo()
        })
    }

    override fun initData() {
        user = SignManager.getCurrUser() ?: User()

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
        viewPager.offscreenPageLimit = titles.size - 1
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int) = fragmentList[position]

            override fun getItemCount() = fragmentList.size
        }
        // 将 TabLayout 与 ViewPager 进行绑定
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        activity?.let {
            CUtils.setDarkMode(it, false)
        }
    }

    /**
     * 绑定用户数据
     */
    private fun bindInfo() {
        if (user.cover.isNullOrEmpty()) {
            IMGLoader.loadCover(mineCoverIV, user.avatar, isBlur = true)
        } else {
            IMGLoader.loadCover(mineCoverIV, user.cover, isBlur = true)
        }

        val avatar: String = user.avatar
        IMGLoader.loadAvatar(mineAvatarIV, avatar)

        when (user.gender) {
            1 -> mineGenderIV.setImageResource(R.drawable.ic_gender_man)
            0 -> mineGenderIV.setImageResource(R.drawable.ic_gender_woman)
            else -> mineGenderIV.setImageResource(R.drawable.ic_gender_unknown)
        }
        val nickname = if (user.nickname.isNullOrEmpty()) VMStr.byRes(R.string.info_nickname_default) else user.nickname
        setTopTitle(nickname)
        mineNameTV.text = nickname
        mineAddressTV.text = if (user.address.isNullOrEmpty()) VMStr.byRes(R.string.info_address_default) else user.address
        mineSignatureTV.text = if (user.signature.isNullOrEmpty()) VMStr.byRes(R.string.info_signature_default) else user.signature

        mineLikeTV.text = user.likeCount.toString()
        mineFollowTV.text = user.followCount.toString()
        mineFansTV.text = user.fansCount.toString()

        mineHeaderLayout.updateLayout()
    }


}