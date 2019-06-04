package com.vmloft.develop.library.im.emotion;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/29 11:41
 *
 * 表情分组实体类
 */
public class IMEmotionGroup extends IMEmotionBean {

    // 表情组名字
    public String mName;

    /**
     * 表情集合
     */
    public List<IMEmotionItem> mEmotionItemList = new ArrayList<>();

    /**
     * 获取表情数量
     */
    public int getEmotionCount() {
        return mEmotionItemList.size();
    }
}
