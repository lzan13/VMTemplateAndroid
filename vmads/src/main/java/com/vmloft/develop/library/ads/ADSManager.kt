package com.vmloft.develop.library.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.ATSDK
import com.anythink.core.api.AdError
import com.anythink.nativead.api.*
import com.anythink.nativead.unitgroup.api.CustomNativeAd
import com.anythink.rewardvideo.api.ATRewardVideoAd
import com.anythink.rewardvideo.api.ATRewardVideoListener
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdExtraInfo
import com.anythink.splashad.api.ATSplashAdListener
import com.anythink.splashad.api.IATSplashEyeAd
import com.vmloft.develop.library.base.common.CSPManager

import com.vmloft.develop.library.tools.utils.logger.VMLog
import com.vmloft.develop.library.tools.utils.VMDimen


/**
 * Create by lzan13 on 2020/4/13 22:05
 * 描述：广告管理类
 */
object ADSManager {

    // 开屏广告对象
    private var atSplashAD: ATSplashAd? = null
    private var splashListener: ATSplashAdListener? = null
    private var splashCallback: (Int) -> Unit = {}

    // 原生广告对象
    private var atNativeAD: ATNative? = null
    private var nativeListener: ATNativeNetworkListener? = null
    private var nativeCallback: (Int) -> Unit = {}

    // 激励视频广告对象
    private var atVideoAD: ATRewardVideoAd? = null
    private var videoListener: ATRewardVideoListener? = null
    private var videoCallback: (Int) -> Unit = {}

    /**
     * 初始化广告
     */
    @SuppressLint("MissingPermission")
    fun init(context: Context) {
        // 调用聚合平台初始化
        if (CSPManager.isDebug()) {
            ATSDK.setNetworkLogDebug(CSPManager.isDebug())
            ATSDK.setBiddingTestDevice("973b4a9e170dc1bc")
            ATSDK.integrationChecking(context)
            if (ATSDK.isCnSDK()) {
                VMLog.i("当前 SDK 为中国区")
            } else {
                VMLog.i("当前 SDK 为海外区")
            }
        }
        ATSDK.init(context, ADSConstants.topOnAppId(), ADSConstants.topOnAppKey())

        setupSplashAD()

        setupNativeAD()

        setupVideoAD()
    }

    /**
     * ------------------------------- 开屏广告 -------------------------------
     */
    private fun setupSplashAD() {
        splashListener = object : ATSplashAdListener {
            /**
             * 广告加载成功回调
             * @param isTimeout：广告加载成功时，是否超过fetchAdTimeout指定时间
             */
            override fun onAdLoaded(isTimeout: Boolean) {
                VMLog.i("ADSManager onAdLoaded timeout:$isTimeout")
                splashCallback.invoke(if (isTimeout) ADSConstants.Status.timeout else ADSConstants.Status.loaded)
            }

            /**
             * 广告加载超时回调，可在此处进行开屏广告加载超时处理，注意：
             * （1）广告加载超时回调后，开屏广告仍在加载，如果加载成功会触发onAdLoaded(true)
             * （2）如果本次加载的开屏广告没有被展示，会放到缓存中供下次展示
             */
            override fun onAdLoadTimeout() {
                VMLog.e("ADSManager onAdLoadTimeout")
                splashCallback.invoke(ADSConstants.Status.timeout)
            }

            /**
             * 广告加载失败回调，可通过AdError.getFullErrorInfo()获取全部错误信息，请参考 AdError
             * 注意：禁止在此回调中执行广告的加载方法进行重试，否则会引起很多无用请求且可能会导致应用卡顿
             */
            override fun onNoAdError(error: AdError) {
                VMLog.e("ADSManager onNoAdError ${error.fullErrorInfo}")
                splashCallback.invoke(ADSConstants.Status.failed)
            }

            /**
             * 广告展示回调
             * ATAdInfo：广告的信息对象，可区分广告平台，主要包含第三方聚合平台的id信息 见ATAdInfo信息说明
             */
            override fun onAdShow(info: ATAdInfo) {
                VMLog.i("ADSManager onAdShow")
                splashCallback.invoke(ADSConstants.Status.show)
            }

            /**
             * 广告点击回调 参数意义同上
             */
            override fun onAdClick(info: ATAdInfo) {
                VMLog.i("ADSManager onAdClick")
                splashCallback.invoke(ADSConstants.Status.click)
            }
            /**
             * 广告关闭回调 参数意义同上
             * splashEyeAd：开屏点睛广告控制接口类，开发者可通过此接口控制展示点睛广告 注意：
             * （1）当穿山甲、优量汇的开屏广告素材支持点睛时，splashEyeAd不为null
             * （2）当展示的是快手开屏广告时，splashEyeAd为非null值，但不一定表示此次快手开屏广告的素材支持点睛，不支持时调用IATSplashEyeAd#show()方法会直接回调ATSplashEyeAdListener#onAdDismiss()方法
             * （3）当splashEyeAd不为null，但是开发者不想支持点睛功能时，必须调用splashEyeAd.destroy()释放资源，然后跳转主页面或者移除开屏View
             */
            override fun onAdDismiss(info: ATAdInfo, extraInfo: ATSplashAdExtraInfo?) {
                VMLog.i("ADSManager onAdDismiss")
                splashCallback.invoke(ADSConstants.Status.close)
                extraInfo?.atSplashEyeAd?.destroy()
            }
        }
    }

