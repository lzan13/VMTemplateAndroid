package com.vmloft.develop.library.im.emotion;

/**
 * Create by lzan13 on 2019/5/29 11:41
 *
 * 表情选项实体类
 */
public class IMEmotionItem extends IMEmotionBean {

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


    public IMEmotionItem() {
    }

    public IMEmotionItem(int resId, String desc, boolean inner) {
        mResId = resId;
        mDesc = desc;
        isInnerEmotion = inner;
    }
}
