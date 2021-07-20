package com.vmloft.develop.app.template.request.db

import androidx.room.*
import com.vmloft.develop.app.template.request.bean.Category
import com.vmloft.develop.app.template.request.bean.Match

/**
 * Create by lzan13 on 2021/7/3 14:41
 * 描述：匹配数据数据 Dao，这里主要是缓存自己的匹配数据
 */
@Dao
interface MatchDao {

    @Insert
    suspend fun insert(vararg match: Match)

    @Delete
    suspend fun delete(match: Match)

    @Query("DELETE FROM match")
    fun delete()

    @Update
    suspend fun update(match: Match)

    @Query("SELECT * FROM match WHERE id = :id")
    suspend fun query(id: String): Match?

    @Query("SELECT *  FROM match")
    suspend fun all(): List<Match>
}