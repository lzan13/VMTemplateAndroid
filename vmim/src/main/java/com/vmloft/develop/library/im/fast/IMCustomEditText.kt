package com.vmloft.develop.library.im.fast

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

/**
 * Create by lzan13 2021/06/01
 * 描述：自定义输入框，限制选择
 */
class IMCustomEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatEditText(context, attrs, defStyleAttr) {

    override fun onSelectionChanged(start: Int, end: Int) {

        val text: CharSequence? = text
        if (text != null) {
            if (start !== text.length || end !== text.length) {
                setSelection(text.length, text.length)
                return
            }
        }
        super.onSelectionChanged(start, end)
    }
}