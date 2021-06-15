package com.vmloft.develop.library.common.request

/**
 * Create by lzan13 on 2020/02/13 18:56
 * 描述：网络请求分页结果实体类
 */
data class RPaging<out T>(
    val currentCount: Int,
    val totalCount: Int,
    val page: Int,
    val limit: Int,
    val data: List<T>
)
