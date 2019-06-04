package com.vmloft.develop.library.im.emotion;

/**
 * Create by lzan13 on 2019/5/29 11:41
 *
 * 表情选项实体类
 */
public class IMEmotionItem extends IMEmotionBean {

    // 表情对应文本
    public String mDesc;

    // 表情对应本地文件名
    public String mFileName;
    // 动态表情对应本地文件名
    public String mFileGifName;

    /**
     * 无参构造
     */
    public IMEmotionItem() {}

    /**
     * 内部表情专用构造
     *
     * @param resId 资源 Id
     * @param desc  表情描述
     */
    public IMEmotionItem(int resId, String desc) {
        mResId = resId;
        mDesc = desc;
        isInnerEmotion = true;
    }
}
