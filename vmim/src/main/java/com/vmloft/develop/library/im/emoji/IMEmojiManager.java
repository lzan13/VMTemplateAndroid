package com.vmloft.develop.library.im.emoji;

import com.vmloft.develop.library.im.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/29 12:01
 *
 * 表情管理类
 */
public class IMEmojiManager {

    public static final int IM_EMOJI_COLUMN_COUNT = 7;
    public static final int IM_EMOJI_COLUMN_COUNT_DEFAULT = 4;

    private static final String IM_EMOJI_GROUP_NAME = "im_emoji_group_name";

    private List<IMEmojiGroup> mEmojiGroupList = new ArrayList<>();

    private IMEmojiManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMEmojiManager INSTANCE = new IMEmojiManager();
    }

    /**
     * 获取单例类实例
     */
    public static IMEmojiManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 表情管理类的初始化
     */
    public void init() {
        initEmojiData();
    }

    /**
     * 获取表情组
     */
    public List<IMEmojiGroup> getEmojiGroupList() {
        return mEmojiGroupList;
    }

    /**
     * 初始化 Emoji 表情数据
     */
    private void initEmojiData() {

        List<IMEmojiItem> emojiList = new ArrayList<>();

        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_1, "[傻笑]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_2, "[微笑]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_3, "[呲牙]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_4, "[害羞]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_5, "[色]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_6, "[眨眼]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_7, "[得意]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_8, "[笑哭]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_9, "[亲亲]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_10, "[飞吻]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_11, "[美味]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_12, "[淘气]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_13, "[吐舌]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_14, "[聪明]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_15, "[惬意]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_16, "[激动]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_17, "[书呆子]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_18, "[帅气]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_19, "[小丑]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_20, "[不想说话]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_21, "[思考]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_22, "[鄙视]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_23, "[失落]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_24, "[愤怒]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_25, "[瞪眼]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_26, "[翻白眼]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_27, "[眼花]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_28, "[生气]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_29, "[担心]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_30, "[难过]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_31, "[流泪]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_32, "[流汗]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_33, "[惊讶]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_34, "[震惊]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_35, "[邪恶]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_36, "[生病]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_37, "[气炸了]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_38, "[困了]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_39, "[流口水]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_40, "[流鼻涕]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_41, "[圆月脸]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_42, "[弯月脸]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_43, "[眼睛]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_44, "[便便]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_45, "[赞]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_46, "[胜利]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_47, "[加油]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_48, "[拳击]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_49, "[挥手]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_50, "[ok]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_51, "[握手]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_52, "[鼓掌]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_53, "[双手合十]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_54, "[摊手]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_55, "[拒绝]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_56, "[捂脸]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_57, "[孕妇]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_58, "[爱情]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_59, "[幸福的一家]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_60, "[自拍]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_61, "[鬼脸]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_62, "[庆祝]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_63, "[气球]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_64, "[火]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_65, "[闪烁]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_66, "[土豪]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_67, "[晚安]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_68, "[满分]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_69, "[奖杯]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_70, "[唱歌]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_71, "[圣诞老人]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_72, "[圣诞树]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_73, "[心]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_74, "[心碎]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_75, "[丘比特]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_76, "[礼盒]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_77, "[嘴唇]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_78, "[玫瑰]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_79, "[花朵]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_80, "[花束]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_81, "[狗]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_82, "[猫]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_83, "[牛]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_84, "[猪]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_85, "[捂脸猴]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_86, "[热带鱼]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_87, "[西瓜]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_88, "[草莓]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_89, "[橙子]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_90, "[番茄]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_91, "[苹果]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_92, "[椰子树]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_93, "[稻穗]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_94, "[向日葵]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_95, "[四叶草]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_96, "[仙人掌]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_97, "[风叶]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_98, "[枫叶]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_99, "[落叶]", true));
        emojiList.add(new IMEmojiItem(R.drawable.im_emoji_100, "[太阳]", true));

        IMEmojiGroup group = new IMEmojiGroup();
        group.mGroupName = IM_EMOJI_GROUP_NAME;
        group.mGroupIndex = 0;
        group.mEmojiResId = R.drawable.im_emoji_2;
        group.isEmoji = true;
        group.mEmojiItemList = emojiList;

        mEmojiGroupList.add(group);
    }
}
