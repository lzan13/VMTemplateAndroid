package com.vmloft.develop.library.im.fast

import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.View

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMUser
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImActivityChatFastBinding
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.logger.VMLog

/**
 * Create on lzan13 on 2021/05/31 10:10
 * 描述：实时快速聊天界面，输入内容直接看到
 */
@Route(path = IMRouter.imChatFast)
class IMChatFastActivity : BActivity<ImActivityChatFastBinding>() {

    @Autowired
    lateinit var chatId: String

    @JvmField
    @Autowired
    var isApply: Boolean = false

    lateinit var mUser: IMUser
    lateinit var mSelfUser: IMUser

    override var isHideTopSpace = true

    override fun initVB() = ImActivityChatFastBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.im_chat_fast)

        initInputWatcher()

        // 监听输入内容 CMD 消息
        LDEventBus.observe(this, IMConstants.ChatFast.cmdFastInputAction, EMMessage::class.java) {
            val status = it.getIntAttribute(IMConstants.ChatFast.msgAttrFastInputStatus, IMConstants.ChatFast.fastInputStatusEnd)
            if (status == IMConstants.ChatFast.fastInputStatusApply) {
                // TODO 邀请的申请过不来这里的，通过入参判断是否显示申请对话框
            } else if (status == IMConstants.ChatFast.fastInputStatusAgree) {
                showBar(R.string.im_fast_agree)
                mBinding.imChatFastContentET.isEnabled = true
                mBinding.imChatFastWaitLL.visibility = View.GONE
            } else if (status == IMConstants.ChatFast.fastInputStatusReject || status == IMConstants.ChatFast.fastInputStatusBusy) {
                showRejectOrBusyDialog(status)
            } else if (status == IMConstants.ChatFast.fastInputStatusContent) {
                // 内容变化
                changeContent(it)
            } else if (status == IMConstants.ChatFast.fastInputStatusEnd) {
                showEndDialog()
            }
        }
    }

    /**
     * 设置输入框内容的监听
     */
    private fun initInputWatcher() {
        mBinding.imChatFastContent1TV.movementMethod = ScrollingMovementMethod.getInstance()
        mBinding.imChatFastContentET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                VMLog.i("输入内容变化 $s start-$start count-$count after-$after")
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var content = ""
                var len = count
                if (before == 0) {
                    content = s.substring(start)
                } else {
                    len = before
                }
                VMLog.i("输入内容变化 $s content-$content start-$start before-$before count-$count")
                IMChatFastManager.sendFastSignal(chatId, IMConstants.ChatFast.fastInputStatusContent, content, len)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mBinding.imChatFastContentET.isFocusable = true
        mBinding.imChatFastContentET.isFocusableInTouchMode = true
        mBinding.imChatFastContentET.requestFocus()
        mBinding.imChatFastContentET.setOnClickListener {
            if (mBinding.imChatFastContentET.isEnabled) {
                mBinding.imChatFastContentET.requestFocus()
                mBinding.imChatFastContentET.text?.let { content -> mBinding.imChatFastContentET.setSelection(content.length) }
            } else {
                showBar(R.string.im_fast_wait)
            }
        }
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        mUser = IM.imListener.getUser(chatId) ?: IMUser(chatId)
        mSelfUser = IM.imListener.getUser(IM.getSelfId()) ?: IMUser(IM.getSelfId())

        bindInfo()

        mBinding.imChatFastWaitLL.visibility = View.VISIBLE

        if (isApply) {
            showApplyDialog()
        } else {
            // 对方没有同意时不能发送消息
            mBinding.imChatFastContentET.isEnabled = false
            IMChatFastManager.sendFastSignal(chatId, IMConstants.ChatFast.fastInputStatusApply)
            showBar(R.string.im_fast_wait)
        }
    }

    private fun bindInfo() {
        IMGLoader.loadAvatar(mBinding.imChatFastAvatar1IV, mUser.avatar)
        mBinding.imChatFastName1TV.text = mUser.nickname ?: "小透明"
        when (mUser.gender) {
            1 -> mBinding.imChatFastGender1IV.setImageResource(R.drawable.ic_gender_man)
            0 -> mBinding.imChatFastGender1IV.setImageResource(R.drawable.ic_gender_woman)
            else -> mBinding.imChatFastGender1IV.setImageResource(R.drawable.ic_gender_man)
        }

        IMGLoader.loadAvatar(mBinding.imChatFastAvatar2IV, mSelfUser.avatar)
        mBinding.imChatFastName2TV.text = mSelfUser.nickname ?: "小透明"
        when (mSelfUser.gender) {
            1 -> mBinding.imChatFastGender2IV.setImageResource(R.drawable.ic_gender_man)
            0 -> mBinding.imChatFastGender2IV.setImageResource(R.drawable.ic_gender_woman)
            else -> mBinding.imChatFastGender2IV.setImageResource(R.drawable.ic_gender_man)
        }
    }

    /**
     * 改变对方输入内容
     */
    private fun changeContent(msg: EMMessage) {
        val content = msg.getStringAttribute(IMConstants.ChatFast.msgAttrFastInputContent, "")
        val len = msg.getIntAttribute(IMConstants.ChatFast.msgAttrFastInputLen, 0)
        if (content.isNullOrEmpty()) {
            mBinding.imChatFastContent1TV.text = mBinding.imChatFastContent1TV.text.substring(0, mBinding.imChatFastContent1TV.text.length - len)
        } else {
            mBinding.imChatFastContent1TV.text = "${mBinding.imChatFastContent1TV.text}$content"
        }
    }

    /**
     * 显示申请对话框
     */
    private fun showApplyDialog() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.backDismissSwitch = false
            dialog.touchDismissSwitch = false
            dialog.setContent(R.string.im_fast_apply)
            dialog.setNegative {
                IMChatFastManager.sendFastSignal(chatId, IMConstants.ChatFast.fastInputStatusReject)
                finish()
            }
            dialog.setPositive {
                mBinding.imChatFastWaitLL.visibility = View.GONE
                IMChatFastManager.sendFastSignal(chatId, IMConstants.ChatFast.fastInputStatusAgree)
            }
            dialog.show()
        }
    }

    /**
     * 显示被拒绝或者对方忙碌对话框
     */
    private fun showRejectOrBusyDialog(status: Int) {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.backDismissSwitch = false
            dialog.touchDismissSwitch = false
            if (status == IMConstants.ChatFast.fastInputStatusReject) {
                dialog.setContent(R.string.im_fast_reject)
            } else {
                dialog.setContent(R.string.im_fast_busy)
            }
            dialog.setNegative("")
            dialog.setPositive(VMStr.byRes(R.string.vm_i_know)) {
                finish()
            }
            dialog.show()
        }
    }

    /**
     * 显示结束对话框
     */
    private fun showEndDialog() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.backDismissSwitch = false
            dialog.touchDismissSwitch = false
            dialog.setContent(R.string.im_fast_end)
            dialog.setNegative(VMStr.byRes(R.string.im_fast_cancel)) {
                finish()
            }
            dialog.setPositive(VMStr.byRes(R.string.im_fast_confirm)) {
                IM.imListener.onHeadClick(chatId)
                finish()
            }
            dialog.show()
        }
    }

    /**
     * 显示退出对话框
     */
    private fun showExitDialog() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.backDismissSwitch = false
            dialog.touchDismissSwitch = false
            dialog.setContent(R.string.im_fast_exit)
            dialog.setPositive(listener = {
                IMChatFastManager.sendFastSignal(chatId, IMConstants.ChatFast.fastInputStatusEnd)
                finish()
            })
            dialog.show()
        }
    }

    override fun onBackPressed() {
        showExitDialog()
    }
}