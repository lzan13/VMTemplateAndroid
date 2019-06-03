package com.vmloft.develop.library.im.emoji;

/**
 * Create by lzan13 on 2019/5/29 11:30
 *
 * 表情数据实体基类
 */
public class IMEmojiBean {

    // 是否是 Emoji 表情，这个不用下载
    public boolean isEmoji;

    // 是否是大表情
    public boolean isEmojiBig;

    // Emoji 的资源 id
    public int mEmojiResId;

    // 表情描述的文本信息如:[捂脸]
    public String mEmojiDesc;

    // 不是 Emoji 表情，图片加载地址
    public String mUrl = "";

    /**
     * 不是 Emoji 表情，动态图片加载地址
     */
    public String mGifUrl = "";
}
