package com.vmloft.develop.app.template.im

import com.hyphenate.chat.EMMessage

import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.im.IIMListener

/**
 * Create by lzan13 on 2019/5/23 09:57
 *
 * 描述：实现 IM 全局回调接口
 */
class IMListener : IIMListener {

    /**
     * 联系人头像点击
     *
     * @param id 用户 id
     */
    override fun onHeadClick(id: String) {
        val user = CacheManager.getUser(id)
        CRouter.go(AppRouter.appUserInfo, obj0 = user)
    }

    override fun getMsgType(msg: EMMessage): Int {
        return 0
    }

    override fun getMsgSummary(msg: EMMessage): String {
        return ""
    }
}