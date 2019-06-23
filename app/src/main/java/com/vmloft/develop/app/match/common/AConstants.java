package com.vmloft.develop.app.match.common;

/**
 * Create by lzan13 on 2019/04/22
 *
 * 项目常量类
 */
public class AConstants {

    public static final long TIME_SECOND = 1000;
    public static final long TIME_MINUTE = 60 * TIME_SECOND;
    public static final long TIME_HOUR = 60 * TIME_MINUTE;
    public static final long TIME_DAY = 24 * TIME_HOUR;
    public static final long TIME_WEEK = 7 * TIME_DAY;


    // 聊天扩展
    public static String CHAT_EXT_MATCH = "chat_ext_match";

    /**
     * 消息扩展类型
     */
    public interface MsgExt {
        String MSG_EXT_MATCH_FATE = "msg_ext_match_fate";

        // 消息类型
        String MSG_EXT_TYPE = "msg_ext_type";
        // 匹配消息
        int MSG_MATCH = 0x1000;
    }
}

