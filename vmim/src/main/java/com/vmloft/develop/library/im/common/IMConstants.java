package com.vmloft.develop.library.im.common;

/**
 * Create by lzan13 on 2019/5/21 15:32
 *
 * IM 常量类
 */
public class IMConstants {

    /**
     * 聊天界面传参
     */
    public static final String IM_CHAT_KEY_ID = "im_chat_key_id";

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
    // 消息id
    public static final String ATTR_MSG_ID = "attr_msg_id";
    // 理由
    public static final String ATTR_REASON = "attr_reason";
    // 撤回
    public static final String ATTR_RECALL = "attr_recall";
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
