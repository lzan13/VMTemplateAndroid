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
        int IM_UNKNOWN = 0x00;
        // 系统消息
        int IM_SYSTEM = 0x01;
        // 撤回
        int IM_RECALL = 0x02;

        // 文本
        int IM_TEXT_RECEIVE = 0x10;
        int IM_TEXT_SEND = 0x11;
        // 图片
        int IM_IMAGE_RECEIVE = 0x20;
        int IM_IMAGE_SEND = 0x21;
        // 视频
        int IM_VIDEO_RECEIVE = 0x30;
        int IM_VIDEO_SEND = 0x31;
        // 定位
        int IM_LOCATION_RECEIVE = 0x40;
        int IM_LOCATION_SEND = 0x41;
        // 语音
        int IM_VOICE_RECEIVE = 0x50;
        int IM_VOICE_SEND = 0x51;
        // 文件
        int IM_FILE_RECEIVE = 0x60;
        int IM_FILE_SEND = 0x61;
    }

    /**
     * 消息扩展类型
     */
    public interface MsgExtType {
        // 通话
        int IM_CALL = 0x100;
        int IM_CALL_RECEIVE = 0x101;
        int IM_CALL_SEND = 0x102;

        // 大表情
        int IM_BIG_EMOTION = 0x110;
        int IM_BIG_EMOTION_RECEIVE = 0x111;
        int IM_BIG_EMOTION_SEND = 0x112;
    }

    /**
     * CMD 消息 action
     */
    // 输入状态
    public static final String IM_MSG_ACTION_INPUT = "im_msg_action_input";
    // 撤回
    public static final String IM_MSG_ACTION_RECALL = "im_msg_action_recall";
    // 骰子
    public static final String IM_MSG_ACTION_DICE = "im_msg_action_dice";
    // 石头剪刀布
    public static final String IM_MSG_ACTION_SJB = "im_msg_action_sjb";
    // 通话表情
    public static final String IM_MSG_ACTION_EMOTION = "im_msg_action_emotion";
    // 联系人信息改变
    public static final String IM_MSG_ACTION_CONTACT_CHANGE = "im_msg_action_contact_change";

    /**
     * 消息扩展
     */
    // 扩展类型
    public static final String IM_MSG_EXT_TYPE = "im_chat_msg_ext_type";
    // 是否发送通知栏提醒
    public static final String IM_MSG_EXT_NOTIFY = "im_chat_msg_ext_notify";
    // 是否为视频通话
    public static final String IM_MSG_EXT_VIDEO_CALL = "im_chat_msg_video_call";
    // 骰子扩展
    public static final String IM_MSG_EXT_DICE_INDEX = "im_msg_ext_dice_index";
    // 石头剪刀布扩展
    public static final String IM_MSG_EXT_SJB_INDEX = "im_msg_ext_sjb_index";
    // 大表情扩展信息
    public static final String IM_MSG_EXT_EMOTION_GROUP = "im_msg_ext_emotion_group";
    public static final String IM_MSG_EXT_EMOTION_DESC = "im_msg_ext_emotion_desc";
    public static final String IM_MSG_EXT_EMOTION_URL = "im_msg_ext_emotion_url";
    public static final String IM_MSG_EXT_EMOTION_GIF_URL = "im_msg_ext_emotion_gif_url";

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
