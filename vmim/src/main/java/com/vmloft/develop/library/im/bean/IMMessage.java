package com.vmloft.develop.library.im.bean;

import com.hyphenate.chat.EMMessage;

import java.io.Serializable;

/**
 * Create by lzan13 on 2019/5/21 15:11
 *
 * 自定义 IMMessage 消息对象
 */
public class IMMessage implements Serializable {

    // 内部消息对象
    private EMMessage mMessage;

    public IMMessage() {
    }

    public IMMessage(EMMessage message) {
        mMessage = message;
    }

    public EMMessage getmMessage() {
        return mMessage;
    }

    public void setmMessage(EMMessage mMessage) {
        this.mMessage = mMessage;
    }
}
