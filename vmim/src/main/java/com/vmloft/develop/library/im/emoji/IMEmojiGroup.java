package com.vmloft.develop.library.im.emoji;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/29 11:41
 *
 * 表情分组实体类
 */
public class IMEmojiGroup extends IMEmojiBean {

    /**
     * 表情分组索引
     */
    public int mGroupIndex;

    /**
     * 表情包的名称
     */
    public String mGroupName;

    /**
     * 表情包的下载地址
     */
    public String mGroupUrl;

    /**
     * 表情集合
     */
    public List<IMEmojiItem> mEmojiItemList = new ArrayList<>();

    /**
     * 获取表情数量
     */
    public int getEmojiCount() {
        return mEmojiItemList.size();
    }

}
