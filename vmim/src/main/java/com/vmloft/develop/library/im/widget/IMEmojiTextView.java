package com.vmloft.develop.library.im.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.vmloft.develop.library.im.emoji.IMEmojiManager;

/**
 * Create by lzan13 2019/05/30 09:39
 *
 * 可以处理 Emoji 表情的 TextView
 */
public class IMEmojiTextView extends AppCompatTextView {

    private boolean isEnableEmoji = true;


    public IMEmojiTextView(Context context) {
        this(context, null);
    }

    public IMEmojiTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IMEmojiTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写，重新实现文本的表情符处理
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        Spannable spannable = handleEmoji(text);
        super.setText(spannable, type);
    }

    /**
     * 设置是否启用 TextView 识别 Emoji 表情功能
     */
    public void setEnableEmoji(boolean enable) {
        isEnableEmoji = enable;
    }

    /**
     * 处理 Emoji
     *
     * @param text 需要处理的文本内容
     */
    protected Spannable handleEmoji(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableString("");
        }
        if (!isEnableEmoji) {
            return new SpannableString(text);
        }
        return IMEmojiManager.getInstance().getEmojiSpannable(text);
    }

}
