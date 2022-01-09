package com.vmloft.develop.app.template.common

/**
 * Create by lzan13 on 2020-02-18 14:43
 * 常量类
 */
object Constants {

    const val dbName = "wcdb_vmmatch"
    const val dbPass = "wcdb_vmmatch_lzan13"

    // 用户信息改变事件
    const val userInfoEvent = "userInfoEvent"

    // 创建帖子
    const val createPostEvent = "createPostEvent"
    // 创建评论
    const val createCommentEvent = "createCommentEvent"

    /**
     * 反馈类型
     */
    object FeedbackType{
        const val opinion = 0
        const val ads = 1
        const val sensitivity = 2
        const val pornVulgar = 3
        const val violence = 4
        const val uncivilized = 5
        const val fraud = 6
        const val other = 7
    }
}