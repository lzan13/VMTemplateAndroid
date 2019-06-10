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

    // LeanCloud id 和 key
    public static String APP_LC_ID = "j1AGx1iU48PGjyv1RcuQr0OX-gzGzoHsz";
    public static String APP_LC_KEY = "jwYileaj7c4FCU7L1SuAzUWR";

    /**
     * 消息扩展类型
     */
    public interface MsgExtType {
        String MSG_EXT_TYPE = "msg_ext_type";

        String MSG_EXT_MATCH = "msg_ext_match";
        // 匹配信息
        int IM_MATCH = 0x1000;
    }
}

