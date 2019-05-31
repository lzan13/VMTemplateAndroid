package com.vmloft.develop.library.im.common;

/**
 * Create by lzan13 on 2019/5/20 23:16
 *
 * 自定义异常类
 */
public class IMException extends Exception {

    // 通用错误
    public static final int COMMON = -1;
    // 未登录
    public static final int NO_SIGN_IN = -2;
    /**
     * Default for non-checking.
     */
    private static final long serialVersionUID = 1L;

    protected int code = -1;
    protected String desc = "";

    public IMException() {
        super();
    }

    /**
     * 使用给定描述构建异常
     *
     * @param desc 异常描述
     */
    public IMException(String desc) {
        super(desc);
    }

    /**
     * 通过制定错误码和异常描述构建异常
     *
     * @param code
     * @param desc
     */
    public IMException(int code, String desc) {
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
    public IMException(String desc, Throwable cause) {
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
