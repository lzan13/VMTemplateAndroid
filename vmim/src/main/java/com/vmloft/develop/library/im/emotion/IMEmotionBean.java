package com.vmloft.develop.library.im.emotion;

/**
 * Create by lzan13 on 2019/5/29 11:30
 *
 * 表情数据实体基类
 */
public class IMEmotionBean {

    // 是否是内部自带表情，这种表情不需要下载，直接通过资源 Id 加载
    public boolean isInnerEmotion;

    // 是否是大表情
    public boolean isBigEmotion;

    // 表情的资源 Id，只有内部自带的表情才有此值
    public int mResId;

    // 不是内部表情，图片加载地址
    public String mUrl = "";
    // 不是内部表情，动态图片加载地址
    public String mGifUrl = "";
}
