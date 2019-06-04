package com.vmloft.develop.library.im.common;

/**
 * Create by lzan13 on 2019/5/21 15:32
 *
 * IM 常量类
 */
public class IMConstants {

    // 时间常量
    public static final long IM_TIME_SECOND = 1000; // 秒
    public static final long IM_TIME_MINUTE = IM_TIME_SECOND * 60; // 分
    public static final long IM_TIME_HOUR = IM_TIME_MINUTE * 60; // 时
    public static final long IM_TIME_DAY = IM_TIME_HOUR * 24; // 天
    // 消息允许撤回时间 3 分钟
    public static final long IM_RECALL_LIMIT_TIME = IM_TIME_MINUTE * 3;
    // 输入状态检测时间
    public static final long TIME_INPUT_STATUS = IM_TIME_SECOND * 5;

    // 分页拉取限制
    public static final int IM_CHAT_MSG_LIMIT = 20;

    /**
     * 聊天类型
     */
    public interface ChatType {
        int IM_SINGLE_CHAT = 0;
        int IM_GROUP_CHAT = 1;
        int IM_CHAT_ROOM = 2;
    }

    /**
     * 消息类型
     */
    public interface MsgType {
        // 未知类型
        int IM_UNKNOWN = 0;
        // 系统消息
        int IM_SYSTEM = 1;
        // 撤回
        int IM_RECALL = 2;
        // 通话消息
        int IM_CALL = 3;

        // 文本
        int IM_TEXT_RECEIVE = 10;
        int IM_TEXT_SEND = 11;
        int IM_IMAGE_RECEIVE = 12;
        int IM_IMAGE_SEND = 13;
        int IM_VIDEO_RECEIVE = 14;
        int IM_VIDEO_SEND = 15;
        int IM_LOCATION_RECEIVE = 16;
        int IM_LOCATION_SEND = 17;
        int IM_VOICE_RECEIVE = 18;
        int IM_VOICE_SEND = 19;
        int IM_FILE_RECEIVE = 20;
        int IM_FILE_SEND = 21;
    }

    /**
     * 消息扩展类型
     */
    public interface MsgExtType {
        // 通话
        int IM_CALL = 0x10;
        int IM_CALL_RECEIVE = 0x11;
        int IM_CALL_SEND = 0x12;

        // 大表情
        int IM_BIG_EMOTION = 0x20;
        int IM_BIG_EMOTION_RECEIVE = 0x21;
        int IM_BIG_EMOTION_SEND = 0x22;
    }

    // 消息扩展类型
    public static final String IM_CHAT_MSG_EXT_TYPE = "im_chat_msg_ext_type";
    public static final String IM_CHAT_MSG_EXT_TYPE_VIDEO_CALL = "im_chat_msg_type_video_call";

    /**
     * CMD 消息 action
     */
    // 输入状态
    public static final String IM_CHAT_ACTION_INPUT = "im_chat_action_input";
    public static final String IM_CHAT_ACTION_RECALL = "im_chat_action_recall";

    // 传递聊天 Id
    public static final String IM_CHAT_ID = "im_chat_id";
    // 传递聊天类型
    public static final String IM_CHAT_TYPE = "im_chat_type";
    // 传递消息
    public static final String IM_CHAT_MSG = "im_chat_msg";
    // 传递消息 Id
    public static final String IM_CHAT_MSG_ID = "im_chat_msg_id";
    // 通话时间
    public static final String IM_CHAT_CALL_TIME = "im_chat_call_time";

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

    // at(@)
    public static final String ATTR_AT = "attr_at";
    // 阅后即焚
    public static final String ATTR_BURN = "attr_burn";
    // 群组id
    public static final String ATTR_GROUP_ID = "attr_group_id";
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
