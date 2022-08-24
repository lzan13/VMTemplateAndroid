package com.vmloft.develop.app.template.ui.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View

import com.vmloft.develop.app.template.databinding.WidgetMatchEmotionDialogBinding
import com.vmloft.develop.app.template.report.ReportConstants
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.report.ReportManager
import com.vmloft.develop.library.tools.base.VMBDialog


/**
 * Create by lzan13 on 2022/03/22 21:41
 * 描述：匹配性别设置对话框
 */
class MatchEmotionDialog(context: Context) : VMBDialog<WidgetMatchEmotionDialogBinding>(context) {

    private val selfMatch = SignManager.getSelfMatch()
    private var matchContent: String = ""
    private var matchEmotion: Int = 0

    override fun initVB() = WidgetMatchEmotionDialogBinding.inflate(LayoutInflater.from(context))

    init {
        matchContent = selfMatch.content
        matchEmotion = selfMatch.emotion

        setupMatchEmotion()

        mBinding.contentET.setText(selfMatch.content)

        // 设置表情面板事件
        mBinding.emotionHappyLL.setOnClickListener { changeMatchEmotion(0) }
        mBinding.emotionNormalLL.setOnClickListener { changeMatchEmotion(1) }
        mBinding.emotionSadLL.setOnClickListener { changeMatchEmotion(2) }
        mBinding.emotionAngerLL.setOnClickListener { changeMatchEmotion(3) }

        mBinding.cancelTV.setOnClickListener { dismiss() }
        mBinding.submitTV.setOnClickListener { saveMatchEmotion() }

        mBinding.contentET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                matchContent = s.toString().trim()
                mBinding.submitTV.isEnabled = !matchContent.isEmpty()
            }
        })
    }

    /**
     * 装载匹配心情
     */
    private fun setupMatchEmotion() {
        mBinding.emotionHappyLL.isSelected = matchEmotion == 0
        mBinding.emotionNormalLL.isSelected = matchEmotion == 1
        mBinding.emotionSadLL.isSelected = matchEmotion == 2
        mBinding.emotionAngerLL.isSelected = matchEmotion == 3
    }

    /**
     * 修改匹配心情
     */
    private fun changeMatchEmotion(emotion: Int) {
        matchEmotion = emotion

        setupMatchEmotion()
    }

    /**
     * 保存匹配心情数据
     */
    private fun saveMatchEmotion() {
        if (matchContent.isEmpty()) {
            mBinding.errorTipsTV.visibility = View.VISIBLE
            return
        } else {
            mBinding.errorTipsTV.visibility = View.GONE
        }
        selfMatch.content = matchContent
        selfMatch.emotion = matchEmotion

        SignManager.setSelfMatch(selfMatch)

        val params = mutableMapOf<String, Any>()
        params["emotion"] = selfMatch.emotion // 心情 0-开心 1-平淡 2-难过 3-愤怒
        ReportManager.reportEvent(ReportConstants.eventChangeEmotion, params)

        dismiss()
    }
}