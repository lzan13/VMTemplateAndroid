package com.vmloft.develop.library.im.bean;

import com.hyphenate.chat.EMMessage;

import com.hyphenate.exceptions.HyphenateException;
import com.vmloft.develop.library.tools.utils.VMStr;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Create by lzan13 on 2019/5/21 15:11
 *
 * 自定义 IMMessage 消息对象
 */
public class IMMessage implements Serializable {

    // 内部消息对象
    private EMMessage mMessage;

    public IMMessage() {}

    public IMMessage(EMMessage message) {
        mMessage = message;
    }

    /**
     * 获取 boolean 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    public boolean getBooleanAttribute(String key, boolean value) {
        if (VMStr.isEmpty(key)) {
            return value;
        }
        return mMessage.getBooleanAttribute(key, value);
    }

    /**
     * 获取 int 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    public int getIntAttribute(String key, int value) {
        if (VMStr.isEmpty(key)) {
            return value;
        }
        return mMessage.getIntAttribute(key, value);
    }

    /**
     * 获取 long 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    public long getLongAttribute(String key, long value) {
        if (VMStr.isEmpty(key)) {
            return value;
        }
        return mMessage.getLongAttribute(key, value);
    }

    /**
     * 获取 String 类型扩展属性
     *
     * @param key   属性名
     * @param value 缺省值
     */
    public String getStringAttribute(String key, String value) {
        if (VMStr.isEmpty(key)) {
            return value;
        }
        return mMessage.getStringAttribute(key, value);
    }

    /**
     * 获取 JSONObject 类型扩展属性
     *
     * @param key 属性名
     */
    public JSONObject getJSONObjectAttribute(String key) {
        if (VMStr.isEmpty(key)) {
            return null;
        }
        try {
            return mMessage.getJSONObjectAttribute(key);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取 JSONArray 类型扩展属性
     *
     * @param key 属性名
     */
    public JSONArray getJSONArrayAttribute(String key) {
        if (VMStr.isEmpty(key)) {
            return null;
        }
        try {
            return mMessage.getJSONArrayAttribute(key);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return null;
    }
}
