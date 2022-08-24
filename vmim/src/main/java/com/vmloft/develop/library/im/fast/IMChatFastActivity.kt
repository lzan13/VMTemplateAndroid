package com.vmloft.develop.library.im.fast

import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.View

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMSignal
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImActivityChatFastBinding
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.animator.VMAnimator
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMSystem
import com.vmloft.develop.library.tools.utils.logger.VMLog
import java.util.*

/**
 * Create on lzan13 on 2021/05/31 10:10
 * 描述：实时快速聊天界面，输入内容直接看到
 */
@Route(path = IMRouter.imChatFast)
class IMChatFastActivity : BActivity<ImActivityChatFastBinding>() {

    @JvmField
    @Autowired(name = CRouter.paramsWhat)
    var isApply: Int = 0 // 被邀请

    @Autowired(name = CRouter.paramsStr0)
    lateinit var chatId: String

    lateinit var mUser: User
    lateinit var mSelfUser: User

    private var mAnimatorWrapRadar: VMAnimator.AnimatorSetWrap? = null
    private var mAnimatorWrap: VMAnimator.AnimatorSetWrap? = null
    private var mAnimatorWrap2: VMAnimator.AnimatorSetWrap? = null

    // 聊天总时间
    private var timer: Timer? = null
    private val totalTime = 60 * 5
    private var chatTime: Int = 0

    override var isHideTopSpace = true

