package com.vmloft.develop.app.template.ui.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

import com.vmloft.develop.app.template.R
import com.vmloft.develop.library.common.widget.CommonDialog

import kotlinx.android.synthetic.main.widget_match_dialog.*

/**
 * Create by lzan13 2021/5/19
 * 描述：匹配输入对话框
 */
class MatchDialog(context: Context) : CommonDialog(context) {
    var inputContent: String? = null

    init {
        dialogContentET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                inputContent = s.toString().trim()
                positiveDismissSwitch = !inputContent.isNullOrEmpty()
            }
        })
    }

    override fun layoutId(): Int = R.layout.widget_match_dialog

//    fun getContent(): String? {
//        return content
//    }

    override fun getNegativeTV(): TextView? = dialogNegativeTV

    override fun getPositiveTV(): TextView? = dialogPositiveTV

}