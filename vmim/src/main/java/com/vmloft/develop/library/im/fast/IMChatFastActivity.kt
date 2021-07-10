package com.vmloft.develop.library.im.fast

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.common.base.BaseActivity
import com.vmloft.develop.library.common.base.BaseFragment
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.common.widget.CommonDialog
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMUser
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.logger.VMLog

import kotlinx.android.synthetic.main.im_activity_chat_fast.*


/**
 * Create on lzan13 on 2021/05/31 10:10
 *
 * 实时快速聊天界面，输入内容直接看到
 */
@Route(path = IMRouter.imChatFast)
class IMChatFastActivity : BaseActivity() {

    @Autowired
    lateinit var chatId: String

    @JvmField
    @Autowired
    var isApply: Boolean = false

    lateinit var mUser: IMUser
    lateinit var mSelfUser: IMUser

    lateinit var chatFragment: BaseFragment

    override fun layoutId() = R.layout.im_activity_chat_fast

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
        imChatFastContentET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                VMLog.e("输入内容变化 $s start-$start count-$count after-$after")
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var content = ""
                var len = count
                if (before == 0) {
                    content = s.substring(start)
                } else {
                    len = before
                }
                VMLog.e("输入内容变化 $s content-$content start-$start before-$before count-$count")
                IMChatFastManager.sendFastSignal(chatId, IMConstants.ChatFast.fastInputStatusContent, content, len)
            }

            override fun afterTextChanged(s: Editable) {
                VMLog.e("输入内容变化 $s")
            }
        })
        imChatFastContentET.isFocusable = true
        imChatFastContentET.isFocusableInTouchMode = true
        imChatFastContentET.requestFocus()
        imChatFastContentET.setOnClickListener {
            imChatFastContentET.requestFocus()
            imChatFastContentET.text?.let { content -> imChatFastContentET.setSelection(content.length) }
        }
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        mUser = IM.imListener.getUser(chatId) ?: IMUser(chatId)
        val selfId = IM.getSelfId()
        mSelfUser = IM.imListener.getUser(selfId) ?: IMUser(selfId)

        bindInfo()

        if (isApply) {
            showApplyDialog()
        } else {
            IMChatFastManager.sendFastSignal(chatId, IMConstants.ChatFast.fastInputStatusApply)
            showBar(R.string.im_fast_wait)
        }
    }

    override fun hideTopSpace() = true

    private fun bindInfo() {
        IMGLoader.loadAvatar(imChatFastAvatar1IV, mUser.avatar)
        imChatFastName1TV.text = mUser.nickname ?: "小透明"
        when (mUser.gender) {
            1 -> imChatFastGender1IV.setImageResource(R.drawable.ic_gender_man)
            0 -> imChatFastGender1IV.setImageResource(R.drawable.ic_gender_woman)
            else -> imChatFastGender1IV.setImageResource(R.drawable.ic_gender_man)
        }

        IMGLoader.loadAvatar(imChatFastAvatar2IV, mSelfUser.avatar)
        imChatFastName2TV.text = mSelfUser.nickname ?: "小透明"
        when (mSelfUser.gender) {
            1 -> imChatFastGender2IV.setImageResource(R.drawable.ic_gender_man)
            0 -> imChatFastGender2IV.setImageResource(R.drawable.ic_gender_woman)
            else -> imChatFastGender2IV.setImageResource(R.drawable.ic_gender_man)
        }
    }

    /**
     * 改变对方输入内容
     */
    private fun changeContent(msg: EMMessage) {
        val content = msg.getStringAttribute(IMConstants.ChatFast.msgAttrFastInputContent, "")
        val len = msg.getIntAttribute(IMConstants.ChatFast.msgAttrFastInputLen, 0)
        if (content.isNullOrEmpty()) {
            imChatFastContent1TV.text = imChatFastContent1TV.text.substring(0, imChatFastContent1TV.text.length - len)
        } else {
            imChatFastContent1TV.text = "${imChatFastContent1TV.text}$content"
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
            dialog.setNegative() {
                IMChatFastManager.sendFastSignal(chatId, IMConstants.ChatFast.fastInputStatusReject)
                finish()
            }
            dialog.setPositive() {
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