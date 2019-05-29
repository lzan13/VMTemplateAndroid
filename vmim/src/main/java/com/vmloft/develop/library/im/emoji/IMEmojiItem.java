package com.vmloft.develop.library.im.emoji;

/**
 * Create by lzan13 on 2019/5/29 11:41
 *
 * 表情选项实体类
 */
public class IMEmojiItem extends IMEmojiBean {

    /**
     * 表情对应本地文件名
     */
    public String mFileName;

    /**
     * 动态表情对应本地文件名
     */
    public String mFileGifName;

    /**
     * 表情包名
     */
    public String mGroupName;


    public IMEmojiItem() {
    }

    public IMEmojiItem(int resId, String desc, boolean emoji) {
        mEmojiResId = resId;
        mEmojiDesc = desc;
        isEmoji = emoji;
    }
}
