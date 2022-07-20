package com.vmloft.develop.library.common.web

import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.LinearLayout

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient

import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.common.R
import com.vmloft.develop.library.common.databinding.ActivityWebBinding
import com.vmloft.develop.library.tools.utils.VMColor

/**
 * Create by lzan13 on 2020/05/02 15:56
 * 描述：Web 界面
 */
@Route(path = CRouter.commonWeb)
class WebActivity : BActivity<ActivityWebBinding>() {

    @Autowired
    lateinit var url: String

    private lateinit var mAgentWeb: AgentWeb

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.vm_loading)

        initWebView()
    }

    override fun initVB() = ActivityWebBinding.inflate(layoutInflater)

    override fun initData() {
        ARouter.getInstance().inject(this)

        setupUrl()

        mAgentWeb.urlLoader.loadUrl(url)
    }

    private fun initWebView() {
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(mBinding.webContainer, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator(VMColor.byRes(R.color.app_main), 1)
            .setWebChromeClient(chromeClient)
            .createAgentWeb()
            .ready()
            .go("")
        mAgentWeb.webCreator.webParentLayout.setBackgroundResource(R.color.app_bg)
    }

    /**
     * WebView 回调
     */
    private val chromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            setTopTitle(title)
        }
    }

    private fun setupUrl() {
        val uri: Uri = Uri.parse(url)
        val fullScreen = uri.getBooleanQueryParameter("fullScreen", false)
        val hideTitle = uri.getBooleanQueryParameter("hideTitle", false)
        val orientation = uri.getQueryParameter("orientation") ?: "1"

        if (fullScreen) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        requestedOrientation = if (orientation == "0") ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        commonTopLL?.visibility = if (hideTitle) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        mAgentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onPause() {
        mAgentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mAgentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }
}