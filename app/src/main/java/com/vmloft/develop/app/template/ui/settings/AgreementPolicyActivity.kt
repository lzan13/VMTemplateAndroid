package com.vmloft.develop.app.template.ui.settings

import android.view.View
import android.widget.LinearLayout

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.just.agentweb.AgentWeb

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivitySettingsAgreementPolicyBinding
import com.vmloft.develop.app.template.request.bean.Config
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.tools.utils.VMStr

import kotlinx.android.synthetic.main.activity_settings_agreement_policy.*

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2021/7/11
 * 描述：展示用户政策及隐私协议
 */
@Route(path = AppRouter.appSettingsAgreementPolicy)
class AgreementPolicyActivity : BVMActivity<SettingsViewModel>() {

    @Autowired
    lateinit var type: String

    private lateinit var mAgentWeb: AgentWeb

    override fun initVM(): SettingsViewModel = getViewModel()

    override fun layoutId(): Int = R.layout.activity_settings_agreement_policy

    override fun initUI() {
        super.initUI()
        (mBinding as ActivitySettingsAgreementPolicyBinding).viewModel = mViewModel
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        setTopTitle(if (type == "agreement") VMStr.byRes(R.string.user_agreement) else VMStr.byRes(R.string.privacy_policy))

        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(webContainer, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
//            .setWebChromeClient(chromeClient)
            .createAgentWeb()
            .ready()
            .go("")
        if (type == "agreement") {
            mViewModel.getUserAgreement()
        } else {
            mViewModel.getPrivacyPolicy()
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "userAgreement" || model.type == "privacyPolicy") {
            if (model.data == null) {
                showEmpty()
            } else {
                val config = model.data as Config
                showContent(config.content)
            }
        }
    }

    /**
     * 协议与政策内容
     */
    private fun showContent(content: String) {
        webEmptyIV.visibility = View.GONE
        webContainer.visibility = View.VISIBLE
        if (content.indexOf("http") == 0) {
            mAgentWeb.urlLoader.loadUrl(content)
        } else {
            mAgentWeb.urlLoader.loadData(content, "text/html", "utf-8")
        }
    }

    /**
     * 显示空视图
     */
    private fun showEmpty() {
        webEmptyIV.visibility = View.VISIBLE
        webContainer.visibility = View.GONE
    }

    /**
     * WebView 回调
     */
//    private val chromeClient: WebChromeClient = object : WebChromeClient() {
//        override fun onReceivedTitle(view: WebView, title: String) {
//            setTopTitle(title)
//        }
//    }

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