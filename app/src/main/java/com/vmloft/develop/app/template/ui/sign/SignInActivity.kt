package com.vmloft.develop.app.template.ui.sign


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.alibaba.android.arouter.facade.annotation.Route
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivitySignInBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2020/6/4 17:10
 * 描述：登录界面
 */
@Route(path = AppRouter.appSignIn)
class SignInActivity : BVMActivity<SignViewModel>() {

    private val fragmentKeys = arrayListOf("signInByPassword", "signInBySMS")
    private var currentIndex = 0
    private var currentFragment: Fragment? = null

    private val fragmentList = arrayListOf<Fragment>()
    private lateinit var byPasswordFragment: SignInByPasswordFragment
    private lateinit var byCodeFragment: SignInByCodeFragment

    override fun initVM(): SignViewModel = getViewModel()

    override fun layoutId(): Int = R.layout.activity_sign_in

    override fun initUI() {
        super.initUI()
        (mBinding as ActivitySignInBinding).viewModel = mViewModel

        setTopTitle(R.string.sign_in)
        setTopEndBtnListener(VMStr.byRes(R.string.sign_to_sign_up)) { CRouter.go(AppRouter.appSignUp) }
//        setTopEndBtnListener("验证码登录") {
//            if (currentIndex == 0) {
//                switchFragment(1)
//                setTopEndBtn("密码登录")
//            } else {
//                switchFragment(0)
//                setTopEndBtn("验证码登录")
//            }
//        }
    }

    override fun initData() {
        initFragmentList()
        switchFragment(currentIndex)
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "signIn") {
            // 登录成功，跳转到主页
            CRouter.goMain()
            finish()
        }
    }

    /**
     * 界面切换
     */
    private fun switchFragment(position: Int) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val oldFragment: Fragment = fragmentList[currentIndex]
        val newFragment: Fragment = fragmentList[position]
        if (currentFragment == null) {
            transaction.add(R.id.signContainerFL, newFragment, fragmentKeys[position]).commit()
        } else {
            if (newFragment.isAdded) {
                transaction.hide(oldFragment).show(newFragment).commit()
            } else {
                transaction.hide(oldFragment).add(R.id.signContainerFL, newFragment, fragmentKeys[position]).commit()
            }
        }

        currentIndex = position
        currentFragment = newFragment
    }

    /**
     * 初始化 Fragment 集合
     */
    private fun initFragmentList() {
        byPasswordFragment = SignInByPasswordFragment()
        byCodeFragment = SignInByCodeFragment()

        fragmentList.run {
            add(byPasswordFragment)
            add(byCodeFragment)
        }
    }
}