    /**
     * 加载开屏广告
     */
    fun loadSplashAD(activity: Activity, callback: (Int) -> Unit) {
        splashCallback = callback

        if (atSplashAD == null) {
            atSplashAD = ATSplashAd(activity, ADSConstants.ADSIds.adsSplashId, splashListener)
        }
        if (atSplashAD!!.isAdReady) {
            splashCallback.invoke(ADSConstants.Status.loaded)
        } else {
            atSplashAD!!.loadAd()
        }
    }

    /**
     * 显示开屏广告
     */
    fun showSplashAD(activity: Activity, container: ViewGroup) {
        atSplashAD!!.show(activity, container)
    }

    /**
     * ------------------------------- 原生广告 -------------------------------
     */
    private fun setupNativeAD() {
        nativeListener = object : ATNativeNetworkListener {

            /**
             * 广告加载成功回调
             */
            override fun onNativeAdLoaded() {
                splashCallback.invoke(ADSConstants.Status.loaded)
            }

            /**
             * 广告加载失败回调
             */
            override fun onNativeAdLoadFail(error: AdError) {
                VMLog.e("ADSManager onNoAdError ${error.fullErrorInfo}")
                nativeCallback.invoke(ADSConstants.Status.failed)
            }
        }
    }

    /**
     * 加载原生广告
     */
    fun loadNativeAD(activity: Activity, callback: (Int) -> Unit) {
        nativeCallback = callback

        if (atNativeAD == null) {
            atNativeAD = ATNative(activity, ADSConstants.ADSIds.adsNativeId, nativeListener)
        }
        if (atNativeAD!!.checkAdStatus().isReady) {
            nativeCallback.invoke(ADSConstants.Status.loaded)
        } else {
            val space: Int = VMDimen.dp2px(4)
            val adsWidth = (VMDimen.screenWidth - space * 3) / 2
            val adsHeight = adsWidth * 9 / 16

            val params: MutableMap<String, Any> = HashMap()
            params[ATAdConst.KEY.AD_WIDTH] = adsWidth
            params[ATAdConst.KEY.AD_HEIGHT] = adsHeight

            atNativeAD!!.setLocalExtra(params)
            atNativeAD!!.makeAdRequest()
        }
    }

    /**
     * 显示原生广告
     */
    fun showNativeAD(context: Context, container: ViewGroup) {
        val nativeAd = atNativeAD!!.nativeAd
        if (nativeAd == null) {
            atNativeAD!!.makeAdRequest()
            return
        }

        var nativeADView = ATNativeAdView(context)
        container.addView(nativeADView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))

