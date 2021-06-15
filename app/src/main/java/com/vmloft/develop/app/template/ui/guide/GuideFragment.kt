package com.vmloft.develop.app.template.ui.guide

import android.os.Bundle
import com.vmloft.develop.app.template.R
import com.vmloft.develop.library.common.base.BaseFragment

import kotlinx.android.synthetic.main.fragment_guide.*


/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：主页
 */
class GuideFragment : BaseFragment() {

    companion object {
        private val argImgId = "argImgId"
        private val argTitleId = "argTitleId"
        private val argBodyId = "argBodyId"
        /**
         * Fragment 的工厂方法，方便创建并设置参数
         */
        fun newInstance(imgId: Int, titleId: Int, bodyId: Int): GuideFragment {
            val fragment = GuideFragment()
            val args = Bundle()
            args.putInt(argImgId, imgId)
            args.putInt(argTitleId, titleId)
            args.putInt(argBodyId, bodyId)
            fragment.arguments = args
            return fragment
        }
    }


    override fun layoutId() = R.layout.fragment_guide

    override fun initUI() {
        super.initUI()

        guideCoverIV.setImageResource(arguments!!.getInt(argImgId))
        guideTitleTV.setText(arguments!!.getInt(argTitleId))
        guideBodyTV.setText(arguments!!.getInt(argBodyId))
    }

    override fun initData() {
        loadAnim()
    }


    private fun loadAnim() {}
}