package com.vmloft.develop.library.pay

import android.app.Activity
import com.alipay.sdk.app.EnvUtils

import com.alipay.sdk.app.PayTask
import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.tools.utils.logger.VMLog

/**
 * Create by lzan13 on 2021/11/17
 * 描述：支付宝支付相关封装类
 */
object ALYPay {

    /**
     * 发起支付
     * @param payInfo 支付信息 key=value 形式，多个参数中间用 & 分隔
     */
    suspend fun pay(activity: Activity, payInfo: String): RResult<String> {
        if (CSPManager.isDebug()) {
            EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX)
        }
        val alipay = PayTask(activity)

        /**
         * 调用支付宝支付接口
         * 第一个参数：app 支付请求参数字符串，主要包含商户的订单信息，key=value 形式，以&连接。
         * 第二个参数：用户在商户 app 内部点击付款，是否需要一个 loading 做为在钱包唤起之前的过渡，这个值设置为 true，
         * 将会在调用 pay 接口的时候直接唤起一个 loading，直到唤起 H5 支付页面或者唤起外部的钱包付款页面 loading 才消失。
         */
        /**
         * 9000 订单支付成功。
         * 8000 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态。
         * 4000 订单支付失败。
         * 5000 重复请求。
         * 6001 用户中途取消。
         * 6002 网络连接出错。
         * 6004 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态。
         * 其他 其他支付错误。
         */
        val payResult = alipay.payV2(payInfo, true)
        val code = payResult["resultStatus"]?.toInt() ?: -1
        VMLog.d("${code}  ${payResult["result"]} ${payResult["memo"]} ")
        return if (code == 9000) {
            RResult.Success<Nothing>()
        } else {
            val msg = if (code == 8000) {
                "支付结束，可稍后查看支付结果"
            } else if (code == 4000) {
                "支付失败，请稍后重试"
            } else if (code == 5000) {
                "重复请求"
            } else if (code == 6001) {
                "支付已取消"
            } else if (code == 6002) {
                "网络连接出错"
            } else if (code == 6004) {
                "支付结束，可稍后查看支付结果"
            } else {
                "支付失败，请稍后重试"
            }
            RResult.Error(code, msg)
        }
    }
}