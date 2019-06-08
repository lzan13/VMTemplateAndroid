package com.vmloft.develop.library.im.utils;

import android.text.TextUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.PathUtil;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.common.IMException;
import com.vmloft.develop.library.tools.utils.VMCrypto;
import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzan13 on 2016/3/22.
 * 消息处理工具类，主要做谢谢EMMessage对象的处理
 */
public class IMChatUtils {

    /**
     * 发送一条撤回消息的透传，这里需要和接收方协商定义，通过一个透传，并加上扩展去实现消息的撤回
     *
     * @param message  需要撤回的消息
     * @param callBack 发送消息的回调，通知调用方发送撤回消息的结果
     */
    public static void sendRecallMessage(EMMessage message, final EMCallBack callBack) {
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
    public static boolean receiveRecallMessage(EMMessage cmdMessage) {
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
    public static void sendInputStatusMessage(String to) {
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMessage.setTo(to);
        // 创建CMD 消息的消息体 并设置 action 为输入状态
        EMCmdMessageBody body = new EMCmdMessageBody(IMConstants.IM_MSG_ACTION_INPUT);
        cmdMessage.addBody(body);
        // 确认无误，开始表示发送输入状态的透传
        EMClient.getInstance().chatManager().sendMessage(cmdMessage);
    }

    /**
     * 获取图片消息的缩略图本地保存的路径
     *
     * @param fullSizePath 缩略图的原始路径
     * @return 返回本地路径
     */
    public static String getThumbImagePath(String fullSizePath) {
        String thumbImageName = VMCrypto.cryptoStr2SHA1(fullSizePath);
        String path = PathUtil.getInstance().getHistoryPath() + "/" + "thumb_" + thumbImageName;
        return path;
    }

    /**
     * 获取消息类型
     */
    public static int getMessageType(EMMessage message) {
        int extType = message.getIntAttribute(IMConstants.IM_MSG_EXT_TYPE, IMConstants.MsgType.IM_UNKNOWN);
        int itemType;
        if (extType == IMConstants.MsgExtType.IM_CALL) {
            // 通话
            itemType = message.direct() == EMMessage.Direct.RECEIVE ? IMConstants.MsgExtType.IM_CALL_RECEIVE :
                IMConstants.MsgExtType.IM_CALL_SEND;
        } else if (extType == IMConstants.MsgExtType.IM_BIG_EMOTION) {
            // 大表情
            itemType = message.direct() == EMMessage.Direct.RECEIVE ? IMConstants.MsgExtType.IM_BIG_EMOTION_RECEIVE :
                IMConstants.MsgExtType.IM_BIG_EMOTION_SEND;
        } else if (message.getType() == EMMessage.Type.TXT) {
            // 文本
            itemType = message.direct() == EMMessage.Direct.RECEIVE ? IMConstants.MsgType.IM_TEXT_RECEIVE :
                IMConstants.MsgType.IM_TEXT_SEND;
        } else if (message.getType() == EMMessage.Type.IMAGE) {
            // 图片
            itemType = message.direct() == EMMessage.Direct.RECEIVE ? IMConstants.MsgType.IM_IMAGE_RECEIVE :
                IMConstants.MsgType.IM_IMAGE_SEND;
        } else if (message.getType() == EMMessage.Type.VIDEO) {
            // 视频
            itemType = message.direct() == EMMessage.Direct.RECEIVE ? IMConstants.MsgType.IM_VIDEO_RECEIVE :
                IMConstants.MsgType.IM_TEXT_SEND;
        } else if (message.getType() == EMMessage.Type.LOCATION) {
            // 位置
            itemType = message.direct() == EMMessage.Direct.RECEIVE ? IMConstants.MsgType.IM_LOCATION_RECEIVE :
                IMConstants.MsgType.IM_LOCATION_SEND;
        } else if (message.getType() == EMMessage.Type.VOICE) {
            // 语音
            itemType = message.direct() == EMMessage.Direct.RECEIVE ? IMConstants.MsgType.IM_VOICE_RECEIVE :
                IMConstants.MsgType.IM_VOICE_SEND;
        } else if (message.getType() == EMMessage.Type.FILE) {
            // 文件
            itemType = message.direct() == EMMessage.Direct.RECEIVE ? IMConstants.MsgType.IM_FILE_RECEIVE :
                IMConstants.MsgType.IM_FILE_SEND;
        } else {
            // 未知，显示提示文本
            itemType = IMConstants.MsgType.IM_UNKNOWN;
        }
        return itemType;
    }

    /**
     * 获取消息摘要信息
     */
    public static String getSummary(EMMessage message) {
        String content = VMStr.byRes(R.string.im_unknown_msg);
        int type = getMessageType(message);
        /**
         * 通知类消息
         */
        if (type == IMConstants.MsgType.IM_SYSTEM) {
            // TODO 系统提醒
        } else if (type == IMConstants.MsgType.IM_RECALL) {
            // 撤回消息
            content = "[" + VMStr.byRes(R.string.im_recall_already) + "]";
        }
        /**
         * 扩展类消息
         */
        else if (type == IMConstants.MsgExtType.IM_CALL_RECEIVE || type == IMConstants.MsgExtType.IM_CALL_SEND) {
            // 通话消息
            content = "[" + VMStr.byRes(R.string.im_call) + " - " + ((EMTextMessageBody) message.getBody()).getMessage() + "]";
        } else if (type == IMConstants.MsgExtType.IM_BIG_EMOTION_RECEIVE || type == IMConstants.MsgExtType.IM_BIG_EMOTION_SEND) {
            // 大表情
            content = ((EMTextMessageBody) message.getBody()).getMessage();
        }
        /**
         * 普通类消息
         */
        else if (type == IMConstants.MsgType.IM_TEXT_RECEIVE || type == IMConstants.MsgType.IM_TEXT_SEND) {
            content = ((EMTextMessageBody) message.getBody()).getMessage();
        } else if (type == IMConstants.MsgType.IM_IMAGE_RECEIVE || type == IMConstants.MsgType.IM_IMAGE_SEND) {
            // 图片消息
            content = "[" + VMStr.byRes(R.string.im_picture) + "]";
        } else if (type == IMConstants.MsgType.IM_VOICE_RECEIVE || type == IMConstants.MsgType.IM_VOICE_SEND) {
            content = "[" + VMStr.byRes(R.string.im_voice) + "]";
        } else {
            // 未知类型消息
            content = "[" + VMStr.byRes(R.string.im_unknown_msg) + "]";
        }
        return content;
    }

    /**
     * 包装会话类型
     */
    public static EMConversation.EMConversationType wrapConversationType(int chatType) {
        if (chatType == IMConstants.ChatType.IM_SINGLE_CHAT) {
            return EMConversation.EMConversationType.Chat;
        } else if (chatType == IMConstants.ChatType.IM_GROUP_CHAT) {
            return EMConversation.EMConversationType.GroupChat;
        } else if (chatType == IMConstants.ChatType.IM_CHAT_ROOM) {
            return EMConversation.EMConversationType.ChatRoom;
        }
        return EMConversation.EMConversationType.Chat;
    }

    /**
     * 包装聊天类型
     */
    public static EMMessage.ChatType wrapChatType(int chatType) {
        if (chatType == IMConstants.ChatType.IM_SINGLE_CHAT) {
            return EMMessage.ChatType.Chat;
        } else if (chatType == IMConstants.ChatType.IM_GROUP_CHAT) {
            return EMMessage.ChatType.GroupChat;
        } else if (chatType == IMConstants.ChatType.IM_CHAT_ROOM) {
            return EMMessage.ChatType.ChatRoom;
        }
        return EMMessage.ChatType.Chat;
    }

    /**
     * -------------------------------------- 会话扩展 --------------------------------------
     */
    private static JSONObject getExtObject(EMConversation conversation) throws JSONException {
        // 获取当前会话对象的扩展
        String ext = conversation.getExtField();
        JSONObject extObject = null;
        // 判断扩展内容是否为空
        if (TextUtils.isEmpty(ext)) {
            extObject = new JSONObject();
        } else {
            extObject = new JSONObject(ext);
        }
        return extObject;
    }

    /**
     * 设置当前会话草稿
     *
     * @param conversation 需要设置的会话对象
     * @param draft        需要设置的草稿内容
     */
    public static void setConversationDraft(EMConversation conversation, String draft) {
        try {
            JSONObject extObject = getExtObject(conversation);
            // 将扩展信息设置给 JSONObject 对象
            extObject.put(IMConstants.IM_CONVERSATION_KEY_DRAFT, draft);
            // 将扩展信息保存到 EMConversation 对象扩展中去
            conversation.setExtField(extObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前会话的草稿内容
     *
     * @param conversation 当前会话
     * @return 返回草稿内容
     */
    public static String getConversationDraft(EMConversation conversation) {
        try {
            JSONObject extObject = getExtObject(conversation);
            // 根据扩展的key获取扩展的值
            return extObject.optString(IMConstants.IM_CONVERSATION_KEY_DRAFT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设置会话置顶状态
     *
     * @param conversation 要置顶的会话对象
     * @param top          设置会话是否置顶
     */
    public static void setConversationTop(EMConversation conversation, boolean top) {
        try {
            JSONObject extObject = getExtObject(conversation);
            // 将扩展信息设置给外层的 JSONObject 对象
            extObject.put(IMConstants.IM_CONVERSATION_KEY_TOP, top);
            // 将扩展信息保存到 Conversation 对象的扩展中去
            conversation.setExtField(extObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前会话是否置顶
     *
     * @param conversation 需要操作的会话对象
     * @return 返回当前会话是否置顶
     */
    public static boolean getConversationTop(EMConversation conversation) {
        try {
            JSONObject extObject = getExtObject(conversation);
            return extObject.optBoolean(IMConstants.IM_CONVERSATION_KEY_TOP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 标记会话为未读状态
     *
     * @param conversation 需要标记的会话
     * @param unread       设置未读状态
     */
    public static void setConversationUnread(EMConversation conversation, boolean unread) {
        try {
            JSONObject extObject = getExtObject(conversation);
            // 将扩展信息设置给 JSONObject 对象
            extObject.put(IMConstants.IM_CONVERSATION_KEY_UNREAD, unread);
            // 将扩展信息保存到 EMConversation 对象扩展中去
            conversation.setExtField(extObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 conversation 对象扩展中的未读状态
     *
     * @param conversation 当前会话
     * @return 返回未读状态
     */
    public static boolean getConversationUnread(EMConversation conversation) {
        try {
            JSONObject extObject = getExtObject(conversation);
            // 根据扩展的key获取扩展的值
            return extObject.optBoolean(IMConstants.IM_CONVERSATION_KEY_UNREAD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置会话的最后时间
     *
     * @param conversation 要设置的会话对象
     */
    public static void setConversationLastTime(EMConversation conversation, long lastTime) {
        try {
            JSONObject extObject = getExtObject(conversation);
            extObject.put(IMConstants.IM_CONVERSATION_KEY_LAST_TIME, lastTime);
            // 将扩展信息保存到 Conversation 对象的扩展中去
            conversation.setExtField(extObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取会话的最后时间
     *
     * @param conversation 需要获取的会话对象
     * @return 返回此会话最后的时间
     */
    public static long getConversationLastTime(EMConversation conversation) {
        try {
            JSONObject extObject = getExtObject(conversation);
            // 根据扩展的key获取扩展的值
            return extObject.optLong(IMConstants.IM_CONVERSATION_KEY_LAST_TIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return VMDate.currentMilli();
    }// ---------------------------------------- 会话扩展结束 ----------------------------------------
}
