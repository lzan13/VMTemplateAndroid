package com.vmloft.develop.library.im

import com.hyphenate.chat.EMMessage

/**
 * Create by lzan13 on 2021/5/21 09:47
 *
 * 描述：定义 IM 全局回调接口
 */
interface IIMListener {

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