        val adsRender = CustomNativeRender()
        nativeAd.renderAdView(nativeADView, adsRender)
        nativeAd.prepare(nativeADView, adsRender.getClickView(), null);
    }

    /**
     * ------------------------------- 激励视频 -------------------------------
     */
    /**
     * 装载激励视频广告
     */
    private fun setupVideoAD() {
        // 设置广告监听
        videoListener = object : ATRewardVideoListener {
            /**
             * 加载完成
             */
            override fun onRewardedVideoAdLoaded() {
                VMLog.i("onRewardedVideoAdLoaded")
                videoCallback.invoke(ADSConstants.Status.loaded)
            }

            /**
             * 加载失败 参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_test?id=aderror
             */
            override fun onRewardedVideoAdFailed(error: AdError) {
                VMLog.e("onRewardedVideoAdFailed: ${error.fullErrorInfo}")
                videoCallback.invoke(ADSConstants.Status.failed)
            }

            /**
             * 广告开始播放回调
             * ATAdInfo：广告的信息对象，可区分广告平台，主要包含第三方聚合平台的id信息
             * 见 ATAdInfo信息说明 https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info
             */
            override fun onRewardedVideoAdPlayStart(info: ATAdInfo) {
                VMLog.i("onRewardedVideoAdPlayStart")
                videoCallback.invoke(ADSConstants.Status.playStart)
            }

            /**
             * 播放结束 参数同上
             */
            override fun onRewardedVideoAdPlayEnd(info: ATAdInfo) {
                VMLog.i("anythink onRewardedVideoAdPlayEnd")
                videoCallback.invoke(ADSConstants.Status.playEnd)
            }

            /**
             * 播放失败 参数同上
             */
            override fun onRewardedVideoAdPlayFailed(error: AdError, info: ATAdInfo) {
                VMLog.i("anythink onRewardedVideoAdPlayFailed: ${error.fullErrorInfo}")
                videoCallback.invoke(ADSConstants.Status.playFailed)
            }

            /**
             * 关闭 建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady()）
             * 参数同上
             */
            override fun onRewardedVideoAdClosed(info: ATAdInfo) {
                VMLog.i("anythink onRewardedVideoAdClosed")
                videoCallback.invoke(ADSConstants.Status.close)
                // 关闭后加载下一次广告数据
                atVideoAD!!.load()
            }

            /**
             * 播放点击 参数同上
             */
            override fun onRewardedVideoAdPlayClicked(info: ATAdInfo) {
                // 播放点击
                VMLog.i("anythink onRewardedVideoAdPlayClicked")
                videoCallback.invoke(ADSConstants.Status.click)
            }

            /**
             * 结束回调 建议在此回调中下发奖励，一般在onRewardedVideoAdClosed之前回调 参数同上
             */
            override fun onReward(info: ATAdInfo) {
                VMLog.i("anythink onReward  $info")
                videoCallback.invoke(ADSConstants.Status.reward)
            }
        }
    }

    /**
     * 加载激励视频广告
     */
    fun loadVideoAD(activity: Activity, userParams: Map<String, String>, callback: (Int) -> Unit) {
        videoCallback = callback

        if (atVideoAD == null) {
            atVideoAD = ATRewardVideoAd(activity, ADSConstants.ADSIds.adsVideoId)
            // 设置广告监听
            atVideoAD!!.setAdListener(videoListener)
            // 设置用户信息，用于向服务器回调
            atVideoAD!!.setLocalExtra(userParams)
        }

        if (atVideoAD!!.isAdReady) {
            videoCallback.invoke(ADSConstants.Status.loaded)
        } else {
            atVideoAD!!.load()
        }
    }

    /**
     * 展示激励视频广告
     */
    fun showVideoAD(activity: Activity) {
        atVideoAD!!.show(activity)
    }

    /**
     * 展示激励视频广告
     */
    fun closeVideoAD() {
        videoCallback = {}
    }
}
