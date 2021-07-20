package com.vmloft.develop.library.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.vmloft.develop.library.common.R
import com.vmloft.develop.library.common.report.ReportManager
import com.vmloft.develop.library.common.widget.CommonDialog
import com.vmloft.develop.library.tools.utils.VMDimen

import kotlinx.android.synthetic.main.widget_common_top_bar.*


/**
 * Created by lzan13 on 2020/02/15 11:16
 * 描述：Fragment 基类
 */
abstract class BaseFragment : Fragment() {
    protected var mDialog: CommonDialog? = null
    protected var isLoaded: Boolean = false

    // 是否居中显示标题
    open var centerTitle: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUI()
    }

    override fun onResume() {
        super.onResume()
        if (!isLoaded) {
            isLoaded = true
            initData()
        }
        ReportManager.reportPageStart(this.javaClass.simpleName)
    }

    override fun onPause() {
        super.onPause()
        ReportManager.reportPageEnd(this.javaClass.simpleName)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            ReportManager.reportPageEnd(this.javaClass.simpleName)
        } else {
            ReportManager.reportPageStart(this.javaClass.simpleName)
        }
    }

    /**
     * 布局资源 id
     */
    abstract fun layoutId(): Int

    /**
     * 初始化 UI
     */
    open fun initUI() {
        setupTobBar()
    }

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 装载 TopBar
     */
    private fun setupTobBar() {
        // 设置状态栏透明主题时，布局整体会上移，所以给头部 View 设置 StatusBar 的高度
        commonTopSpace?.layoutParams?.height = VMDimen.statusBarHeight

        commonTopBar?.setCenter(centerTitle)
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