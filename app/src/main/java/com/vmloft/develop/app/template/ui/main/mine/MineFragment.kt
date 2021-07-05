package com.vmloft.develop.app.template.ui.main.mine

import android.graphics.Color
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
import com.vmloft.develop.library.tools.widget.behavior.VMBehaviorFrameLayout

import kotlinx.android.synthetic.main.fragment_mine.*


/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：我的
 */
class MineFragment : BaseFragment() {

    private lateinit var mUser: User

    private val fragmentList = arrayListOf<Fragment>()
    private lateinit var publishFragment: PostFallsFragment
    private lateinit var likesFragment: PostLikesFragment

    private val titles = listOf("我发布的", "我喜欢的")


    override fun layoutId() = R.layout.fragment_mine

    override fun initUI() {
        super.initUI()
        CUtils.setDarkMode(requireActivity(), false)

        setTopTitleColor(R.color.app_title_display)

        initBehavior()

        mineCoverIV.setOnClickListener { CRouter.goDisplaySingle(if (mUser.cover.isNullOrEmpty()) mUser.avatar else mUser.cover) }
        mineAvatarIV.setOnClickListener { CRouter.goDisplaySingle(mUser.avatar) }
        mineFansLL.setOnClickListener { }
        mineFollowLL.setOnClickListener { }
        mineLikeLL.setOnClickListener { }

        mineEditInfoTV.setOnClickListener { CRouter.go(AppRouter.appPersonalInfo) }
        mineSettingsBtn.setOnClickListener { CRouter.go(AppRouter.appSettings) }

        LDEventBus.observe(this, Constants.userInfoEvent, User::class.java, {
            mUser = it
            bindInfo()
        })
    }

    override fun initData() {
        mUser = SignManager.instance.getCurrUser() ?: User()

        initFragmentList()
        initViewPager()

        bindInfo()
    }

    /**
     * 初始化 Fragment 集合
     */
    private fun initFragmentList() {
        publishFragment = PostFallsFragment.newInstance(mUser.id)
        likesFragment = PostLikesFragment.newInstance(mUser.id)

        fragmentList.run {
            add(publishFragment)
            add(likesFragment)
        }
    }

    private fun initBehavior() {
        mineBehaviorLayout.setStickHeaderHeight(VMDimen.dp2px(48) + VMDimen.dp2px(36) + VMDimen.statusBarHeight)
        mineBehaviorLayout.setHeaderScrollListener(object : VMBehaviorFrameLayout.SimpleHeaderScrollListener() {
            override fun onScroll(dy: Int, percent: Float) {
                mineCoverIV.translationY = dy / 2f

                setTopTitle(if (percent > 0.6) if (mUser.nickname.isNullOrEmpty()) "小透明" else mUser.nickname else "")

                setTopBGColor(Color.argb((percent * 255).toInt(), 42, 42, 42))
            }
        })
        mineBehaviorLayout.setMaxHeaderHeight(VMDimen.dp2px(512))

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
        if (mUser.cover.isNullOrEmpty()) {
            IMGLoader.loadCover(mineCoverIV, mUser.avatar, isBlur = true)
        } else {
            IMGLoader.loadCover(mineCoverIV, mUser.cover, isBlur = true)
        }

        val avatar: String = mUser.avatar
        IMGLoader.loadAvatar(mineAvatarIV, avatar)

        when (mUser.gender) {
            1 -> mineGenderIV.setImageResource(R.drawable.ic_gender_man)
            0 -> mineGenderIV.setImageResource(R.drawable.ic_gender_woman)
            else -> mineGenderIV.setImageResource(R.drawable.ic_gender_man)
        }

        mineNameTV.text = if (mUser.nickname.isNullOrEmpty()) VMStr.byRes(R.string.info_nickname_default) else mUser.nickname
        mineAddressTV.text = if (mUser.address.isNullOrEmpty()) VMStr.byRes(R.string.info_address_default) else mUser.address
        mineSignatureTV.text = if (mUser.signature.isNullOrEmpty()) VMStr.byRes(R.string.info_signature_default) else mUser.signature

        mineLikeTV.text = mUser.likeCount.toString()
        mineFollowTV.text = mUser.followCount.toString()
        mineFansTV.text = mUser.fansCount.toString()
    }


}