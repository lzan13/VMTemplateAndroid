package com.vmloft.develop.library.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

import com.vmloft.develop.library.base.utils.CUtils
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.widget.VMTopBar


/**
 * Created by lzan13 on 2020/02/15 11:16
 * 描述：Fragment 基类
 */
abstract class BFragment<VB : ViewBinding> : Fragment() {

    // 公共控件
    protected var commonTopLL: View? = null
    protected var commonTopSpace: View? = null
    protected var commonTopBar: VMTopBar? = null

    protected var mDialog: CommonDialog? = null

    protected var isLoaded: Boolean = false

    // 是否隐藏顶部控件
    open var isHideTopSpace: Boolean = false

    // 是否居中显示标题
    open var isCenterTitle: Boolean = false

    // 是否设置黑色状态栏
    open var isDarkStatusBar: Boolean = true

    private lateinit var _binding: VB
    protected val mBinding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = initVB(inflater, parent)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
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
     * 装载 TopBar
     */
    private fun setupTopBar() {
        CUtils.setDarkMode(requireActivity(), isDarkStatusBar)

        commonTopLL = mBinding.root.findViewById(R.id.commonTopLL)
        commonTopBar = mBinding.root.findViewById(R.id.commonTopBar)
        commonTopSpace = mBinding.root.findViewById(R.id.commonTopSpace)
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
     * 设置 TopBar 右侧图标按钮及监听
     */
    protected fun setTopEndIcon(resId: Int, listener: View.OnClickListener) {
        commonTopBar?.setEndIcon(resId)
        commonTopBar?.setEndIconListener(listener)
    }

    override fun onDestroy() {
        mDialog?.dismiss()
        super.onDestroy()
    }
}