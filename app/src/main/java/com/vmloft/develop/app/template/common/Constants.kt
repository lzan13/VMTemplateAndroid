package com.vmloft.develop.app.template.common

/**
 * Create by lzan13 on 2020-02-18 14:43
 * 常量类
 */
object Constants {

    // App 层事件总线 Key
    object Event {
        const val finishPrev = "finishPrev" // 结束上一个界面
        const val createPost = "createPost" // 创建帖子
        const val createComment = "createComment" // 创建评论
        const val shieldPost = "shieldPost" // 屏蔽帖子
        const val orderStatus = "orderStatus" // 订单状态更新
        const val videoReward = "videoReward" // 金币任务奖励
    }

    /**
     * 反馈类型
     */
    object FeedbackType {
        const val opinion = 0  // 意见建议
        const val ads = 1  // 广告引流
        const val sensitivity = 2  // 政治敏感
        const val illegal = 3  // 违法违规
        const val pornVulgar = 4  // 色情低俗
        const val violence = 5  // 血腥暴力
        const val guide = 6  // 诱导信息
        const val uncivilized = 7  // 谩骂攻击
        const val fraud = 8  // 涉嫌诈骗
        const val uncomfortable = 9  // 引人不适
        const val other = 10 // 其它
    }
}