package com.vmloft.develop.library.common.utils

import com.vmloft.develop.library.tools.utils.VMDate

/**
 * Create by lzan13 on 2020-02-15 19:29
 * 描述：格式化工具类
 */
object FormatUtils {
    /**
     * 格式化时间
     */
    @JvmStatic
    fun relativeTime(time: Long): String {
        return VMDate.getRelativeTime(time) ?: ""
    }

    /**
     * 格式化时间
     */
    @JvmStatic
    fun relativeTime(time: String): String {
        return VMDate.getRelativeTime(VMDate.milliFormUTC(time)) ?: ""
    }

    /**
     * 格式化未读数
     */
    @JvmStatic
    fun wrapUnread(unread: Int): String {
        return when {
            unread == 0 -> ""
            unread > 99 -> "+" + 99
            else -> "" + unread
        }
    }
}