package com.vmloft.develop.app.match.common;

/**
 * Create by lzan13 on 2019/5/20 23:16
 *
 * 自定义异常类
 */
public class AException extends Exception {

    public static final int NO_ERROR = 0;
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


    // 网络不可用
    public static final int SYS_NETWORK = 101;

    // 超时
    public static final int SYS_TIMEOUT = 102;

    /**
     * 这些错误和服务器返回的操作错误码相对应
     */
    // 服务器问题
    public static final int SERVER = 1000;
    public static final int SERVER_DB = 1001;
    public static final int INVALID_PARAM = 1002;

    // 权限错误
    public static final int TOKEN_INVALID = 2000;
    public static final int TOKEN_EXPIRED = 2001;
    public static final int NOT_PERMISSION = 2002;

    // 账户错误
    public static final int ACCOUNT_EXIST = 3000;
    public static final int ACCOUNT_NAME_EXIST = 3001;
    public static final int ACCOUNT_NOT_EXIST = 3002;
    public static final int ACCOUNT_DELETED = 3003;
    public static final int ACCOUNT_NO_ACTIVATED = 3004;
    public static final int INVALID_ACTIVATE_LINK = 3005;
    public static final int INVALID_PASSWORD = 3006;

    /**
     * Default for non-checking.
     */
    private static final long serialVersionUID = 1L;

    protected int code = UNKNOWN;
    protected String desc = "";

    public AException() {
        super();
    }

    /**
     * 使用给定描述构建异常
     *
     * @param desc 异常描述
     */
    public AException(String desc) {
        super(desc);
    }

    /**
     * 通过制定错误码和异常描述构建异常
     *
     * @param code
     * @param desc
     */
    public AException(int code, String desc) {
        super(desc);
        this.code = code;
        this.desc = desc;
    }

    /**
     * Constructs an exception with the given description.
     *
     * @param desc  the exception's description.
     * @param cause the exception's cause.
     */
    public AException(String desc, Throwable cause) {
        super(desc);
        super.initCause(cause);
    }

    public String getDesc() {
        return this.desc;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
