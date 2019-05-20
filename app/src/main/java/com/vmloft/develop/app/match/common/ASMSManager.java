package com.vmloft.develop.app.match.common;

import com.vmloft.develop.app.match.base.ACallback;

/**
 * Create by lzan13 on 2019/5/9 13:47
 *
 * 短信验操作管理类
 */
public class ASMSManager {

    /**
     * 私有构造，初始化 ShredPreferences 文件名
     */
    private ASMSManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class ASMSManagerHolder {
        private static final ASMSManager INSTANCE = new ASMSManager();
    }

    /**
     * 获取的实例
     */
    public static final ASMSManager getInstance() {
        return ASMSManagerHolder.INSTANCE;
    }

    /**
     * 统一回调注册方法
     *
     * @param callback 操作回调
     */
    private void commonCallback(final ACallback callback) {
//        EventHandler eventHandler = new EventHandler() {
//            @Override
//            public void afterEvent(int event, int result, Object data) {
//                if (callback == null) {
//                    return;
//                }
//                if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
//                    if (result == SMSSDK.RESULT_COMPLETE) {
//                        VMLog.d("获取支持国家代码成功" + data);
//                        callback.onSuccess(data);
//                    } else {
//                        VMLog.d("请求验证码失败: %s", ((Throwable) data).getMessage());
//                        callback.onError(AError.UNKNOWN, ((Throwable) data).getMessage());
//                    }
//                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//                    if (result == SMSSDK.RESULT_COMPLETE) {
//                        VMLog.d("请求验证成功");
//                        callback.onSuccess(data);
//                    } else {
//                        VMLog.d("请求验证码失败: %s", ((Throwable) data).getMessage());
//                        callback.onError(AError.UNKNOWN, ((Throwable) data).getMessage());
//                    }
//                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
//                    if (result == SMSSDK.RESULT_COMPLETE) {
//                        VMLog.d("验证验证码成功");
//                        callback.onSuccess(data);
//                    } else {
//                        VMLog.d("验证验证码失败: %s", ((Throwable) data).getMessage());
//                        callback.onError(AError.UNKNOWN, ((Throwable) data).getMessage());
//                    }
//                }
//                // 执行完成置空
//                SMSSDK.unregisterEventHandler(this);
//            }
//        };
//        // 注册一个事件回调，用于处理 SMSSDK 接口请求的结果
//        SMSSDK.registerEventHandler(eventHandler);
    }

    /**
     * 获取支持的国家
     *
     * @param callback 操作回调
     */
    public void getSupportedCountries(ACallback callback) {
        commonCallback(callback);
//        SMSSDK.getSupportedCountries();
    }

    /**
     * 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
     *
     * @param country  国家代码 中国 86
     * @param phone    手机号
     * @param callback 操作回调
     */
    public void getVerificationCode(String country, String phone, ACallback callback) {
        commonCallback(callback);
//        SMSSDK.getVerificationCode(country, phone);
    }

    /**
     * 提交验证码，其中的code表示验证码，如“1357”
     *
     * @param country  国家代码 中国 86
     * @param phone    手机号
     * @param code     验证码
     * @param callback 操作回调
     */
    public void submitVerificationCode(String country, String phone, String code, ACallback callback) {
        commonCallback(callback);
//        SMSSDK.submitVerificationCode(country, phone, code);
    }
}
