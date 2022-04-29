package com.vmloft.develop.app.template.ui.feedback

import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivityFeedbackBinding
import com.vmloft.develop.app.template.request.bean.Attachment
import com.vmloft.develop.app.template.request.bean.Comment
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.viewmodel.FeedbackViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.image.IMGChoose
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.tools.utils.VMReg
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMSystem
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/6/17 17:10
 * 描述：问题反馈
 */
@Route(path = AppRouter.appFeedback)
class FeedbackActivity : BVMActivity<ActivityFeedbackBinding, FeedbackViewModel>() {


    // 反馈类型 0-意见建议 1-广告 2-政治敏感 3-色情低俗 4-血腥暴力 5-不文明 6-涉嫌诈骗 7-其他
    @Autowired(name = CRouter.paramsWhat)
    @JvmField
    var type: Int = 0

    @Autowired(name = CRouter.paramsObj0)
    @JvmField
    var reportObj: Parcelable? = null

    private var userId: String = ""
    private var postId: String = ""
    private var commentId: String = ""

    // 内容
    private var mContent: String = ""

    // 联系方式
    private var mContact: String = ""

    // 截图
    private var picture: Any? = null

    override fun initVB() = ActivityFeedbackBinding.inflate(layoutInflater)

    override fun initVM(): FeedbackViewModel = getViewModel()

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.settings_feedback)

        setTopEndBtnEnable(false)
        setTopEndBtnListener(VMStr.byRes(R.string.btn_confirm)) { planSubmit() }

        // 选择图片
        mBinding.feedbackPictureBtn.setOnClickListener {
            IMGChoose.singlePicture(this) {
                picture = it
                mBinding.feedbackPictureBtn.visibility = View.GONE
                IMGLoader.loadCover(mBinding.feedbackPictureIV, picture, true, 8)
            }
        }
        mBinding.feedbackPictureIV.setOnClickListener {
            CRouter.goDisplaySingle(picture.toString())
        }
        mBinding.feedbackPictureCloseIV.setOnClickListener {
            picture = null
            mBinding.feedbackPictureBtn.visibility = View.VISIBLE
        }
        // 监听输入框变化
        mBinding.feedbackContentET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                mContent = s.toString().trim()
                verifyInputBox()
            }
        })
        mBinding.feedbackContactET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                mContact = s.toString().trim()
            }
        })


    }

    override fun initData() {
        ARouter.getInstance().inject(this)
        reportObj?.let {
            when (it) {
                is User -> {
                    userId = it.id
                }
                is Post -> {
                    userId = it.owner.id
                    postId = it.id
                }
                is Comment -> {
                    userId = it.owner.id
                    postId = it.post.id
                    commentId = it.id
                }
            }
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "uploadPicture") {
            submit(model.data as Attachment)
        } else if (model.type == "feedback") {
            showBar(R.string.feedback_submit_hint)
            VMSystem.runInUIThread({ finish() }, CConstants.timeSecond)
        }
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() {
        // 检查输入框是否为空
        setTopEndBtnEnable(!mContent.isNullOrEmpty())
    }

    /**
     * 准备
     */
    private fun planSubmit() {
        if (picture == null) {
            submit(null)
        } else {
            mViewModel.uploadPicture(picture!!)
        }
    }

    /**
     * 创建
     */
    private fun submit(attachment: Attachment?) {
        if (!VMReg.isCommonReg(mContent, "^[\\s\\S]{1,800}\$")) {
            return errorBar(R.string.input_not_null)
        }

        val list = mutableListOf<String>()
        attachment?.let {
            list.add(it.id)
        }
        mViewModel.feedback(mContact, mContent, type, list, userId, postId, commentId)
    }


}
