package com.vmloft.develop.app.template.request.db

import androidx.room.TypeConverter
import com.vmloft.develop.app.template.request.bean.Attachment
import com.vmloft.develop.app.template.request.bean.Category
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.library.common.utils.JsonUtils

/**
 * Create by lzan13 on 2021/7/11
 * 描述：匹配数据转换器，主要是为了解决 Room 保存嵌套对象内部数据问题
 */
class DataConverter {
    // 附件
    @TypeConverter
    fun attachmentsToJson(list: List<Attachment>): String {
        return JsonUtils.toJson(list)
    }

    @TypeConverter
    fun jsonToAttachmentList(json: String): List<Attachment>? {
        return JsonUtils.fromJson<List<Attachment>>(json)
    }

    // 分类
    @TypeConverter
    fun categoryToJson(data: Category): String {
        return JsonUtils.toJson(data)
    }

    @TypeConverter
    fun jsonToCategory(json: String): Category? {
        return JsonUtils.fromJson(json, Category::class.java)
    }

    @TypeConverter
    fun userToJson(data: User): String {
        return JsonUtils.toJson(data)
    }

    @TypeConverter
    fun jsonToUser(json: String): User? {
        return JsonUtils.fromJson(json, User::class.java)
    }

}