package com.vmloft.develop.app.template.ui.post

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants

import com.vmloft.develop.app.template.databinding.ActivityPostCommentBinding
import com.vmloft.develop.app.template.request.bean.Comment
import com.vmloft.develop.app.template.request.viewmodel.PostViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMReg

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2021/11/11 22:56
 * 描述：帖子评论界面
 */
@Route(path = AppRouter.appPostComment)
class PostCommentActivity : BVMActivity<ActivityPostCommentBinding, PostViewModel>() {

    @Autowired(name = CRouter.paramsStr0)
    lateinit var postId: String

    @Autowired(name = CRouter.paramsObj0)
    @JvmField
    var userId: String = ""

    private var content: String = ""


    override fun initVB() = ActivityPostCommentBinding.inflate(layoutInflater)

    override fun initVM(): PostViewModel = getViewModel()

    override fun initUI() {
        super.initUI()

        initInputWatcher()
        mBinding.root.setOnClickListener { onBackPressed() }
        mBinding.submitIV.setOnClickListener { submit() }
    }

    override fun initData() {
        ARouter.getInstance().inject(this)
    }


    /**
     * 设置输入框内容的监听
     */
    private fun initInputWatcher() {
        mBinding.commentET.isFocusable = true
        mBinding.commentET.isFocusableInTouchMode = true
        mBinding.commentET.requestFocus()
        mBinding.commentET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                content = s.toString().trim()
                mBinding.submitIV.visibility = if (content.isEmpty()) View.GONE else View.VISIBLE
            }
        })
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        mBinding.submitIV.isEnabled = !model.isLoading
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "createComment") {
            LDEventBus.post(Constants.createCommentEvent, model.data as Comment)
            finish()
        }
    }

    /**
     * 提交评论
     */
    private fun submit() {
        if (!VMReg.isCommonReg(content, "^[\\s\\S]{1,64}\$")) {
            return errorBar(R.string.post_comment_limit_hint)
        }
        mViewModel.createComment(content, postId, userId ?: "")

    }


}