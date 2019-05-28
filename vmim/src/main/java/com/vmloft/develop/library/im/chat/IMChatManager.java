package com.vmloft.develop.library.im.chat;

import android.content.Context;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.tools.utils.VMLog;

import java.io.File;

/**
 * Create by lzan13 on 2019/5/9 10:38
 *
 * IM 聊天管理类
 */
public class IMChatManager {

    /**
     * 私有的构造方法
     */
    private IMChatManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMChatManager INSTANCE = new IMChatManager();
    }

    /**
     * 获取单例类实例
     */
    public static IMChatManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    public void init(Context context) {

    }

    /**
     * 添加消息监听
     */
    public void addMessageListener() {

    }

    /**
     * 移除消息监听
     */
    public void removeMessageListener() {

    }

    /**
     * 最终调用发送信息方法
     *
     * @param message  需要发送的消息
     * @param callback 发送结果回调接口
     */
    public void sendMessage(final EMMessage message, final IMCallback<EMMessage> callback) {
        /**
         *  调用sdk的消息发送方法发送消息，发送消息时要尽早的设置消息监听，防止消息状态已经回调，
         *  但是自己没有注册监听，导致检测不到消息状态的变化
         *  所以这里在发送之前先设置消息的状态回调
         */
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                VMLog.i("消息发送成功 msgId %s - %s", message.getMsgId(), message.toString());
                if (callback != null) {
                    callback.onSuccess(message);
                }
            }

            @Override
            public void onError(int code, String desc) {
                VMLog.i("消息发送失败 [%d] - %s", code, desc);
                // 创建并发出一个可订阅的关于的消息事件
                if (callback != null) {
                    callback.onError(code, desc);
                }
            }

            @Override
            public void onProgress(int progress, String desc) {
                // TODO 消息发送进度，这里不处理，留给消息Item自己去更新
                VMLog.i("消息发送中 [%d] - %s", progress, desc);
                if (callback != null) {
                    callback.onProgress(progress, desc);
                }
            }
        });
        // 发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        // 发送一条新消息时插入新消息的位置，这里直接用插入新消息前的消息总数来作为新消息的位置
//        int position = conversation.getAllMessages().indexOf(message);
    }

    /**
     * 创建一条文本消息
     *
     * @param content 消息内容
     * @param toId    接收者
     * @param isSend  是否为发送消息
     */
    public EMMessage createTextMessage(String content, String toId, boolean isSend) {
        EMMessage message;
        if (isSend) {
            message = EMMessage.createTxtSendMessage(content, toId);
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.addBody(new EMTextMessageBody(content));
            message.setFrom(toId);
        }
        return message;
    }

    /**
     * 创建一条图片消息
     *
     * @param path   图片路径
     * @param id     接收者
     * @param isSend 是否为发送消息
     */
    public EMMessage createPictureMessage(String path, String id, boolean isSend) {
        EMMessage message;
        if (isSend) {
            message = EMMessage.createTxtSendMessage(path, id);
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.addBody(new EMImageMessageBody(new File(path)));
            message.setFrom(id);
        }
        return message;
    }

    /**
     * 发送图片消息
     *
     * @param path     要发送的图片的路径
     * @param toId     接收者
     * @param callback 发送结果回调接口
     */
    public void sendPicture(String path, String toId, IMCallback<EMMessage> callback) {
        EMMessage message = EMMessage.createImageSendMessage(path, true, toId);
        // 调用统一发送消息的方法，
        sendMessage(message, callback);
    }

    /**
     * 发送位置消息
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param address   位置
     * @param toId      接收者
     * @param callback  发送结果回调接口
     */
    public void sendLocation(double latitude, double longitude, String address, String toId, IMCallback<EMMessage> callback) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, address, toId);
        // 调用统一发送消息的方法，
        sendMessage(message, callback);
    }

    /**
     * 发送视频消息
     *
     * @param path      视频文件地址
     * @param thumbPath 视频缩略图地址
     * @param time      视频时长
     * @param toId      接收者
     * @param callback  发送结果回调接口
     */
    public void sendVideo(String path, String thumbPath, int time, String toId, IMCallback<EMMessage> callback) {
        EMMessage message = EMMessage.createVideoSendMessage(path, thumbPath, time, toId);
        // 调用统一发送消息的方法，
        sendMessage(message, callback);
    }

    /**
     * 发送语音消息
     *
     * @param path     语音文件的路径
     * @param time     语音持续时间
     * @param toId     接收者
     * @param callback 发送结果回调接口
     */
    public void sendVoice(String path, int time, String toId, IMCallback<EMMessage> callback) {
        EMMessage message = EMMessage.createVoiceSendMessage(path, time, toId);
        sendMessage(message, callback);
    }

    /**
     * 发送文件消息
     *
     * @param path     要发送的文件的路径
     * @param toId     接收者
     * @param callback 发送结果回调接口
     */
    public void sendFile(String path, String toId, IMCallback<EMMessage> callback) {
        // 根据文件路径创建一条文件消息
        EMMessage message = EMMessage.createFileSendMessage(path, toId);
        // 调用统一发送消息的方法，
        sendMessage(message, callback);
    }

    /**
     * 发送 CMD 透传消息
     *
     * @param action   要发送 cmd 命令
     * @param toId     接收者
     * @param callback 发送结果回调接口
     */
    public void sendAction(String action, String toId, IMCallback<EMMessage> callback) {
        // 根据文件路径创建一条文件消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setTo(toId);
        // 创建CMD 消息的消息体 并设置 action
        EMCmdMessageBody body = new EMCmdMessageBody(action);
        message.addBody(body);
        sendMessage(message, callback);
    }

    // TXT, IMAGE, VIDEO, LOCATION, VOICE, FILE, CMD
}
