package com.vmloft.develop.library.im.emoji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by lzan13 on 2019/5/29 12:01
 *
 * 表情管理类
 */
public class IMEmojiManager {

    public static final int IM_EMOJI_COLUMN_COUNT = 7;
    public static final int IM_EMOJI_COLUMN_COUNT_DEFAULT = 4;

    private static final String IM_EMOJI_GROUP = "im_emoji_group";
    private static final String IM_EMOJI_BIG_GROUP = "im_emoji_big_group";
    private static List<IMEmojiItem> EMOJIS = new ArrayList<>();
    private static List<IMEmojiItem> EMOJIS_BIG = new ArrayList<>();

    static {
        // 小 Emoji 表情分组
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_01, "[傻笑]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_02, "[微笑]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_03, "[笑哭]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_04, "[害羞]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_05, "[尴尬]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_06, "[眨眼]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_07, "[得意]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_08, "[鄙视]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_09, "[色]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_10, "[飞吻]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_11, "[美味]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_12, "[邪恶]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_13, "[吐舌]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_14, "[聪明]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_15, "[帅气]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_16, "[开心]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_17, "[流口水]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_18, "[惬意]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_19, "[大哭]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_20, "[震惊]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_21, "[生气]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_22, "[愤怒]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_23, "[书呆子]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_24, "[思考]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_25, "[虚]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_26, "[困了]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_27, "[睡着了]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_28, "[冻僵了]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_29, "[戴口罩]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_30, "[发烧]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_31, "[擦鼻涕]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_32, "[吐]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_33, "[瞎了]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_34, "[囧]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_35, "[翻白眼]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_36, "[失落]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_37, "[伤心]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_38, "[流汗]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_39, "[可怜]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_40, "[小丑]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_41, "[鬼脸]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_42, "[骷髅]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_43, "[眼睛]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_44, "[便便]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_45, "[赞]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_46, "[胜利]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_47, "[加油]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_48, "[拳击]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_49, "[挥手]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_50, "[OK]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_51, "[握手]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_52, "[鼓掌]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_53, "[阿门]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_54, "[摊手女]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_55, "[摊手男]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_56, "[捂脸女]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_57, "[捂脸男]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_58, "[跳舞女]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_59, "[跳舞男]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_60, "[四叶草]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_61, "[心]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_62, "[心碎]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_63, "[庆祝]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_64, "[气球]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_65, "[火]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_66, "[星星]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_67, "[太阳]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_68, "[嘴唇]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_69, "[玫瑰]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_70, "[西瓜]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_71, "[草莓]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_72, "[橙子]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_73, "[番茄]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_74, "[苹果]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_75, "[猴不看]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_76, "[猴不听]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_77, "[猴不说]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_78, "[猫笑哭]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_79, "[猫色]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_80, "[猫惊讶]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_81, "[狗]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_82, "[猫]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_83, "[牛]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_84, "[猪]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_85, "[狐狸]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_86, "[鱼]", true));
        EMOJIS.add(new IMEmojiItem(R.drawable.im_emoji_87, "[独角兽]", true));

        // 大 Emoji 表情分组
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_01, "[捂脸]", true));
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_02, "[机智]", true));
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_03, "[邪恶]", true));
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_04, "[耶]", true));
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_05, "[呆萌]", true));
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_06, "[狗头]", true));
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_07, "[惊吓]", true));
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_08, "[瞪]", true));
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_09, "[嘿哈]", true));
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_10, "[巴掌印]", true));
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_11, "[喝水]", true));
        EMOJIS_BIG.add(new IMEmojiItem(R.drawable.im_emoji_big_12, "[额]", true));
    }

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
        initEmojiBigData();
    }

    /**
     * 获取表情组
     */
    public List<IMEmojiGroup> getEmojiGroupList() {
        if (mEmojiGroupList.size() == 0) {
            init();
        }
        return mEmojiGroupList;
    }

    /**
     * 初始化 Emoji 表情数据
     */
    private void initEmojiData() {

        IMEmojiGroup group = new IMEmojiGroup();
        group.mGroupName = IM_EMOJI_GROUP;
        group.mGroupIndex = 0;
        group.mEmojiResId = R.drawable.im_emoji_01;
        group.isEmoji = true;
        group.mEmojiItemList = EMOJIS;

        mEmojiGroupList.add(group);
    }

    /**
     * 初始化 Emoji 大表情数据
     */
    private void initEmojiBigData() {

        IMEmojiGroup group = new IMEmojiGroup();
        group.mGroupName = IM_EMOJI_BIG_GROUP;
        group.mGroupIndex = 1;
        group.mEmojiResId = R.drawable.im_emoji_big_icon;
        group.isEmoji = true;
        group.isEmojiBig = true;
        group.mEmojiItemList = EMOJIS_BIG;

        mEmojiGroupList.add(group);
    }

    /**
     * 获取 Emoji 表情 Spannable 用来添加到输入框
     *
     * @param emojiId  表情资源 id
     * @param emojiStr 表情字符串
     * @return
     */
    public SpannableString getEmojiSpannable(int emojiId, String emojiStr) {
        if (VMStr.isEmpty(emojiStr)) {
            return null;
        }
        SpannableString spannable = new SpannableString(emojiStr);
        spannable.setSpan(createEmojiSpannable(emojiId), 0, emojiStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * 删除 Emoji 表情
     *
     * @param position 当前光标位置
     * @param text     输入框文本
     * @return 删除长度
     */
    public int deleteEmoji(int position, String text) {
        if (position == 0 || VMStr.isEmpty(text) || position > text.length()) {
            return 0;
        }

        //只截取光标之前的字符串
        text = text.substring(0, position);

        char last = text.charAt(position - 1);
        //最后一个字符必须是']'结尾
        if (last != ']') {
            //不符合表情匹配条件，只删除一个字符
            return 1;
        }

        int start = text.lastIndexOf("[");
        if (start < 0) {
            //不符合表情匹配条件，只删除一个字符
            return 1;
        }
        int end = position;
        String deleteString = text.substring(start, end);

        boolean isEmojiString = getResID(deleteString) != 0;
        if (isEmojiString) {
            return deleteString.length();
        } else {
            return 0;
        }
    }

    /**
     * 得到一个 SpannableString 对象，通过传入的字符串
     *
     * @param text 需要识别的文本
     */
    public SpannableString getEmojiSpannable(CharSequence text) {
        SpannableString spannable = new SpannableString(text);

        // 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
        String regularStr = "\\[[^\\]]+\\]";

        // 通过传入的正则表达式来生成一个 Pattern
        Pattern patten = Pattern.compile(regularStr, Pattern.CASE_INSENSITIVE);
        // 递归替换表情符
        replaceEmojiSpannable(spannable, patten, 0);
        return spannable;
    }

    /**
     * 根据 Emoji 表情描述文本映射到资源ID
     *
     * @param desc Emoji 表情描述文本
     * @return Emoji 表情资源 id
     */
    private int getResID(String desc) {
        for (int i = 0; i < EMOJIS.size(); i++) {
            if (EMOJIS.get(i).mEmojiDesc.equals(desc)) {
                return EMOJIS.get(i).mEmojiResId;
            }
        }
        return 0;
    }

    /**
     * 控制表情符号的大小
     *
     * @param emojiId
     * @return
     */
    private ImageSpan createEmojiSpannable(int emojiId) {
        Bitmap bitmap = BitmapFactory.decodeResource(IM.getInstance().getIMContext().getResources(), emojiId);
        bitmap = VMBitmap.compressBitmapByMatrix(bitmap, VMDimen.getDimenPixel(R.dimen.im_emoji_size));

        VerticalImageSpan imageSpan;
        if (bitmap != null) {
            imageSpan = new VerticalImageSpan(IM.getInstance().getIMContext(), bitmap);
        } else {
            imageSpan = new VerticalImageSpan(IM.getInstance().getIMContext(), emojiId);
        }

        return imageSpan;
    }

    /**
     * 对 SpannableString 进行正则判断，如果符合要求，则以表情图片代替
     *
     * @param spannable
     * @param patten
     * @param start
     */
    private void replaceEmojiSpannable(SpannableString spannable, Pattern patten, int start) {
        Matcher matcher = patten.matcher(spannable);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式 ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }

            int resId = getResID(key);
            // 通过上面匹配得到的字符串来生成图片资源id
            if (resId != 0) {
                // 计算该图片名字的长度，也就是要替换的字符串的长度
                int end = matcher.start() + key.length();

                // 将该图片替换字符串中规定的位置中
                spannable.setSpan(createEmojiSpannable(resId), matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                // 如果整个字符串还未验证完，则继续。。
                if (end < spannable.length()) {
                    replaceEmojiSpannable(spannable, patten, end);
                }
                break;
            }
        }
    }

    /**
     * -------------------------------- 自定义 ImageSpan --------------------------------
     *
     * 定义垂直居中的 ImageSpan，主要是为了实现让表情和文字排版居中
     */
    private static class VerticalImageSpan extends ImageSpan {

        public VerticalImageSpan(Context context, Bitmap bitmap) {
            super(context, bitmap);
        }

        public VerticalImageSpan(Context context, int imgId) {
            super(context, imgId);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt) {
            Drawable drawable = getDrawable();
            Rect rect = drawable.getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fontMetricsInt.ascent = -bottom;
                fontMetricsInt.top = -bottom;
                fontMetricsInt.bottom = top;
                fontMetricsInt.descent = top;
            }
            return rect.right;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            Drawable drawable = getDrawable();
            canvas.save();
            int transY = 0;
            transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        }
    }
}
