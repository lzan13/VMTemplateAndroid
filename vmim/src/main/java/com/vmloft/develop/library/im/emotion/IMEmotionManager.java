package com.vmloft.develop.library.im.emotion;

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
public class IMEmotionManager {

    public static final int IM_EMOTION_COLUMN_COUNT = 7;
    public static final int IM_EMOTION_COLUMN_COUNT_DEFAULT = 4;

    // 内部表情分组名
    private static final String IM_EMOTION_GROUP = "im_emotion_group";
    private static final String IM_EMOTION_BIG_GROUP = "im_emotion_big_group";

    private static List<IMEmotionItem> EMOTION_LIST = new ArrayList<>();
    private static List<IMEmotionItem> BIG_EMOTION_LIST = new ArrayList<>();

    static {
        // 内部小表情分组
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_01, "[傻笑]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_02, "[微笑]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_03, "[笑哭]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_04, "[害羞]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_05, "[尴尬]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_06, "[眨眼]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_07, "[得意]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_08, "[鄙视]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_09, "[色]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_10, "[飞吻]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_11, "[美味]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_12, "[邪恶]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_13, "[吐舌]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_14, "[聪明]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_15, "[帅气]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_16, "[开心]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_17, "[流口水]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_18, "[惬意]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_19, "[大哭]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_20, "[震惊]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_21, "[生气]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_22, "[愤怒]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_23, "[书呆子]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_24, "[思考]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_25, "[虚]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_26, "[困了]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_27, "[睡着了]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_28, "[冻僵了]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_29, "[戴口罩]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_30, "[发烧]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_31, "[擦鼻涕]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_32, "[吐]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_33, "[瞎了]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_34, "[囧]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_35, "[翻白眼]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_36, "[失落]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_37, "[伤心]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_38, "[流汗]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_39, "[可怜]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_40, "[小丑]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_41, "[鬼脸]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_42, "[骷髅]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_43, "[眼睛]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_44, "[便便]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_45, "[赞]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_46, "[胜利]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_47, "[加油]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_48, "[拳击]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_49, "[挥手]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_50, "[OK]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_51, "[握手]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_52, "[鼓掌]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_53, "[阿门]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_54, "[摊手女]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_55, "[摊手男]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_56, "[捂脸女]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_57, "[捂脸男]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_58, "[跳舞女]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_59, "[跳舞男]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_60, "[四叶草]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_61, "[心]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_62, "[心碎]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_63, "[庆祝]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_64, "[气球]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_65, "[火]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_66, "[星星]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_67, "[太阳]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_68, "[嘴唇]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_69, "[玫瑰]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_70, "[西瓜]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_71, "[草莓]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_72, "[橙子]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_73, "[番茄]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_74, "[苹果]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_75, "[猴不看]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_76, "[猴不听]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_77, "[猴不说]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_78, "[猫笑哭]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_79, "[猫色]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_80, "[猫惊讶]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_81, "[狗]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_82, "[猫]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_83, "[牛]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_84, "[猪]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_85, "[狐狸]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_86, "[鱼]"));
        EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_87, "[独角兽]"));

        // 内部大表情分组
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_01, "[黑人问号]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_02, "[好的]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_03, "[尴尬]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_04, "[沧桑]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_05, "[惊呆]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_06, "[好棒]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_07, "[无语]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_08, "[社会]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_09, "[吃瓜]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_10, "[羞羞]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_11, "[摸头]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_12, "[加油]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_13, "[旺财]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_14, "[猫哈子]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_15, "[萨摩]"));
        BIG_EMOTION_LIST.add(new IMEmotionItem(R.drawable.im_emotion_big_16, "[二哈]"));
    }

    private List<IMEmotionGroup> mEmotionGroupList = new ArrayList<>();

    private IMEmotionManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMEmotionManager INSTANCE = new IMEmotionManager();
    }

    /**
     * 获取单例类实例
     */
    public static IMEmotionManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 表情管理类的初始化
     */
    public void init() {
        initInnerEmotionData();
        initBigEmotionData();
    }

    /**
     * 获取表情组
     */
    public List<IMEmotionGroup> getEmotionGroupList() {
        if (mEmotionGroupList.size() == 0) {
            init();
        }
        return mEmotionGroupList;
    }

    /**
     * 初始化表情数据
     */
    private void initInnerEmotionData() {

        IMEmotionGroup group = new IMEmotionGroup();
        group.mName = IM_EMOTION_GROUP;
        group.mResId = R.drawable.im_emotion_01;
        group.isInnerEmotion = true;
        group.mEmotionItemList = EMOTION_LIST;

        mEmotionGroupList.add(group);
    }

    /**
     * 初始化内部大表情数据
     */
    private void initBigEmotionData() {

        IMEmotionGroup group = new IMEmotionGroup();
        group.mName = IM_EMOTION_BIG_GROUP;
        group.mResId = R.drawable.im_emotion_big_icon;
        group.isInnerEmotion = true;
        group.isBigEmotion = true;
        group.mEmotionItemList = BIG_EMOTION_LIST;

        mEmotionGroupList.add(group);
    }

    /**
     * 获取表情 Spannable 用来添加到输入框
     *
     * @param resId      表情资源 id
     * @param emotionStr 表情字符串
     * @return
     */
    public SpannableString getEmotionSpannable(int resId, String emotionStr) {
        if (VMStr.isEmpty(emotionStr)) {
            return null;
        }
        SpannableString spannable = new SpannableString(emotionStr);
        spannable.setSpan(createEmotionSpannable(resId), 0, emotionStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * 删除表情
     *
     * @param position 当前光标位置
     * @param text     输入框文本
     * @return 删除长度
     */
    public int deleteEmotion(int position, String text) {
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

        IMEmotionItem item = getEmotionItem(IM_EMOTION_GROUP, deleteString);
        if (item != null) {
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
    public SpannableString getEmotionSpannable(CharSequence text) {
        SpannableString spannable = new SpannableString(text);

        // 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
        String regularStr = "\\[[^\\]]+\\]";

        // 通过传入的正则表达式来生成一个 Pattern
        Pattern patten = Pattern.compile(regularStr, Pattern.CASE_INSENSITIVE);
        // 递归替换表情符
        replaceEmotionSpannable(spannable, patten, 0);
        return spannable;
    }

    /**
     * 根据表情描述文本映射到资源ID
     *
     * @param groupName 表情所属分组名
     * @param desc      表情描述文本
     * @return 表情资源 Id
     */
    public IMEmotionItem getEmotionItem(String groupName, String desc) {
        for (IMEmotionGroup group : mEmotionGroupList) {
            if (group.mName.equals(groupName)) {
                for (IMEmotionItem item : group.mEmotionItemList) {
                    if (item.mDesc.equals(desc)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 控制表情符号的大小
     *
     * @param resId
     * @return
     */
    private ImageSpan createEmotionSpannable(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(IM.getInstance().getIMContext().getResources(), resId);
        bitmap = VMBitmap.compressBitmapByMatrix(bitmap, VMDimen.getDimenPixel(R.dimen.im_emotion_size));

        VerticalImageSpan imageSpan;
        if (bitmap != null) {
            imageSpan = new VerticalImageSpan(IM.getInstance().getIMContext(), bitmap);
        } else {
            imageSpan = new VerticalImageSpan(IM.getInstance().getIMContext(), resId);
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
    private void replaceEmotionSpannable(SpannableString spannable, Pattern patten, int start) {
        Matcher matcher = patten.matcher(spannable);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式 ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }

            IMEmotionItem item = getEmotionItem(IM_EMOTION_GROUP, key);
            // 通过上面匹配得到的字符串来生成图片资源id
            if (item != null) {
                // 计算该图片名字的长度，也就是要替换的字符串的长度
                int end = matcher.start() + key.length();

                // 将该图片替换字符串中规定的位置中
                spannable.setSpan(createEmotionSpannable(item.mResId), matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                // 如果整个字符串还未验证完，则继续。。
                if (end < spannable.length()) {
                    replaceEmotionSpannable(spannable, patten, end);
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
