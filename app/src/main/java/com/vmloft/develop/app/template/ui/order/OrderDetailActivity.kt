package com.vmloft.develop.app.template.ui.order

import android.content.ComponentName
import android.content.Intent

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivityOrderDetailBinding
import com.vmloft.develop.app.template.request.bean.Order
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BActivity
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.tools.VMTools
import com.vmloft.develop.library.tools.utils.VMSystem

import org.json.JSONObject

/**
 * Create by lzan13 on 2021/8/11
 * 描述：订单详情相关界面
 */
@Route(path = AppRouter.appOrderDetail)
class OrderDetailActivity : BActivity<ActivityOrderDetailBinding>() {

    var wechatQR: String = ""
    var wechatScheme: String = ""
    var alipayQR: String = ""
    var alipayScheme: String = ""
    var aliPayUrl: String = ""

    var payType: Int = 1

    @Autowired(name = CRouter.paramsObj0)
    lateinit var order: Order

    override fun initVB() = ActivityOrderDetailBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.order_detail)


        mBinding.orderPriceCopyTV.setOnClickListener { copyPrice() }

        mBinding.orderRechargeWechatTV.setOnClickListener { changePayType(0) }
        mBinding.orderRechargeAlipayTV.setOnClickListener { changePayType(1) }

        mBinding.submitTV.setOnClickListener { submit() }
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        parseInfo()

        bindInfo()
    }


    /**
     * 复制价格到剪切板，方便后续支付
     */
    private fun copyPrice() {
        VMSystem.copyToClipboard(order.realPrice)
        showBar(R.string.order_copy_price_hint)
    }

    /**
     * 提交支付
     */
    private fun submit() {
        when (payType) {
            0 -> openWechatScan()
            1 -> CRouter.goWeb(alipayScheme, true)
            else -> errorBar("不支持的支付类型")
        }
    }

    /**
     * 打开微信扫一扫
     */
    private fun openWechatScan() {
        val intent = Intent()
        intent.component = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
        intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = "android.intent.action.VIEW"
        VMTools.context.startActivity(intent)
    }

    /**
     * 解析信息，因为订单扩展里边包含了支付二维码和支付宝跳转链接，需要单独解析出来
     * 扩展信息
     * {
     *  "wxCode":"http://zyphoto.itluntan.cn/20210809161857",
     *  "zfbCode":"http://zyphoto.itluntan.cn/20210809161957",
     *  "zfbScheme":"alipayqr://platformapi/startapp?saId=10000007&qrcode=https%3A%2F%2Fqr.alipay.com%2Ftsx14575pzorxigphwtdj84",
     *  "afbPayUrl":"https://admin.zhanzhangfu.com/common/zfbuserid?zfbuserid=2088602250620913&price=8.00"
     * }
     */
    private fun parseInfo() {
        val jsonObject = JSONObject(order.extend)

        wechatQR = jsonObject.optString("wxCode", "")
        wechatScheme = "weixin://scanqrcode"

        alipayQR = jsonObject.optString("zfbCode", "")
        alipayScheme = jsonObject.optString("zfbScheme", "")
        aliPayUrl = jsonObject.optString("afbPayUrl", "")
    }

    /**
     * 绑定信息
     */
    private fun bindInfo() {
        mBinding.orderTitleTV.text = order.title
        mBinding.orderPriceTV.text = order.realPrice

        // 默认选中支付宝支付
        changePayType(payType)
    }

    /**
     * 改变支付类型
     */
    private fun changePayType(type: Int) {
        payType = type
        mBinding.orderRechargeWechatTV.isSelected = payType == 0
        mBinding.orderRechargeAlipayTV.isSelected = payType == 1

        if (payType == 0) {
            IMGLoader.loadCover(mBinding.orderRechargeQRIV, wechatQR)
        } else {
            IMGLoader.loadCover(mBinding.orderRechargeQRIV, alipayQR)
        }
    }

}