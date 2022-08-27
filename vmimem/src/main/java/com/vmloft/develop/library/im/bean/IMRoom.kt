package com.vmloft.develop.library.im.bean


/**
 * Create by lzan13 on 2019/5/23 09:50
 *
 * 描述：定义 IM 内部房间实体类，用来获取头像昵称等简单属性进行展示
 */
data class IMRoom(
    var id: String, // id
    var owner: IMUser, // 所有者
    var title: String = "", // 标题
    var desc: String = "", // 描述
    var count: Int = 1, // 人数
) {

}