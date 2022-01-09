package com.vmloft.develop.library.im.chat

import android.content.Intent
import android.os.Handler
import android.os.Message

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.common.base.BActivity
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMUser
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImActivityChatBinding
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create on lzan13 on 2021/05/09 10:10
 *
 * IM 默认提供的聊天界面，可直接打开使用
 */
@Route(path = IMRouter.imChat)
class IMChatActivity : BActivity<ImActivityChatBinding>() {

    @Autowired
    lateinit var chatId: String

    @JvmField
    @Autowired
    var chatType: Int = IMConstants.ChatType.imChatSingle

    @Autowired
    lateinit var chatExtend: String

    lateinit var mUser: IMUser

    lateinit var chatFragment: IMChatFragment

    override var isHideTopSpace = true

    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            setTopSubTitle("")
        }
    }

    override fun initVB() = ImActivityChatBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        // 监听新消息过来，这里主要是收到消息后重置下输入状态
        LDEventBus.observe(this, IMConstants.Common.newMsgEvent, EMMessage::class.java) {
            setTopSubTitle("")
        }

        // 监听输入状态 CMD 消息
        LDEventBus.observe(this, IMConstants.Common.cmdInputStatusAction, EMMessage::class.java) {
            setTopSubTitle(VMStr.byRes(R.string.im_chat_input_status))
            // 三秒后重置输入状态，需要移除上一次的 message
            handler.removeMessages(0)
            handler.sendEmptyMessageDelayed(0, 3000)
        }
    }

    override fun initData() {
        ARouter.getInstance().inject(this)

        mUser = IM.imListener.getUser(chatId) ?: IMUser(chatId)

        setTopTitle(if (mUser.nickname.isNullOrEmpty()) "小透明" else mUser.nickname)

        setupFragment()
    }

    /**
     * 加载聊天界面
     */
    private fun setupFragment() {
        chatFragment = IMChatFragment.newInstance(chatId, chatType, chatExtend)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.imChatContainer, chatFragment)
        ft.commit()
    }

    /**
     * 重写父类的onNewIntent方法，防止打开两个聊天界面
     *
     * @param intent 带有参数的intent
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        ARouter.getInstance().inject(this)
        setupFragment()
    }
}