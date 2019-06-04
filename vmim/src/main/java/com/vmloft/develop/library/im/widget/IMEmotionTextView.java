package com.vmloft.develop.library.im.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.vmloft.develop.library.im.emotion.IMEmotionManager;

/**
 * Create by lzan13 2019/05/30 09:39
 *
 * 可以处理表情的 TextView
 */
public class IMEmotionTextView extends AppCompatTextView {

    private boolean isEnableEmotion = true;


    public IMEmotionTextView(Context context) {
        this(context, null);
    }

    public IMEmotionTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IMEmotionTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写，重新实现文本的表情符处理
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        Spannable spannable = handleEmotion(text);
        super.setText(spannable, type);
    }

    /**
     * 设置是否启用 TextView 识别表情功能
     */
    public void setEnableEmotion(boolean enable) {
        isEnableEmotion = enable;
    }

    /**
     * 处理表情
     *
     * @param text 需要处理的文本内容
     */
    protected Spannable handleEmotion(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableString("");
        }
        if (!isEnableEmotion) {
            return new SpannableString(text);
        }
        return IMEmotionManager.getInstance().getEmotionSpannable(text);
    }

}
