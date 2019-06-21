package com.vmloft.develop.library.im.chat;

import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.common.IMException;
import com.vmloft.develop.library.im.utils.IMChatUtils;
import com.vmloft.develop.library.im.utils.IMUtils;
import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public void init() {
        // 将会话加载到内存，因为这个必须要登录之后才能加载，这里只是登录过才有效
        EMClient.getInstance().chatManager().loadAllConversations();
        EMClient.getInstance().chatManager().addMessageListener(new IMChatListener());

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(IM.getInstance().getIMContext());

        // 各种消息广播接收器
        IntentFilter filter = new IntentFilter(IMUtils.Action.getNewMessageAction());
        filter.addAction(IMUtils.Action.getCMDMessageAction());

        IMChatReceiver chatReceiver = new IMChatReceiver();
        lbm.registerReceiver(chatReceiver, filter);
    }

    /**
     * 获取全部会话，并进行排序
     */
    public List<EMConversation> getAllConversation() {
        Map<String, EMConversation> map = EMClient.getInstance().chatManager().getAllConversations();
        List<EMConversation> list = new ArrayList<>();
        list.addAll(map.values());
        // 排序
        Collections.sort(list, (EMConversation o1, EMConversation o2) -> {
            if (IMChatUtils.getConversationLastTime(o1) > IMChatUtils.getConversationLastTime(o2)) {
                return -1;
            } else if (IMChatUtils.getConversationLastTime(o1) < IMChatUtils.getConversationLastTime(o2)) {
                return 1;
            }
            return 0;
        });

        // 排序之后，重新将置顶的条目设置到顶部
        List<EMConversation> result = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (IMChatUtils.getConversationTop(list.get(i))) {
                result.add(count, list.get(i));
                count++;
            } else {
                result.add(list.get(i));
            }
        }
        return result;
    }

    /**
     * 根据会话 id 获取会话
     *
     * @param id 会话 id
     */
    public EMConversation getConversation(String id) {
        return EMClient.getInstance().chatManager().getConversation(id);
    }

    /**
     * 根据会话 id 获取会话
     *
     * @param id       会话 id
     * @param chatType 会话类型
     */
    public EMConversation getConversation(String id, int chatType) {
        EMConversation.EMConversationType conversationType = IMChatUtils.wrapConversationType(chatType);
        // 为空时创建会话
        return EMClient.getInstance().chatManager().getConversation(id, conversationType, true);
    }

    /**
     * 删除会话
     *
     * @param id 会话 id
     */
    public void removeConversation(String id) {
        removeConversation(id, false);
    }

    /**
     * 删除会话
     *
     * @param id  会话 id
     * @param msg 是否一起删除消息
     */
    public void removeConversation(String id, boolean msg) {
        EMClient.getInstance().chatManager().deleteConversation(id, msg);
    }

    /**
     * 清空会话，这里会删除内存和数据库的数据
     *
     * @param id 会话 id
     */
    public void clearConversation(String id) {
        clearConversation(id, false);
    }

    /**
     * 清空会话，这里会删除内存和数据库的数据
     *
     * @param id 会话 id
     * @param db 是否删除数据库数据
     */
    public void clearConversation(String id, boolean db) {
        EMConversation conversation = getConversation(id);
        if (conversation != null) {
            if (db) {
                conversation.clearAllMessages();
            } else {
                conversation.clear();
            }
        }
    }

    /**
     * 清空未读数
     */
    public void clearUnreadCount(String id, int chatType) {
        EMConversation conversation = getConversation(id, chatType);
        conversation.markAllMessagesAsRead();
        IMChatUtils.setConversationUnread(conversation, false);
    }

    /**
     * 获取当前会话总消息数量
     */
    public int getMessagesCount(String id, int chatType) {
        EMConversation conversation = getConversation(id, chatType);
        return conversation.getAllMsgCount();
    }

    /**
     * 获取会话已加载所有消息
     */
    public List<EMMessage> getCacheMessages(String id, int chatType) {
        EMConversation conversation = getConversation(id, chatType);
        return conversation.getAllMessages();
    }

    /**
     * 获取缓存中的图片消息，主要用来预览图片
     */
    public List<EMMessage> getCachePictureMessage(String id, int chatType) {
        List<EMMessage> result = new ArrayList<>();
        for (EMMessage msg : getCacheMessages(id, chatType)) {
            if (msg.getType() == EMMessage.Type.IMAGE) {
                result.add(msg);
            }
        }
        return result;
    }

    /**
     * 获取当前会话的所有消息
     */
    public List<EMMessage> loadMoreMessages(String id, int chatType, String msgId) {
        EMConversation conversation = getConversation(id, chatType);
        if (conversation != null) {
            return conversation.loadMoreMsgFromDB(msgId, IMConstants.IM_CHAT_MSG_LIMIT);
        }
        return null;
    }

    /**
     * 获取指定消息，此操作不会将消息加入到内存列表
     *
     * @param id    会话 id
     * @param msgId 消息 id
     * @return
     */
    public EMMessage getMessage(String id, String msgId) {
        EMConversation conversation = getConversation(id);
        if (conversation == null) {
            return null;
        }
        return conversation.getMessage(msgId, false);
    }

    /**
     * 获取最后一条消息，这回将这条消息加入到内存
     *
     * @param id       会话 id
     * @param chatType 会话类型
     * @return
     */
    public EMMessage getLastMessage(String id, int chatType) {
        EMConversation conversation = getConversation(id, chatType);
        if (conversation == null) {
            return null;
        }
        return conversation.getLastMessage();
    }

    /**
     * 获取消息位置
     */
    public int getPosition(EMMessage message) {
        EMConversation conversation = getConversation(message.conversationId(), message.getChatType().ordinal());
        return conversation.getAllMessages().indexOf(message);
    }

    /**
     * 获取会话草稿
     */
    public String getDraft(EMConversation conversation) {
        return IMChatUtils.getConversationDraft(conversation);
    }

    /**
     * 获取会话草稿
     */
    public void setDraft(EMConversation conversation, String draft) {
        IMChatUtils.setConversationDraft(conversation, draft);
    }

    /**
     * 获取会话时间
     */
    public long getTime(EMConversation conversation) {
        return IMChatUtils.getConversationLastTime(conversation);
    }

    /**
     * 获取会话草稿
     */
    public void setTime(EMConversation conversation, long time) {
        IMChatUtils.setConversationLastTime(conversation, time);
    }

    /**
     * 获取未读状态
     */
    public boolean isUnread(EMConversation conversation) {
        return IMChatUtils.getConversationUnread(conversation);
    }

    /**
     * 设置未读
     */
    public void setUnread(EMConversation conversation, boolean unread) {
        IMChatUtils.setConversationUnread(conversation, unread);
    }

    /**
     * 获取置顶状态
     */
    public boolean isTop(EMConversation conversation) {
        return IMChatUtils.getConversationTop(conversation);
    }

    /**
     * 设置置顶
     */
    public void setTop(EMConversation conversation, boolean top) {
        IMChatUtils.setConversationTop(conversation, top);
    }

    /**
     * 设置会话扩展
     */
    public void setConversationExt(String id, int chatType, String key, Object value) {
        EMConversation conversation = getConversation(id, chatType);
        IMChatUtils.setConversationExt(conversation, key, value);
    }

    /**
     * 设置会话扩展
     */
    public Object getConversationExt(String id, int chatType, String key, Object value) {
        EMConversation conversation = getConversation(id, chatType);
        Object result = IMChatUtils.getConversationExt(conversation, key);
        return result == null ? value : result;
    }

    /**
     * ------------------------------------------ 消息相关 ------------------------------------------
     * 创建消息，默认支持以下类型
     * TXT, IMAGE, VIDEO, LOCATION, VOICE, FILE, CMD
     */

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
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.IM_MSG_EXT_NOTIFY, true);
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
            message = EMMessage.createImageSendMessage(path, true, id);
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.IM_MSG_EXT_NOTIFY, true);
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.IMAGE);
            message.addBody(new EMImageMessageBody(new File(path)));
            message.setFrom(id);
        }
        return message;
    }

    /**
     * 发送位置消息
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param address   位置
     * @param id        接收者
     * @param isSend    是否为发送消息
     */
    public EMMessage createLocationMessage(double latitude, double longitude, String address, String id, boolean isSend) {
        EMMessage message;
        if (isSend) {
            message = EMMessage.createLocationSendMessage(latitude, longitude, address, id);
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.IM_MSG_EXT_NOTIFY, true);
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.LOCATION);
            message.addBody(new EMLocationMessageBody(address, latitude, longitude));
            message.setFrom(id);
        }
        return message;
    }

    /**
     * 发送视频消息
     *
     * @param path      视频文件地址
     * @param thumbPath 视频缩略图地址
     * @param time      视频时长
     * @param id        接收者
     * @param isSend    是否为发送消息
     */
    public EMMessage createVideoMessage(String path, String thumbPath, int time, String id, boolean isSend) {
        EMMessage message;
        if (isSend) {
            message = EMMessage.createVideoSendMessage(path, thumbPath, time, id);
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.IM_MSG_EXT_NOTIFY, true);
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.LOCATION);
            message.addBody(new EMVideoMessageBody(path, thumbPath, time, 0l));
            message.setFrom(id);
        }
        return message;
    }

    /**
     * 发送语音消息
     *
     * @param path   语音文件的路径
     * @param time   语音持续时间
     * @param id     接收者
     * @param isSend 是否为发送消息
     */
    public EMMessage createVoiceMessage(String path, int time, String id, boolean isSend) {
        EMMessage message;
        if (isSend) {
            message = EMMessage.createVoiceSendMessage(path, time, id);
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.IM_MSG_EXT_NOTIFY, true);
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.LOCATION);
            message.addBody(new EMVoiceMessageBody(new File(path), time));
            message.setFrom(id);
        }
        return message;
    }

    /**
     * 发送文件消息
     *
     * @param path   要发送的文件的路径
     * @param id     接收者
     * @param isSend 是否为发送消息
     */
    public EMMessage createFileMessage(String path, String id, boolean isSend) {
        EMMessage message;
        if (isSend) {
            message = EMMessage.createFileSendMessage(path, id);
            // 默认所有消息都发送通知提醒，特殊情况特殊处理
            message.setAttribute(IMConstants.IM_MSG_EXT_NOTIFY, true);
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.LOCATION);
            message.addBody(new EMNormalFileMessageBody(new File(path)));
            message.setFrom(id);
        }
        return message;
    }

    /**
     * 发送 CMD 透传消息
     *
     * @param action 要发送 cmd 命令
     * @param id     接收者
     */
    public EMMessage createActionMessage(String action, String id) {
        // 根据文件路径创建一条文件消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setTo(id);
        // 创建CMD 消息的消息体 并设置 action
        EMCmdMessageBody body = new EMCmdMessageBody(action);
        message.addBody(body);
        return message;
    }

    /**
     * 保存消息
     */
    public void saveMessage(EMMessage message) {
        EMClient.getInstance().chatManager().saveMessage(message);
        // 保存会话时间
        setTime(getConversation(message.conversationId(), message.getChatType().ordinal()), message.localTime());
    }

    /**
     * 删除消息
     */
    public void removeMessage(EMMessage message) {
        getConversation(message.conversationId(), message.getChatType().ordinal()).removeMessage(message.getMsgId());
    }

    /**
     * 最终调用发送信息方法
     *
     * @param message  需要发送的消息
     * @param callback 发送结果回调接口
     */
    public void sendMessage(final EMMessage message, final IMCallback<EMMessage> callback) {
        if (!IM.getInstance().isSignIn()) {
            callback.onError(IMException.NO_SIGN_IN, "未登录，无法发送消息");
            return;
        }
        setTime(getConversation(message.conversationId(), message.getChatType().ordinal()), message.localTime());

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
     * 发送消息已读 ACK
     */
    public void sendReadACK(EMMessage message) {
        try {
            EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    /**
     * --------------------------- 发送扩展类消息 ---------------------------
     */
    /**
     * 发送一条撤回消息的透传，这里需要和接收方协商定义，通过一个透传，并加上扩展去实现消息的撤回
     *
     * @param message  需要撤回的消息
     * @param callBack 发送消息的回调，通知调用方发送撤回消息的结果
     */
    public void sendRecallMessage(EMMessage message, final EMCallBack callBack) {
        boolean result = false;
        // 获取当前时间，用来判断后边撤回消息的时间点是否合法，这个判断不需要在接收方做，
        // 因为如果接收方之前不在线，很久之后才收到消息，将导致撤回失败
        long currTime = VMDate.currentMilli();
        long msgTime = message.getMsgTime();
        // 判断当前消息的时间是否已经超过了限制时间，如果超过，则不可撤回消息
        if (currTime < msgTime || (currTime - msgTime > IMConstants.IM_RECALL_LIMIT_TIME)) {
            callBack.onError(IMException.COMMON, VMStr.byRes(R.string.im_error_recall_limit_time));
            return;
        }
        // 获取消息 id，作为撤回消息的参数
        String msgId = message.getMsgId();
        // 创建一个CMD 类型的消息，将需要撤回的消息通过这条CMD消息发送给对方
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        // 判断下消息类型，如果是群聊就设置为群聊
        if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            cmdMessage.setChatType(EMMessage.ChatType.GroupChat);
        }
        // 设置消息接收者
        cmdMessage.setTo(message.getTo());
        // 创建CMD 消息的消息体 并设置 action 为 recall
        String action = IMConstants.IM_MSG_ACTION_RECALL;
        EMCmdMessageBody body = new EMCmdMessageBody(action);
        cmdMessage.addBody(body);
        // 设置消息的扩展为要撤回的 msgId
        cmdMessage.setAttribute(IMConstants.IM_CHAT_MSG_ID, msgId);
        // 确认无误，开始发送撤回消息的透传
        cmdMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                callBack.onSuccess();
            }

            @Override
            public void onError(int i, String s) {
                callBack.onError(i, s);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
        // 准备工作完毕，发送消息
        EMClient.getInstance().chatManager().sendMessage(cmdMessage);
    }

    /**
     * 收到撤回消息，这里需要和发送方协商定义，通过一个透传，并加上扩展去实现消息的撤回
     *
     * @param cmdMessage 收到的透传消息，包含需要撤回的消息的 msgId
     * @return 返回撤回结果是否成功
     */
    public boolean receiveRecallMessage(EMMessage cmdMessage) {
        boolean result = false;
        // 从cmd扩展中获取要撤回消息的id
        String msgId = cmdMessage.getStringAttribute(IMConstants.IM_CHAT_MSG_ID, null);
        if (msgId == null) {
            VMLog.d("recall - 3 %s", msgId);
            return result;
        }
        // 根据得到的msgId 去本地查找这条消息，如果本地已经没有这条消息了，就不用撤回
        EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
        if (message == null) {
            VMLog.d("recall - 3 message is null %s", msgId);
            return result;
        }

        // 设置扩展为撤回消息类型，是为了区分消息的显示
        message.setAttribute(IMConstants.IM_MSG_ACTION_RECALL, IMConstants.MsgType.IM_RECALL);
        // 更新消息
        result = EMClient.getInstance().chatManager().updateMessage(message);
        return result;
    }

    /**
     * 发送输入状态，这里通过cmd消息来进行发送，告知对方自己正在输入
     *
     * @param to 接收方的名字
     */
    public void sendInputStatusMessage(String to) {
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMessage.setTo(to);
        // 创建CMD 消息的消息体 并设置 action 为输入状态
        EMCmdMessageBody body = new EMCmdMessageBody(IMConstants.IM_MSG_ACTION_INPUT);
        cmdMessage.addBody(body);
        // 确认无误，开始表示发送输入状态的透传
        EMClient.getInstance().chatManager().sendMessage(cmdMessage);
    }

    /**
     * 发送联系人信息修改消息
     */
    public void sendContactInfoChange() {
        List<EMConversation> list = getAllConversation();
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        // 创建CMD 消息的消息体 并设置 action
        EMCmdMessageBody body = new EMCmdMessageBody(IMConstants.IM_MSG_ACTION_CONTACT_CHANGE);
        cmdMessage.addBody(body);
        for (EMConversation conversation : list) {
            cmdMessage.setTo(conversation.conversationId());
            EMClient.getInstance().chatManager().sendMessage(cmdMessage);
        }
    }
}
