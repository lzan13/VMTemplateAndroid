package com.vmloft.develop.library.base.notify

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.vmloft.develop.library.base.BActivity
import com.vmloft.develop.library.base.databinding.ActivityNotifyBinding
import com.vmloft.develop.library.base.router.CRouter


/**
 * Create by lzan13 2022/03/28
 * 描述：通知栏提醒中间页，做页面中转用
 */
class NotifyActivity : BActivity<ActivityNotifyBinding>() {

    override fun initVB() = ActivityNotifyBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
    }

    override fun initData() {
        val bundle = intent.getBundleExtra("params") ?: return CRouter.goMain()

        checkBName(bundle)
    }

    private fun checkBName(bundle: Bundle) {
        val bname = bundle.getString("bname")
        if (bname == "im") {
            val chatId = bundle.getString("chatId") ?: ""
            val extend = bundle.getString("extend") ?: ""
            CRouter.go("/IM/Chat", 0, str0 = chatId, str1 = extend, flags = Intent.FLAG_ACTIVITY_NEW_TASK)
        } else {
            CRouter.goMain()
        }
    }

}