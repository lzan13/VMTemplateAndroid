package com.vmloft.develop.app.template.ui.main.msg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.FragmentMsgBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BFragment
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.im.conversation.IMConversationFragment
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.widget.VMFloatMenu


/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：消息
 */
class MsgFragment : BFragment<FragmentMsgBinding>() {

    // 弹出菜单
    private lateinit var floatMenu: VMFloatMenu
    private var viewX: Int = 0
    private var viewY: Int = 0

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = FragmentMsgBinding.inflate(inflater, parent, false)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.nav_msg)
//        setTopEndIcon(R.drawable.ic_add) { showFloatMenu(it) }

        setupFragment()
        initFloatMenu()
    }

    override fun initData() {
        viewX = VMDimen.screenWidth

    }

    private fun setupFragment() {
        val fragment = IMConversationFragment.newInstance()
        val manager: FragmentManager = childFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.replace(R.id.conversation_container, fragment)
        ft.commit()
    }

    /**
     * 加载心情 View
     */
    private fun initFloatMenu() {
        floatMenu = VMFloatMenu(requireActivity())
        floatMenu.setItemClickListener(object : VMFloatMenu.IItemClickListener() {
            override fun onItemClick(id: Int) {
                when (id) {
                    0 -> CRouter.go(AppRouter.appQRScan)
                    else -> {
                    }
                }
            }
        })
    }

    /**
     * 弹出菜单
     */
    private fun showFloatMenu(view: View) {
        floatMenu.clearAllItem()
        floatMenu.addItem(VMFloatMenu.ItemBean(0, "扫一扫", itemIcon = R.drawable.ic_scan))

        floatMenu.showAtLocation(view, viewX, viewY)
    }

}