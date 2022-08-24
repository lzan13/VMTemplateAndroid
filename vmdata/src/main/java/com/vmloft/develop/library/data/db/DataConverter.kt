package com.vmloft.develop.library.data.db

import androidx.room.TypeConverter

import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.data.bean.Attachment
import com.vmloft.develop.library.data.bean.Category
import com.vmloft.develop.library.data.bean.User

/**
 * Create by lzan13 on 2021/7/11
 * 描述：数据转换器，主要是为了解决 Room 保存嵌套对象内部数据问题
 */
class DataConverter {

    // 分类
    @TypeConverter
    fun categoryToJson(data: Category): String {
        return JsonUtils.toJson(data)
    }

    @TypeConverter
    fun jsonToCategory(json: String): Category? {
        return JsonUtils.fromJson(json, Category::class.java)
    }

    // 附件
    @TypeConverter
    fun attachmentsToJson(list: List<Attachment>): String {
        return JsonUtils.toJson(list)
    }

    @TypeConverter
    fun jsonToAttachmentList(json: String): List<Attachment>? {
        return JsonUtils.fromJson<List<Attachment>>(json)
    }

    // 礼物中的附件
    @TypeConverter
    fun attachmentToJson(data: Attachment): String {
        return JsonUtils.toJson(data)
    }

    @TypeConverter
    fun jsonToAttachment(json: String): Attachment? {
        return JsonUtils.fromJson(json, Attachment::class.java)
    }

    // 用户
    @TypeConverter
    fun userToJson(data: User): String {
        return JsonUtils.toJson(data)
    }

    @TypeConverter
    fun jsonToUser(json: String): User? {
        return JsonUtils.fromJson(json, User::class.java)
    }

}