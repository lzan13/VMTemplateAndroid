package com.vmloft.develop.library.mqtt

import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.EventData
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.tools.VMTools
import com.vmloft.develop.library.tools.utils.logger.VMLog

import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback

import java.lang.Exception


/**
 * Create by lzan13 on 2022/3/22
 * 描述：MQTT 帮助类
 */
object MQTTHelper {

    // mqtt 链接所需 Token
    private var mqttToken: String = ""
    private var mqttClient: MqttAndroidClient? = null

    // 缓存主题集合
    private val topicList = mutableListOf<String>()

    /**
     * 链接MQTT
     * @param id 用户 Id
     * @param token 用户链接 MQTT 的 Token
     * @param topic 需要订阅的主题，不为空就会在连接成功后进行订阅
     */
    fun connect(id: String, token: String, topic: String = "") {
        if(token.isNullOrEmpty()) return
        mqttToken = token
        // 处理订阅主题
        if (topic.isNotEmpty()) topicList.add(topic)

        // 拼接链接地址
        val url = "tcp://${MQTTConstants.mqttHost()}:${MQTTConstants.mqttPort()}"
        // 拼接 clientId
        val clientId = "${id}@${MQTTConstants.mqttAppId()}"
        mqttClient = MqttAndroidClient(VMTools.context, url, clientId)

        //连接参数
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true //设置自动重连
        options.isCleanSession = true // 缓存
        options.connectionTimeout = CConstants.timeMinute.toInt() // 设置超时时间，单位：秒
        options.keepAliveInterval = CConstants.timeMinute.toInt() // 心跳包发送间隔，单位：秒
        options.userName = id // 用户名
        options.password = mqttToken.toCharArray() // 密码
        options.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1;
        // 设置MQTT监听
        mqttClient?.setCallback(object : MqttCallback {
            override fun connectionLost(t: Throwable) {
                // 通知链接断开
                VMLog.d("MQTT 链接断开 $t")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                // 通知收到消息
                VMLog.d("MQTT 收到消息:$message")
                // 如果未订阅则直接丢弃
                if (!topicList.contains(topic)) return
                notifyEvent(topic, String(message.payload))
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {}
        })
        //进行连接
        mqttClient?.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(token: IMqttToken) {
                VMLog.d("MQTT 链接成功")
                // 链接成功，循环订阅缓存的主题
                topicList.forEach { subscribe(it) }
            }

            override fun onFailure(token: IMqttToken, t: Throwable) {
                VMLog.d("MQTT 链接失败 $t")
            }
        })
    }

    /**
     * 订阅主题
     * @param topic 主题
     */
    fun subscribe(topic: String) {
        if (!topicList.contains(topic)) {
            topicList.add(topic)
        }
        try {
            //连接成功后订阅主题
            mqttClient?.subscribe(topic, 0, null, object : IMqttActionListener {
                override fun onSuccess(token: IMqttToken) {
                    VMLog.d("MQTT 订阅成功 $topic")
                }

                override fun onFailure(token: IMqttToken, t: Throwable) {
                    VMLog.d("MQTT 订阅失败 $topic $t")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    /**
     * 取消订阅
     * @param topic 主题
     */
    fun unsubscribe(topic: String) {
        if (topicList.contains(topic)) {
            topicList.remove(topic)
        }
        try {
            mqttClient?.unsubscribe(topic)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    /**
     * 发送 MQTT 消息
     * @param topic 主题
     * @param content 内容
     */
    fun sendMsg(topic: String, content: String) {
        val msg = MqttMessage()
        msg.payload = content.encodeToByteArray() // 设置消息内容
        msg.qos = 0 //设置消息发送质量，可为0,1,2.
        // 设置消息的topic，并发送。
        mqttClient?.publish(topic, msg, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                VMLog.d("MQTT 消息发送成功")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                VMLog.d("MQTT 消息发送失败 ${exception.message}")
            }
        })
    }

    /**
     * 通知 MQTT 事件
     */
    private fun notifyEvent(topic: String, data: String) {
        LDEventBus.post(topic, data)
    }
}