package com.vmloft.develop.library.im.common;

/**
 * Create by lzan13 on 2019/5/21 15:32
 *
 * IM 常量类
 */
public class IMConstants {

    // 时间常量
    public static final long IM_TIME_MINUTE = 60 * 1000;
    public static final long IM_TIME_HOUR = 60 * 60 * 1000;
    public static final long IM_TIME_DAY = 24 * 60 * 60 * 1000;

    // 聊天界面传参
    public static final String IM_CHAT_ID = "im_chat_id";
    public static final String IM_CHAT_TYPE = "im_chat_type";


    // 分页拉取限制
    public static final int IM_CHAT_MSG_LIMIT = 20;

    /**
     * 聊天类型
     */
    public interface ChatType {
        public static final int IM_SINGLE_CHAT = 0;
        public static final int IM_GROUP_CHAT = 1;
        public static final int IM_CHAT_ROOM = 2;
    }

    /**
     * 普通消息类型
     */
    // 未知类型
    public static final int IM_CHAT_TYPE_UNKNOWN = 0;
    // 系统通知
    public static final int IM_CHAT_TYPE_SYSTEM = 1;
    // 撤回
    public static final int IM_CHAT_TYPE_RECALL = 2;
    // 文本
    public static final int IM_CHAT_TYPE_TEXT = 9;
    public static final int IM_CHAT_TYPE_TEXT_RECEIVE = 10;
    public static final int IM_CHAT_TYPE_TEXT_SEND = 11;
    public static final int IM_CHAT_TYPE_IMAGE_RECEIVE = 12;
    public static final int IM_CHAT_TYPE_IMAGE_SEND = 13;
    public static final int IM_CHAT_TYPE_VIDEO_RECEIVE = 14;
    public static final int IM_CHAT_TYPE_VIDEO_SEND = 15;
    public static final int IM_CHAT_TYPE_LOCATION_RECEIVE = 16;
    public static final int IM_CHAT_TYPE_LOCATION_SEND = 17;
    public static final int IM_CHAT_TYPE_VOICE_RECEIVE = 18;
    public static final int IM_CHAT_TYPE_VOICE_SEND = 19;
    public static final int IM_CHAT_TYPE_FILE_RECEIVE = 20;
    public static final int IM_CHAT_TYPE_FILE_SEND = 21;
    public static final int IM_CHAT_TYPE_CALL_RECEIVE = 22;
    public static final int IM_CHAT_TYPE_CALL_SEND = 23;

    /**
     * CMD 类型消息 action
     */
    // 输入状态
    public static final String IM_CHAT_ACTION_INPUT = "im_chat_action_input";


    /**
     * 定义会话与消息扩展字段 key
     * 包括会话{@link com.hyphenate.chat.EMConversation}扩展，
     * 以及消息{@link com.hyphenate.chat.EMMessage}扩展
     */
    // 草稿
    public static final String IM_CONVERSATION_KEY_DRAFT = "im_conversation_key_draft";
    // 最后时间
    public static final String IM_CONVERSATION_KEY_LAST_TIME = "im_conversation_key_last_time";
    // 置顶
    public static final String IM_CONVERSATION_KEY_TOP = "im_conversation_key_top";
    // 会话未读
    public static final String IM_CONVERSATION_KEY_UNREAD = "im_conversation_key_unread";
    // 消息扩展类型
    public static final String IM_CHAT_MSG_TYPE = "im_chat_msg_type";

    // at(@)
    public static final String ATTR_AT = "attr_at";
    // 阅后即焚
    public static final String ATTR_BURN = "attr_burn";
    // 群组id
    public static final String ATTR_GROUP_ID = "attr_group_id";
    // 消息id
    public static final String ATTR_MSG_ID = "attr_msg_id";
    // 理由
    public static final String ATTR_REASON = "attr_reason";
    // 撤回
    // 状态
    public static final String ATTR_STATUS = "attr_status";
    // 类型
    public static final String ATTR_TYPE = "attr_type";
    // 用户名
    public static final String ATTR_USERNAME = "attr_username";
    // 输入状态
    public static final String ATTR_INPUT_STATUS = "attr_input_status";
    // 视频通话扩展
    public static final String ATTR_CALL_VIDEO = "attr_call_video";
    // 语音通话扩展
    public static final String ATTR_CALL_VOICE = "attr_call_voice";
}
