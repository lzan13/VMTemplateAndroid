package com.vmloft.develop.library.base.utils

import com.vmloft.develop.library.tools.utils.VMDate

/**
 * Create by lzan13 on 2020-02-15 19:29
 * 描述：格式化工具类
 */
object FormatUtils {
    /**
     * 相对时间
     */
    fun relativeTime(time: Long): String {
        return VMDate.getRelativeTime(time)
    }

    /**
     * 默认样式日期时间
     */
    fun defaultTime(time: Long): String {
        return VMDate.long2Str(time)
    }

    /**
     * 单时间样式时间
     */
    fun onlyTime(time: Long): String {
        return VMDate.long2Time(time)
    }

    /**
     * 格式化未读数
     */
    fun wrapUnread(unread: Int): String {
        return when {
            unread == 0 -> ""
            unread > 99 -> "+" + 99
            else -> "" + unread
        }
    }

    /**
     * 格式化大数据
     */
    fun wrapBig(count: Int): String {
        return when {
            count == 0 -> ""
            count > 9999 -> "${count / 10000}w+"
            else -> count.toString()
        }
    }
}