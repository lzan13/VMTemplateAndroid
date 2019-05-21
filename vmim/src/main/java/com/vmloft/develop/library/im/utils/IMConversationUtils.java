package com.vmloft.develop.library.im.utils;


import android.text.TextUtils;

import com.hyphenate.chat.EMConversation;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.utils.VMDate;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzan13 on 2015/12/14 15:27.
 */
public class IMConversationUtils {

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
    }

}
