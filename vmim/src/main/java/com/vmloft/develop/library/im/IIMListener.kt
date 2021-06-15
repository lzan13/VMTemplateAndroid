package com.vmloft.develop.library.im

import com.hyphenate.chat.EMMessage
import com.vmloft.develop.library.im.bean.IMRoom
import com.vmloft.develop.library.im.bean.IMUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Create by lzan13 on 2021/5/21 09:47
 *
 * 描述：定义 IM 全局回调接口
 */
interface IIMListener {
    /**
     * 同步获取 IM 联系人信息
     *
     * @param id 联系人 Id
     */
    fun getUser(id: String): IMUser?

    /**
     * 异步获取 IM 联系人信息
     *
     * @param id 联系人 Id
     */
    fun getUser(id: String, callback: (IMUser) -> Unit = { })

    /**
     * 获取指定 id 集合的用户信息
     */
    fun getUserList(ids: List<String>, callback: () -> Unit)

    /**
     * 获取房间信息
     */
    fun getRoom(id: String): IMRoom

    /**
     * 离开房间
     */
    fun exitRoom(id: String)

    /**
     * 联系人头像点击
     *
     * @param id 联系人 Id
     */
    fun onHeadClick(id: String)

    /**
     * 获取消息类型
     */
    fun getMsgType(msg: EMMessage): Int

    /**
     * 获取消息摘要
     */
    fun getMsgSummary(msg: EMMessage): String
}