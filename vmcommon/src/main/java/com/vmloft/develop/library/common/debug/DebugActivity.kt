package com.vmloft.develop.library.common.debug

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.library.common.R
import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.common.databinding.ActivityDebugBinding
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.tools.utils.VMStr

/**
 * Create by lzan13 on 2020/05/02 22:56
 * 描述：调试设置
 */
@Route(path = CRouter.commonDebug)
class DebugActivity : BActivity<ActivityDebugBinding>() {

    override fun initVB() = ActivityDebugBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.settings_debug)

        mBinding.debugEnvLV.setOnClickListener {
            showRebootDialog()
        }

    }

    override fun initData() {
        mBinding.debugEnvLV.setCaption(VMStr.byRes(if (CSPManager.isDebug()) R.string.debug_env_debug else R.string.debug_env_release))
    }

    private fun showRebootDialog() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.backDismissSwitch = false
            dialog.touchDismissSwitch = false
            dialog.setContent(R.string.debug_status_change_hint)
            dialog.setPositive(listener = {
                CSPManager.setDebug(!CSPManager.isDebug())
                mBinding.debugEnvLV.setCaption(VMStr.byRes(if (CSPManager.isDebug()) R.string.debug_env_debug else R.string.debug_env_release))
                CRouter.goMain(1)

            })
            dialog.show()
        }
    }

}