package com.vmloft.develop.library.common.base

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

import com.vmloft.develop.library.common.R
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.common.widget.CommonDialog
import com.vmloft.develop.library.tools.utils.VMColor

import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMNetwork
import com.vmloft.develop.library.tools.widget.VMTopBar
import kotlinx.android.synthetic.main.widget_common_empty_status_view.*

import kotlinx.android.synthetic.main.widget_common_top_bar.*

/**
 * Created by lzan13 on 2020/02/15 11:16
 * 描述：Activity MVVM 框架基类
 */
abstract class BVMActivity<VM : BViewModel> : AppCompatActivity() {

    protected var mDialog: CommonDialog? = null

    // 是否隐藏顶部控件
    open var isHideTopSpace: Boolean = false

    // 是否居中显示标题
    open var isCenterTitle: Boolean = false

    // 是否设置黑色状态栏
    open var isDarkStatusBar: Boolean = true

    protected lateinit var mActivity: Activity
    protected lateinit var mBinding: ViewDataBinding
    protected lateinit var mViewModel: VM

    // 统一的 TopBar
    private var topBar: VMTopBar? = null
    private var spaceView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = this

        mViewModel = initVM()

        mBinding = DataBindingUtil.setContentView(this, layoutId())
        mBinding.lifecycleOwner = this

        initUI()

        initData()

        startObserve()

    }

    /**
     * 布局资源 id
     */
    abstract fun layoutId(): Int

    /**
     * 初始化 ViewModel
     */
    abstract fun initVM(): VM

    /**
     * 初始化 UI
     */
    open fun initUI() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setupTobBar()
    }

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 模型变化回调
     */
    open fun onModelLoading(model: BViewModel.UIModel) {

    }

    /**
     * 模型变化回调
     */
    abstract fun onModelRefresh(model: BViewModel.UIModel)

    open fun onModelError(model: BViewModel.UIModel) {
        model.error?.let { message -> errorBar(message) }
    }

    /**
     * 开始观察 View 生命周期
     */
    private fun startObserve() {
        mViewModel.uiState.observe(this, {
            if (it.isLoading) {
                onModelLoading(it)
            } else {
                if (it.isSuccess) {
                    onModelRefresh(it)
                } else {
                    onModelError(it)
                }
            }
            it.toast?.let { message -> showBar(message) }
        })
    }

    /**
     * 装载 TopBar
     */
    private fun setupTobBar() {
        CUtils.setDarkMode(mActivity, isDarkStatusBar)

        if (!isHideTopSpace) {
            // 设置状态栏透明主题时，布局整体会上移，所以给头部 View 设置 StatusBar 的高度
            commonTopSpace?.layoutParams?.height = VMDimen.statusBarHeight
        }

        commonTopBar?.setCenter(isCenterTitle)
        commonTopBar?.setTitleStyle(R.style.AppText_Title)
        commonTopBar?.setIcon(R.drawable.ic_arrow_back)
        commonTopBar?.setIconListener { onBackPressed() }
        commonTopBar?.setEndBtnTextStyle(R.style.AppText_TopBarEndBtn)
    }

    /**
     * 设置顶部标题背景色
     */
    protected fun setTopBGColor(color: Int) {
        commonTopLL?.setBackgroundColor(color)
    }

    /**
     * 设置图标
     */
    protected fun setTopIcon(resId: Int) {
        commonTopBar?.setIcon(resId)
    }

    /**
     * 设置标题
     */
    protected fun setTopTitle(resId: Int) {
        commonTopBar?.setTitle(resId)
    }

    /**
     * 设置标题
     */
    protected fun setTopTitle(title: String) {
        commonTopBar?.setTitle(title)
    }

    /**
     * 设置标题颜色
     */
    protected fun setTopTitleColor(resId: Int) {
        commonTopBar?.setIconColor(VMColor.byRes(resId))
        commonTopBar?.setTitleColor(resId)
    }

    /**
     * 设置子标题
     */
    protected fun setTopSubtitle(title: String?) {
        commonTopBar?.setSubtitle(title)
    }

    /**
     * 设置 TopBar 右侧按钮
     */
    protected fun setTopEndBtnEnable(enable: Boolean) {
        commonTopBar?.setEndBtnEnable(enable)
    }

    /**
     * 设置 TopBar 右侧按钮
     */
    protected fun setTopEndBtn(str: String?) {
        commonTopBar?.setEndBtn(str)
    }

    /**
     * 设置 TopBar 右侧按钮监听
     */
    protected fun setTopEndBtnListener(str: String? = null, listener: View.OnClickListener) {
        commonTopBar?.setEndBtnListener(str, listener)
    }

    /**
     * 设置 TopBar 右侧图标按钮及监听
     */
    protected fun setTopEndIcon(resId: Int, listener: View.OnClickListener) {
        commonTopBar?.setEndIcon(resId)
        commonTopBar?.setEndIconListener(listener)
    }

    /**
     * 隐藏空态
     */
    protected fun hideEmptyView() {
        emptyStatusLL.visibility = View.GONE
    }

    /**
     * 显示 zhan
     */
    protected fun showEmptyNoData() {
        emptyStatusIV.setImageResource(R.drawable.ic_empty_data)
        emptyStatusLL.visibility = View.VISIBLE
    }

    /**
     * 显示请求失败
     */
    protected fun showEmptyFailed() {
        if (VMNetwork.hasNetwork()) {
            emptyStatusIV.setImageResource(R.drawable.ic_empty_failed)
        } else {
            emptyStatusIV.setImageResource(R.drawable.ic_empty_network)
        }
        emptyStatusLL.visibility = View.VISIBLE
    }


    override fun onDestroy() {
        mDialog?.dismiss()
        super.onDestroy()
    }
}