package com.vmloft.develop.app.template.ui.main.msg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.CacheManager
import com.vmloft.develop.app.template.databinding.FragmentMsgBinding
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BFragment
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.im.conversation.IMConversationFragment
import com.vmloft.develop.library.qr.QRCodeScanLauncher
import com.vmloft.develop.library.qr.QRHelper
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.widget.VMFloatMenu


/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：消息
 */
class MsgFragment : BFragment<FragmentMsgBinding>() {

    private lateinit var launcher: QRCodeScanLauncher

    // 弹出菜单
    private lateinit var floatMenu: VMFloatMenu
    private var viewX: Int = 0
    private var viewY: Int = 0

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = FragmentMsgBinding.inflate(inflater, parent, false)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.nav_msg)
        setTopEndIcon(R.drawable.ic_add) { showMenu(it) }

        setupFragment()
        initMenu()

        launcher = QRCodeScanLauncher(this)
    }

    override fun initData() {
        viewX = VMDimen.screenWidth - VMDimen.dp2px(24)
        viewY = VMDimen.dp2px(56)
    }

    private fun setupFragment() {
        val fragment = IMConversationFragment.newInstance()
        val manager: FragmentManager = childFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.replace(R.id.conversation_container, fragment)
        ft.commit()
    }

    /**
     * 初始化悬浮菜单
     */
    private fun initMenu() {
        floatMenu = VMFloatMenu(requireActivity())
        floatMenu.setItemClickListener(object : VMFloatMenu.IItemClickListener() {
            override fun onItemClick(id: Int) {
                when (id) {
                    0 -> openScanQRCode()
                }
            }
        })
    }

    /**
     * 弹出菜单
     */
    private fun showMenu(view: View) {
        floatMenu.clearAllItem()
        floatMenu.addItem(VMFloatMenu.ItemBean(0, VMStr.byRes(R.string.scan), itemIcon = R.drawable.ic_scan))

        floatMenu.showAtLocation(view, viewX, viewY)
    }

    /**
     *
     */
    private fun openScanQRCode() {
        launcher.launch(0) {
            val bean = QRHelper.decodeQRCodeResult(it)
            bean?.let {
                showBar("${bean.type} ${bean.content}")
                if (bean.type == 0) {
                    val user = CacheManager.getUser(bean.content) ?: User(bean.content)
                    CRouter.go(AppRouter.appUserInfo, obj0 = user)
                }
            }
        }
    }
}