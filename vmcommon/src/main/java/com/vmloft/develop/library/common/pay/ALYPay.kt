package com.vmloft.develop.library.common.pay

import android.app.Activity

//import com.alipay.sdk.app.PayTask

/**
 * Create by lzan13 on 2021/11/17
 * 描述：支付宝支付相关封装类
 */
object ALYPay {
    /**
     * 发起支付
     * @param payInfo 支付信息 key=value 形式，多个参数中间用 & 分隔
     */
//    suspend fun pay(activity: Activity, payInfo: String): Map<String, String> {
//        val alipay = PayTask(activity)
//        /**
//         * 调用支付宝支付接口
//         * 第一个参数：app 支付请求参数字符串，主要包含商户的订单信息，key=value 形式，以&连接。
//         * 第二个参数：用户在商户 app 内部点击付款，是否需要一个 loading 做为在钱包唤起之前的过渡，这个值设置为 true，
//         * 将会在调用 pay 接口的时候直接唤起一个 loading，直到唤起 H5 支付页面或者唤起外部的钱包付款页面 loading 才消失。
//         */
//        val result: Map<String, String> = alipay.payV2(payInfo, true)
//        return result
//    }
}