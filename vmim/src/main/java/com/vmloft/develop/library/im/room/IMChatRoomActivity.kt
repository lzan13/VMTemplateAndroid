package com.vmloft.develop.library.im.room

import android.content.Intent

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.vmloft.develop.library.common.base.BActivity
import com.vmloft.develop.library.common.ui.widget.CommonDialog
import com.vmloft.develop.library.im.IM
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMRoom
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.im.databinding.ImActivityChatRoomBinding
import com.vmloft.develop.library.im.router.IMRouter
import com.vmloft.develop.library.tools.utils.VMSystem

/**
 * Create on lzan13 on 2021/05/09 10:10
 *
 * 聊天房间界面
 */
@Route(path = IMRouter.imChatRoom)
class IMChatRoomActivity : BActivity<ImActivityChatRoomBinding>() {

    @Autowired
    lateinit var chatId: String

    @JvmField
    @Autowired
    var chatType: Int = IMConstants.ChatType.imChatRoom

    lateinit var mRoom: IMRoom

    lateinit var chatFragment: IMChatRoomFragment
    lateinit var roomFragment: IMRoomCallFragment

    override var isHideTopSpace = true

    override fun initVB()=ImActivityChatRoomBinding.inflate(layoutInflater)

    override fun initData() {
        ARouter.getInstance().inject(this)

        mRoom = IM.imListener.getRoom(chatId)
        setTopTitle(mRoom.title)

        // 加入聊天室
        IMChatRoomManager.joinRoom(chatId) { code ->
            VMSystem.runInUIThread({
                if (code != 0) {
                    notRoom()
                } else {
                    setupFragment()
                }
            })
        }
    }

    /**
     * 加载聊天界面
     */
    private fun setupFragment() {
        chatFragment = IMChatRoomFragment.newInstance(chatId, chatType)
        roomFragment = IMRoomCallFragment.newInstance(chatId)

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.imRoomContainer, roomFragment)
        ft.replace(R.id.imChatContainer, chatFragment)
        ft.commit()
    }

    /**
     * 房间已销毁
     */
    private fun notRoom() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.backDismissSwitch = false
            dialog.touchDismissSwitch = false
            dialog.setContent(R.string.im_room_not_exist_hint)
            dialog.setNegative("")
            dialog.setPositive(listener = {
                // 聊天室销毁处理
                IMChatRoomManager.roomDestroyed(chatId)
                finish()
            })
            dialog.show()
        }
    }

    /**
     * 退出房间
     */
    private fun exitRoom() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.im_room_exit_hint)
            dialog.setPositive(listener = {
                // 退出聊天室
                IMChatRoomManager.exitRoom(chatId)
                finish()
            })
            dialog.show()
        }
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

    override fun onBackPressed() {
        exitRoom()
    }
}