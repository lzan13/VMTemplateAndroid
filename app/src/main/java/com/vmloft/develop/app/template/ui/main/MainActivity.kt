package com.vmloft.develop.app.template.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivityMainBinding
import com.vmloft.develop.app.template.im.IMManager
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.ui.main.explore.ExploreFragment
import com.vmloft.develop.app.template.ui.main.home.HomeFragment
import com.vmloft.develop.app.template.ui.main.mine.MineFragment
import com.vmloft.develop.app.template.ui.main.msg.MsgFragment
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.bean.Version
import com.vmloft.develop.library.data.viewmodel.MainViewModel
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.tools.utils.VMSystem

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/05/02 19:08
 * 描述：主界面
 */
@Route(path = CRouter.appMain)
class MainActivity : BVMActivity<ActivityMainBinding, MainViewModel>() {

    private val currentTabKey = "currentTabKey"
    private val fragmentKeys = arrayListOf("homeKey", "exploreKey", "msgKey", "mineKey")
    private var currentTab = 0
    private var currentFragment: Fragment? = null

    private val fragmentList = arrayListOf<Fragment>()
    private lateinit var homeFragment: HomeFragment
    private lateinit var exploreFragment: ExploreFragment
    private lateinit var msgFragment: MsgFragment
    private lateinit var mineFragment: MineFragment

    private lateinit var msgUnreadTV: TextView

    @JvmField
    @Autowired
    var type: Int = 0

    override fun initVB() = ActivityMainBinding.inflate(layoutInflater)

    override fun initVM(): MainViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!SignManager.isSingIn()) {
            CRouter.go(AppRouter.appSignGuide)
            return finish()
        }
        if (savedInstanceState == null) {
            // 默认为0
            fragmentList.clear()
            initFragmentList()
        } else {
            //内存被回收了，fragments的list也被回收了，重新add进去
            currentTab = savedInstanceState.getInt(currentTabKey)
            fragmentList.clear()
            homeFragment = (supportFragmentManager.findFragmentByTag(fragmentKeys[0]) as HomeFragment?) ?: HomeFragment()
            exploreFragment = (supportFragmentManager.findFragmentByTag(fragmentKeys[1]) as ExploreFragment?) ?: ExploreFragment()
            msgFragment = (supportFragmentManager.findFragmentByTag(fragmentKeys[2]) as MsgFragment?) ?: MsgFragment()
            mineFragment = (supportFragmentManager.findFragmentByTag(fragmentKeys[3]) as MineFragment?) ?: MineFragment()
            fragmentList.run {
                add(homeFragment)
                add(exploreFragment)
                add(msgFragment)
                add(mineFragment)
            }
            currentFragment = fragmentList[currentTab]
        }
        switchFragment(currentTab)
    }

    override fun initUI() {
        super.initUI()

        if (!SignManager.isSingIn()) return

        initBottomNav()

        // 监听新消息过来，这里主要更新Tab未读
        LDEventBus.observe(this, IMConstants.Common.changeUnreadCount, Int::class.java) {
            checkMsgUnread()
        }

    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        if (!SignManager.isSingIn()) return

        mViewModel.userInfo()

        checkMsgUnread()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        ARouter.getInstance().inject(this)
        if (type == 1) {
            // 清空登录信息统一交给 Main 界面处理
            SignManager.signOut()
            IMManager.signOut()
        }
        if (!SignManager.isSingIn()) {
            CRouter.go(AppRouter.appSignGuide)
            finish()
            return
        }
    }

    /**
     * 初始化底部导航
     */
    private fun initBottomNav() {
        // 如果导航是多色图标，需要取消 BottomNavigationView 的着色效果，自己去设置 selector
        // mainNav.itemIconTintList = null
        mBinding.mainNav.setOnNavigationItemSelectedListener(onNavigationItemSelected)

        val msgNavMenu = mBinding.mainNav.getChildAt(0) as BottomNavigationMenuView
        val msgNavItem = msgNavMenu.getChildAt(2) as BottomNavigationItemView
        val unreadCL = LayoutInflater.from(this).inflate(R.layout.widget_unread_layout, msgNavItem, false)
        msgNavItem.addView(unreadCL)

        msgUnreadTV = unreadCL.findViewById(R.id.unreadTV) as TextView
    }

    /**
     * 导航监听
     */
    private val onNavigationItemSelected = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.nav_home -> switchFragment(0)
            R.id.nav_explore -> switchFragment(1)
            R.id.nav_msg -> switchFragment(2)
            R.id.nav_mine -> switchFragment(3)
        }
        true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存当前 Tab
        outState.putInt(currentTabKey, currentTab)
    }

    /**
     * 界面切换
     */
    private fun switchFragment(tab: Int) {
        if (tab == 0) {
            mBinding.mainNav.setBackgroundResource(R.color.app_bg_nav_dark)
        } else {
            mBinding.mainNav.setBackgroundResource(R.color.app_bg_nav)
        }
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val oldFragment: Fragment = fragmentList[currentTab]
        val newFragment: Fragment = fragmentList[tab]
        if (currentFragment == null) {
            transaction.add(R.id.mainContainerFL, newFragment, fragmentKeys[tab]).commit()
        } else {
            if (newFragment.isAdded) {
                transaction.hide(oldFragment).show(newFragment).commit()
            } else {
                transaction.hide(oldFragment).add(R.id.mainContainerFL, newFragment, fragmentKeys[tab]).commit()
            }
        }

        currentTab = tab
        currentFragment = newFragment
    }

    /**
     * 初始化 Fragment 集合
     */
    private fun initFragmentList() {
        homeFragment = HomeFragment()
        exploreFragment = ExploreFragment()
        msgFragment = MsgFragment()
        mineFragment = MineFragment()

        fragmentList.run {
            add(homeFragment)
            add(exploreFragment)
            add(msgFragment)
            add(mineFragment)
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "userInfo") {
            (model.data as? User)?.let {
                if (it.avatar.isNullOrEmpty() || it.nickname.isNullOrEmpty()) {
                    // 去设置用户信息
                    CRouter.go(AppRouter.appPersonalInfoGuide)
                } else {
                    // 这里放在用户信息成功后检查版本
                    mViewModel.checkVersion()
                }
            }
        } else if (model.type == "checkVersion") {
            if (model.data == null) return
            showVersionDialog(model.data as Version)
        }
    }

    /**
     * 检查消息未读
     */
    private fun checkMsgUnread() {
        val unreadCount = IMManager.getUnreadCount()
        val unreadStr = if (unreadCount > 99) {
            "99+"
        } else {
            unreadCount.toString()
        }
        msgUnreadTV.text = unreadStr
        msgUnreadTV.visibility = if (unreadCount > 0) View.VISIBLE else View.GONE
    }


    /**
     * 显示版本更新弹窗
     */
    private fun showVersionDialog(version: Version) {
        if (version.versionCode <= VMSystem.versionCode) return

        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            // 判断是否需要强制更新，如果强制更新，则不能关闭对话框
            val force = version.force or (version.versionCode - VMSystem.versionCode > 5)
            dialog.backDismissSwitch = !force
            dialog.touchDismissSwitch = !force

            dialog.setTitle(version.title)
            dialog.setContent(version.desc)
            dialog.setNegative(if (force) "" else version.negativeBtn)
            dialog.setPositive(version.positiveBtn) {
                if (version.url.isNullOrEmpty()) {
                    finish()
                } else {
                    CRouter.goWeb(version.url, true)
                    finish()
                }
            }
            dialog.show()
        }
    }

//    override fun onBackPressed() {
//        CRouter.goHome()
//    }
}