    override fun initVB() = ImActivityChatFastBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.im_chat_fast)

        initInputWatcher()

        startAnim()

        // 监听输入内容 CMD 消息
        LDEventBus.observe(this, IMConstants.ChatFast.signalFastInput, IMSignal::class.java) {
            val status = it.getIntAttribute(IMConstants.ChatFast.extFastInputStatus, IMConstants.ChatFast.fastInputStatusEnd)
            if (status == IMConstants.ChatFast.fastInputStatusApply) {
                // TODO 邀请的申请过不来这里的，通过入参判断是否显示申请对话框
            } else if (status == IMConstants.ChatFast.fastInputStatusAgree) {
                startTimer()
                showBar(R.string.im_fast_agree)
                mBinding.imChatFastContentET.isEnabled = true
                mBinding.imChatFastWaitLL.visibility = View.GONE
            } else if (status == IMConstants.ChatFast.fastInputStatusReject || status == IMConstants.ChatFast.fastInputStatusBusy) {
                showRejectOrBusyDialog(status)
            } else if (status == IMConstants.ChatFast.fastInputStatusContent) {
                // 内容变化
                changeContent(it)
            } else if (status == IMConstants.ChatFast.fastInputStatusEnd) {
                stopTimer()

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

        mUser = CacheManager.getUser(chatId)
        mSelfUser = SignManager.getSignUser()

        bindInfo()

        mBinding.imChatFastWaitLL.visibility = View.VISIBLE

        if (isApply == 0) {
            showApplyDialog()
            mBinding.imChatFastContent1TV.text = VMStr.byRes(R.string.im_fast_apply_msg)
        } else {
            // 对方没有同意时不能发送消息
            mBinding.imChatFastContentET.isEnabled = false
            mBinding.imChatFastContentET.setText(VMStr.byRes(R.string.im_fast_apply_msg))

            IMChatFastManager.sendFastSignal(chatId, IMConstants.ChatFast.fastInputStatusApply)
            showBar(R.string.im_fast_wait)
        }
    }

    private fun bindInfo() {
        IMGLoader.loadAvatar(mBinding.imChatFastAvatar1IV, mUser.avatar)
        mBinding.imChatFastName1TV.text = mUser.nickname
        mBinding.imChatFastSignature1TV.text = if (mUser.signature.isNotEmpty()) mUser.signature else "无个性，不签名！"
        when (mUser.gender) {
            1 -> mBinding.imChatFastGender1IV.setImageResource(R.drawable.ic_gender_man)
            0 -> mBinding.imChatFastGender1IV.setImageResource(R.drawable.ic_gender_woman)
            else -> mBinding.imChatFastGender1IV.setImageResource(R.drawable.ic_gender_unknown)
        }

        IMGLoader.loadAvatar(mBinding.imChatFastAvatar2IV, mSelfUser.avatar)
        mBinding.imChatFastName2TV.text = mSelfUser.nickname
        mBinding.imChatFastSignature2TV.text = if (mSelfUser.signature.isNotEmpty()) mSelfUser.signature else "无个性，不签名！"
        when (mSelfUser.gender) {
            1 -> mBinding.imChatFastGender2IV.setImageResource(R.drawable.ic_gender_man)
            0 -> mBinding.imChatFastGender2IV.setImageResource(R.drawable.ic_gender_woman)
            else -> mBinding.imChatFastGender2IV.setImageResource(R.drawable.ic_gender_unknown)
        }
    }

    /**
     * 改变对方输入内容
     */
    private fun changeContent(msg: IMSignal) {
        val content = msg.getStringAttribute(IMConstants.ChatFast.extFastInputContent, "")
        val len = msg.getIntAttribute(IMConstants.ChatFast.extFastInputLen, 0)
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
                stopAnim()
                finish()
            }
            dialog.setPositive {
                startTimer()

                mBinding.imChatFastWaitLL.visibility = View.GONE
                IMChatFastManager.sendFastSignal(chatId, IMConstants.ChatFast.fastInputStatusAgree)
                // 同意之后，输入一句话方便对方看到
                mBinding.imChatFastContentET.setText(R.string.im_fast_agree_msg)
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
                stopAnim()
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
                stopAnim()
                finish()
            }
            dialog.setPositive(VMStr.byRes(R.string.im_fast_confirm)) {
                IM.onHeadClick(chatId)
                stopAnim()
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
                stopAnim()
                stopTimer()
                finish()
            })
            dialog.show()
        }
    }

    /**
     * 拦截返回按键
     */
    override fun onBackPressed() {
        showExitDialog()
    }

    /**
     * 开启定时器
     */
    private fun startTimer() {
        timer = Timer()
        timer?.purge()
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                chatTime++
                VMSystem.runInUIThread({ mBinding.imChatFastTimeTV.text = formatTime() })
            }
        }
        timer?.scheduleAtFixedRate(task, 1000, 1000)
    }

    /**
     * 格式化聊天倒计时
     */
    fun formatTime(): String {
        val t: Int = totalTime - chatTime
        if (t <= 0) {
            stopTimer()
            showEndDialog()
            return "00:00"
        }
        val m = t / 60 % 60
        val s = t % 60 % 60
        var time = if (m > 9) {
            "$m:"
        } else {
            "0$m:"
        }
        time += if (s > 9) {
            "$s"
        } else {
            "0$s"
        }
        return time
    }

    /**
     * 停止计时
     */
    fun stopTimer() {
        timer?.purge()
        timer?.cancel()
        timer = null
    }

    /**
     * 开始匹配，需要经自己的信息提交到后端
     */
    private fun startAnim() {
        val radarOptions = VMAnimator.AnimOptions(mBinding.imChatFastPaperCraneIV, floatArrayOf(-15f, 15f), VMAnimator.rotation, 2000)
        mAnimatorWrapRadar = VMAnimator.createAnimator().play(radarOptions)
        mAnimatorWrapRadar?.start()

        val scaleXOptions = VMAnimator.AnimOptions(mBinding.imChatFastAnimView1, floatArrayOf(0f, 3f), VMAnimator.scaleX, 2000, repeatMode = 1)
        val scaleYOptions = VMAnimator.AnimOptions(mBinding.imChatFastAnimView1, floatArrayOf(0f, 3f), VMAnimator.scaleY, 2000, repeatMode = 1)
        val alphaOptions = VMAnimator.AnimOptions(mBinding.imChatFastAnimView1, floatArrayOf(1.0f, 0.0f), VMAnimator.alpha, 2000, repeatMode = 1)
        mAnimatorWrap = VMAnimator.createAnimator().play(scaleXOptions).with(scaleYOptions).with(alphaOptions)
        mAnimatorWrap?.start()

        val scaleXOptions2 = VMAnimator.AnimOptions(mBinding.imChatFastAnimView2, floatArrayOf(0f, 3f), VMAnimator.scaleX, 2000, repeatMode = 1)
        val scaleYOptions2 = VMAnimator.AnimOptions(mBinding.imChatFastAnimView2, floatArrayOf(0f, 3f), VMAnimator.scaleY, 2000, repeatMode = 1)
        val alphaOptions2 = VMAnimator.AnimOptions(mBinding.imChatFastAnimView2, floatArrayOf(1.0f, 0.0f), VMAnimator.alpha, 2000, repeatMode = 1)
        mAnimatorWrap2 = VMAnimator.createAnimator().play(scaleXOptions2).with(scaleYOptions2).with(alphaOptions2)
        mAnimatorWrap2?.start(delay = 1000)
    }

    private fun stopAnim() {
        mAnimatorWrapRadar?.cancel()
        mAnimatorWrap?.cancel()
        mAnimatorWrap2?.cancel()
    }
}