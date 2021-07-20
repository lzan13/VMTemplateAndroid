package com.vmloft.develop.app.template.request.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.vmloft.develop.app.template.request.bean.User

/**
 * Create by lzan13 on 2021/7/11
 * 描述：匹配数据转换器
 */
class MatchConverter {

    @TypeConverter
    fun userToJson(user: User): String {
        return Gson().toJson(user)
    }

    @TypeConverter
    fun jsonToUser(json: String): User {
        return Gson().fromJson(json, User::class.java)
    }
}