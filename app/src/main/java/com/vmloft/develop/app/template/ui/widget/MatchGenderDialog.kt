package com.vmloft.develop.app.template.ui.widget

import android.content.Context
import android.view.LayoutInflater

import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.WidgetMatchGenderDialogBinding
import com.vmloft.develop.app.template.report.ReportConstants
import com.vmloft.develop.library.report.ReportManager
import com.vmloft.develop.library.tools.base.VMBDialog


/**
 * Create by lzan13 on 2022/03/22 21:41
 * 描述：匹配性别设置对话框
 */
class MatchGenderDialog(context: Context) : VMBDialog<WidgetMatchGenderDialogBinding>(context) {

    private val selfMatch = SignManager.getSelfMatch()!!

    private var filterGender: Int = -1

    override fun initVB() = WidgetMatchGenderDialogBinding.inflate(LayoutInflater.from(context))

    /**
     * 初始化过滤相关
     */
    init {
        filterGender = selfMatch.filterGender

        setupMatchGender()

        mBinding.filterAllLL.setOnClickListener { changeMatchGender(-1) }
        mBinding.filterWomanLL.setOnClickListener { changeMatchGender(0) }
        mBinding.filterManLL.setOnClickListener { changeMatchGender(1) }

        mBinding.submitTV.setOnClickListener { saveMatchFilter() }
    }

    /**
     * 装载过滤性别
     */
    private fun setupMatchGender() {
        mBinding.filterAllLL.isSelected = filterGender == -1
        mBinding.filterWomanLL.isSelected = filterGender == 0
        mBinding.filterManLL.isSelected = filterGender == 1
    }

    /**
     * 修改过滤性别
     */
    private fun changeMatchGender(gender: Int) {
        filterGender = gender

        setupMatchGender()
    }

    /**
     * 保存匹配过滤设置
     */
    private fun saveMatchFilter() {
        selfMatch.filterGender = filterGender

        SignManager.setSelfMatch(selfMatch)

        val params = mutableMapOf<String, Any>()
        params["filter"] = selfMatch.filterGender // 过滤选项 -1-不限 0-女 1-男
        ReportManager.reportEvent(ReportConstants.eventChangeFilter, params)

        dismiss()
    }


}