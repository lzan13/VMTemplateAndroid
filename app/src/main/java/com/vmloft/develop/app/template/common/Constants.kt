package com.vmloft.develop.app.template.common

/**
 * Create by lzan13 on 2020-02-18 14:43
 * 常量类
 */
object Constants {

    const val dbName = "wcdb_vmnepenthe"
    const val dbPass = "wcdb_vmnepenthe_lzan13"

    // App 层事件总线 Key
    object Event {
        const val userInfo = "appUserInfo" // 用户信息改变事件
        const val matchInfo = "appMatchInfo" // 匹配信息改变事件
        const val createPost = "appCreatePost" // 创建帖子
        const val createComment = "appCreateComment" // 创建评论
        const val shieldPost = "appShieldPost" // 屏蔽帖子
        const val orderStatus = "appOrderStatus" // 订单状态更新
        const val goldTaskReward = "appGoldTaskReward" // 金币任务奖励
    }

    /**
     * 反馈类型
     */
    object FeedbackType {
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