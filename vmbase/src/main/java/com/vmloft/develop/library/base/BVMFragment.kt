package com.vmloft.develop.library.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

import com.vmloft.develop.library.base.utils.CUtils
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMNetwork
import com.vmloft.develop.library.tools.widget.VMTopBar

/**
 * Created by lzan13 on 2020/02/15 11:16
 * 描述：Fragment MVVM 框架基类
 */
abstract class BVMFragment<VB : ViewBinding, VM : BViewModel> : Fragment() {

    // 公共控件
    protected var commonTopLL: View? = null
    protected var commonTopSpace: View? = null
    protected var commonTopBar: VMTopBar? = null

    protected var commonLoadingLL: View? = null

    protected var emptyStatusLL: View? = null
    protected var emptyStatusIV: ImageView? = null

    protected var mDialog: Dialog? = null

    // 是否隐藏顶部控件
    open var isHideTopSpace: Boolean = false

    // 是否居中显示标题
    open var isCenterTitle: Boolean = false

    // 是否设置黑色状态栏
    open var isDarkStatusBar: Boolean = true

    protected var isLoaded: Boolean = false

    protected lateinit var mViewModel: VM

    private lateinit var _binding: VB
    protected val mBinding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = initVB(inflater, parent)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = initVM()
        initUI()
        startObserve()
    }


    override fun onResume() {
        super.onResume()
        if (!isLoaded) {
            isLoaded = true
            initData()
        }
//        ReportManager.reportPageStart(this.javaClass.simpleName)
    }

//    override fun onPause() {
//        super.onPause()
//        ReportManager.reportPageEnd(this.javaClass.simpleName)
//    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
//            ReportManager.reportPageEnd(this.javaClass.simpleName)
        } else {
            CUtils.setDarkMode(requireActivity(), isDarkStatusBar)
//            ReportManager.reportPageStart(this.javaClass.simpleName)
        }
    }


    /**
     * 初始化 ViewBinding
     */
    abstract fun initVB(inflater: LayoutInflater, parent: ViewGroup?): VB

    /**
     * 初始化 ViewModel
     */
    abstract fun initVM(): VM

    /**
     * 初始化 UI
     */
    open fun initUI() {
        setupTopBar()
    }

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 模型 loading 状态回调
     */
    open fun onModelLoading(model: BViewModel.UIModel) {
        if (model.isLoading) {
            showLoading()
        } else {
            hideLoading()
        }
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
        mViewModel.uiState.observe(viewLifecycleOwner) {
            onModelLoading(it)
            if (!it.isLoading) {
                if (it.isSuccess) {
                    onModelRefresh(it)
                } else {
                    onModelError(it)
                }
                it.toast?.let { message -> showBar(message) }
            }
        }
    }

    /**
     * 装载 TopBar
     */
    private fun setupTopBar() {
        CUtils.setDarkMode(requireActivity(), isDarkStatusBar)

        commonTopLL = mBinding.root.findViewById(R.id.commonTopLL)
        commonTopBar = mBinding.root.findViewById(R.id.commonTopBar)
        commonTopSpace = mBinding.root.findViewById(R.id.commonTopSpace)

        commonLoadingLL = mBinding.root.findViewById(R.id.commonLoadingLL)

        emptyStatusLL = mBinding.root.findViewById(R.id.emptyStatusLL)
        emptyStatusIV = mBinding.root.findViewById(R.id.emptyStatusIV)
        if (!isHideTopSpace) {
            // 设置状态栏透明主题时，布局整体会上移，所以给头部 View 设置 StatusBar 的高度
            commonTopSpace?.layoutParams?.height = VMDimen.statusBarHeight
        }
        commonTopBar?.setCenter(isCenterTitle)
        commonTopBar?.setTitleStyle(R.style.AppText_Title)
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
     * 设置图标颜色
     */
    protected fun setTopIconColor(color: Int) {
        commonTopBar?.setIconColor(color)
    }

    protected fun setTopIconListener(listener: View.OnClickListener) {
        commonTopBar?.setIconListener(listener)
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
        commonTopBar?.setTitleColor(resId)
    }

    /**
     * 设置子标题
     */
    protected fun setTopSubtitle(title: String?) {
        commonTopBar?.setSubtitle(title)
    }

    /**
     * 设置子标题
     */
    protected fun setTopEndView(view: View?) {
        commonTopBar?.addEndView(view)
    }

    /**
     * 显示 loading
     */
    protected fun showLoading() {
        commonLoadingLL?.visibility = View.VISIBLE
    }

    /**
     * 隐藏 loading
     */
    protected fun hideLoading() {
        commonLoadingLL?.visibility = View.GONE
    }

    /**
     * 隐藏空态
     */
    protected fun hideEmptyView() {
        emptyStatusLL?.visibility = View.GONE
    }

    /**
     * 显示没有数据
     */
    protected fun showEmptyNoData() {
        emptyStatusIV?.setImageResource(R.drawable.ic_empty_data)
        emptyStatusLL?.visibility = View.VISIBLE
    }

    /**
     * 显示请求失败
     */
    protected fun showEmptyFailed() {
        if (VMNetwork.hasNetwork()) {
            emptyStatusIV?.setImageResource(R.drawable.ic_empty_failed)
        } else {
            emptyStatusIV?.setImageResource(R.drawable.ic_empty_network)
        }
        emptyStatusLL?.visibility = View.VISIBLE
    }

    /**
     * 设置空态点击事件
     */
    protected fun setEmptyClick(listener: View.OnClickListener) {
        emptyStatusIV?.setOnClickListener { listener.onClick(it) }
    }

    override fun onDestroy() {
        mDialog?.dismiss()
        super.onDestroy()
    }
}
