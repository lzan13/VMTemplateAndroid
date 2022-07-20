package com.vmloft.develop.app.template.ui.trade

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.databinding.ActivityOrderDetailBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.data.bean.Order
import com.vmloft.develop.library.data.viewmodel.TradeViewModel
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2021/8/11
 * 描述：订单详情相关界面
 */
@Route(path = AppRouter.appOrderDetail)
class OrderDetailActivity : BVMActivity<ActivityOrderDetailBinding, TradeViewModel>() {

    @Autowired(name = CRouter.paramsObj0)
    lateinit var order: Order

    override fun initVB() = ActivityOrderDetailBinding.inflate(layoutInflater)

    override fun initVM(): TradeViewModel = getViewModel()

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.order_detail)

        mBinding.submitTV.setOnClickListener { submit() }
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        bindInfo()
    }


    /**
     * 提交支付
     */
    private fun submit() {
        mViewModel.orderPay(this, order.id)
    }

    /**
     * 绑定信息
     */
    private fun bindInfo() {
        mBinding.orderTitleTV.text = order.title
        mBinding.orderPriceTV.text = (order.realPrice / 100f).toString()
        mBinding.orderIdTV.text = VMStr.byResArgs(R.string.order_id, order.id)
        mBinding.orderCreateTimeTV.text = VMStr.byResArgs(R.string.order_create_time, FormatUtils.defaultTime(order.createdAt))
        mBinding.orderUpdateTimeTV.text = VMStr.byResArgs(R.string.order_update_time, FormatUtils.defaultTime(order.updatedAt))

        mBinding.submitTV.isEnabled = order.status == 0
        when (order.status) {
            0 -> mBinding.submitTV.text = VMStr.byRes(R.string.order_status_0)
            1 -> mBinding.submitTV.text = VMStr.byRes(R.string.order_status_1)
            2 -> mBinding.submitTV.text = VMStr.byRes(R.string.order_status_2)
            3 -> mBinding.submitTV.text = VMStr.byRes(R.string.order_status_3)
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "orderPay") {
            // 支付成功 重新查询下订单信息
            mViewModel.orderInfo(order.id)
            showBar(R.string.order_pay_complete)
        }
        if (model.type == "orderInfo") {
            order = model.data as Order
            bindInfo()
            LDEventBus.post(Constants.Event.orderStatus, order)
        }
    }
}