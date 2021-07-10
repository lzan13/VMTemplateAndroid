package com.vmloft.develop.app.template.ui.user

import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.google.android.material.tabs.TabLayoutMediator

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.im.IMManager
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.ui.post.PostFallsFragment
import com.vmloft.develop.app.template.ui.post.PostLikesFragment
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.widget.behavior.VMBehaviorFrameLayout

import kotlinx.android.synthetic.main.activity_user_info.*

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2021/01/20 22:56
 * 描述：用户信息界面
 */
@Route(path = AppRouter.appUserInfo)
class UserInfoActivity : BVMActivity<UserInfoViewModel>() {

    @Autowired
    lateinit var user: User

    private val fragmentList = arrayListOf<Fragment>()
    private lateinit var publishFragment: PostFallsFragment
//    private lateinit var likesFragment: PostLikesFragment

    private val titles = listOf("发布的")

    override fun initVM(): UserInfoViewModel = getViewModel()

    override fun layoutId() = R.layout.activity_user_info

    override fun initUI() {
        super.initUI()
        CUtils.setDarkMode(this, false)

        setTopTitleColor(R.color.app_title_display)

        initBehavior()

        infoCoverIV.setOnClickListener { CRouter.goDisplaySingle(if (user.cover.isNullOrEmpty()) user.avatar else user.cover) }
        infoAvatarIV.setOnClickListener { CRouter.goDisplaySingle(user.avatar) }
        infoFansLL.setOnClickListener { }
        infoFollowLL.setOnClickListener { }
        infoLikeLL.setOnClickListener { }

        infoFollowMeTV.setOnClickListener { follow() }
        infoSendBtn.setOnClickListener { IMManager.goChat(user.id) }

    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        mViewModel.getUserInfo(user.id)

        initFragmentList()
        initViewPager()

        bindInfo()
    }

    /**
     * 初始化 Fragment 集合
     */
    private fun initFragmentList() {
        publishFragment = PostFallsFragment.newInstance(user.id)
//        likesFragment = PostLikesFragment.newInstance(user.id)

        fragmentList.run {
            add(publishFragment)
//            add(likesFragment)
        }
    }

    private fun initBehavior() {
        infoBehaviorLayout.setStickHeaderHeight(VMDimen.dp2px(48) + VMDimen.dp2px(36) + VMDimen.statusBarHeight)
        infoBehaviorLayout.setHeaderScrollListener(object : VMBehaviorFrameLayout.SimpleHeaderScrollListener() {
            override fun onScroll(dy: Int, percent: Float) {
                infoCoverIV.translationY = dy / 2f

                setTopTitle(if (percent > 0.6) user.nickname else "")

                setTopBGColor(Color.argb((percent * 255).toInt(), 42, 42, 42))
            }
        })
        infoBehaviorLayout.setMaxHeaderHeight(VMDimen.dp2px(512))

    }

    /**
     * 初始化 ViewPager
     */
    private fun initViewPager() {
//        viewPager.offscreenPageLimit = titles.size - 1
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int) = fragmentList[position]

            override fun getItemCount() = fragmentList.size
        }
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "userInfo") {
            user = model.data as User
            bindInfo()
        } else if (model.type == "follow") {
            user.relation++
            setupFollowStatus()
        } else if (model.type == "cancelFollow") {
            user.relation--
            setupFollowStatus()
        }
    }

    /**
     * 绑定用户数据
     */
    private fun bindInfo() {
        if (user.cover.isNullOrEmpty()) {
            IMGLoader.loadCover(infoCoverIV, user.avatar, isBlur = true)
        } else {
            IMGLoader.loadCover(infoCoverIV, user.cover, isBlur = true)
        }

        IMGLoader.loadAvatar(infoAvatarIV, user.avatar)

        when (user.gender) {
            1 -> infoGenderIV.setImageResource(R.drawable.ic_gender_man)
            0 -> infoGenderIV.setImageResource(R.drawable.ic_gender_woman)
            else -> infoGenderIV.setImageResource(R.drawable.ic_gender_man)
        }

        infoNameTV.text = user.nickname
        infoAddressTV.text = user.address
        infoSignatureTV.text = user.signature

        infoLikeTV.text = user.likeCount.toString()
        infoFollowTV.text = user.followCount.toString()
        infoFansTV.text = user.fansCount.toString()

        setupFollowStatus()
    }

    private fun setupFollowStatus() {
        infoFollowMeTV.text = when (user.relation) {
            0 -> VMStr.byRes(R.string.follow_status_0)
            1 -> VMStr.byRes(R.string.follow_status_1)
            2 -> VMStr.byRes(R.string.follow_status_2)
            else -> VMStr.byRes(R.string.follow)
        }
    }

    /**
     * 关注与取消
     */
    private fun follow() {
        if (user.relation != 0 && user.relation != 2) {
            mViewModel.follow(user.id)
        } else {
            mViewModel.cancelFollow(user.id)
        }
    }


}