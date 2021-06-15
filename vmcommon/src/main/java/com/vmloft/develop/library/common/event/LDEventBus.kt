package com.vmloft.develop.library.common.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * Create by lzan13 on 2020/4/12 19:51
 * 描述：统一处理基于 LiveData 事件总线
 */
object LDEventBus {

    fun init() {
        LiveEventBus.config()
                // 配置LifecycleObserver（如Activity）接收消息的模式（默认值true）：
                // true：整个生命周期（从onCreate到onDestroy）都可以实时收到消息
                // false：激活状态（Started）可以实时收到消息，非激活状态（Stoped）无法实时收到消息，需等到Activity重新变成激活状态，方可收到消息
                .lifecycleObserverAlwaysActive(false)
                // 配置在没有Observer关联的时候是否自动清除LiveEvent以释放内存（默认值false）
                .autoClear(true)
        // 配置JsonConverter（默认使用gson）
        // .setJsonConverter()
        // 配置Logger（默认使用DefaultLogger）
        // setLogger
        // 配置是否打印日志（默认打印日志）
        // enableLogger
        // 如果广播模式有问题，请手动传入Context，需要在application onCreate中配置
        // setContext

    }

    /**
     * 发送事件
     *
     * @param key 事件 key
     * @param value 事件值
     * @param delay 事件延迟发送事件 毫秒值
     */
    fun post(key: String, value: Any, delay: Long = 0) {
        if (delay == 0L) {
            LiveEventBus.get(key, Any::class.java).post(value)
        } else {
            LiveEventBus.get(key, Any::class.java).postDelay(value, delay)
        }
    }

    /**
     * 发送有序事件
     *
     * @param key 事件 key
     * @param value 事件值
     */
    fun postOrderly(key: String, value: Any) {
        LiveEventBus.get(key, Any::class.java).postOrderly(value);
    }

    /**
     * 订阅基于生命周期的观察者
     * @param owner 生命周期所有者
     * @param key 订阅事件 key
     * @param clazz 订阅事件类型
     * @param observer 订阅事件回调
     */
    fun <T> observe(owner: LifecycleOwner, key: String, clazz: Class<T>, observer: Observer<T>) {
        LiveEventBus.get(key, clazz).observe(owner, observer)
    }

    /**
     * 订阅粘性的基于生命周期的观察者
     * @param owner 生命周期所有者
     * @param key 订阅事件 key
     * @param clazz 订阅事件类型
     * @param observer 订阅事件回调
     */
    fun <T> observeSticky(owner: LifecycleOwner, key: String, clazz: Class<T>, observer: Observer<T>) {
        LiveEventBus.get(key, clazz).observeSticky(owner, observer)
    }

}