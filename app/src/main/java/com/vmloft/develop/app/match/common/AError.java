package com.vmloft.develop.app.match.common;

/**
 * Create by lzan13 on 2019/04/15
 *
 * 定义错误相关错误码和统一错误信息
 */
public class AError {
    // 未知错误码
    public static final int UNKNOWN = -1;

    /**
     * 账户相关错误码
     */
    // 用户名已被占用
    public static final int USER_ALREADY_EXIST = 100;
    // 邮箱已被使用
    public static final int EMAIL_ALREADY_EXIST = 101;
    // 手机号已被占用
    public static final int PHONE_ALREADY_EXIST = 102;
    // 用户名密码不匹配
    public static final int USERNAME_PASSWORD_MISMATCH = 103;
    // 用户不存在
    public static final int USER_DOESNOT_EXIST = 104;
}
