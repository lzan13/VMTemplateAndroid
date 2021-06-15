package com.vmloft.develop.library.common.ui.web

import android.webkit.WebView
import android.widget.LinearLayout

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient
import com.vmloft.develop.library.common.R

import com.vmloft.develop.library.common.base.BaseActivity
import com.vmloft.develop.library.common.router.CRouter
import kotlinx.android.synthetic.main.activity_web.*


/**
 * Create by lzan13 on 2020/05/02 15:56
 * 描述：Web 界面
 */
@Route(path = CRouter.commonWeb)
class WebActivity : BaseActivity() {

    @Autowired
    lateinit var url: String

    private lateinit var mAgentWeb: AgentWeb

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.vm_loading)
    }

    override fun layoutId(): Int = R.layout.activity_web

    override fun initData() {
        ARouter.getInstance().inject(this)

        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(webContainer, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .setWebChromeClient(chromeClient)
            .createAgentWeb()
            .ready()
            .go(url)
    }

    /**
     * WebView 回调
     */
    private val chromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            setTopTitle(title)
        }
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