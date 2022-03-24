package com.vmloft.develop.library.im.chat

import android.content.Intent
import android.os.Handler
import android.os.Message
import android.view.View

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.hyphenate.chat.EMMessage

import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMUser
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImActivityChatBinding
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.widget.VMFloatMenu

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

    // 弹出菜单
    private lateinit var floatMenu: VMFloatMenu
    private var viewX: Int = 0
    private var viewY: Int = 0

    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            setTopSubTitle("")
        }
    }

    override fun initVB() = ImActivityChatBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()

        setTopEndIcon(R.drawable.ic_more) { showMenu(it) }

        initMenu()

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

        viewX = VMDimen.screenWidth - VMDimen.dp2px(24)
        viewY = VMDimen.dp2px(56)

        mUser = IM.imListener.getUser(chatId) ?: IMUser(chatId)

        setTopTitleColor(if (mUser.identity in 100..199 && ConfigManager.clientConfig.vipEntry) R.color.app_identity_vip else R.color.app_title)
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
     * 初始化悬浮菜单
     */
    private fun initMenu() {
        floatMenu = VMFloatMenu(this)
        floatMenu.setItemClickListener(object : VMFloatMenu.IItemClickListener() {
            override fun onItemClick(id: Int) {
                when (id) {
                    0 -> IM.imListener.onHeadClick(chatId)
                    1 -> IMChatManager.clearConversation(chatId)
                }
            }
        })
    }

    /**
     * 弹出菜单
     */
    private fun showMenu(view: View) {
        floatMenu.clearAllItem()
        floatMenu.addItem(VMFloatMenu.ItemBean(0, VMStr.byRes(R.string.im_chat_menu_user), itemIcon = R.drawable.ic_mine_line))
//        floatMenu.addItem(VMFloatMenu.ItemBean(1, VMStr.byRes(R.string.im_chat_menu_clear), itemIcon = R.drawable.ic_clear))

        floatMenu.showAtLocation(view, viewX, viewY)
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