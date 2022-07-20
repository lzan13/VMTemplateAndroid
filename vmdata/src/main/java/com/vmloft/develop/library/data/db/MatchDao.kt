package com.vmloft.develop.library.data.db

import androidx.room.*

import com.vmloft.develop.library.data.bean.Match

/**
 * Create by lzan13 on 2021/7/3 14:41
 * 描述：匹配数据数据 Dao，这里主要是缓存自己的匹配数据
 */
@Dao
interface MatchDao {
    /**
     * 插入数据，如果已存在则会覆盖
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg match: Match)

    @Delete
    suspend fun delete(match: Match)

    @Query("DELETE FROM match")
    suspend fun delete()

//    @Update
//    suspend fun update(match: Match)

    @Query("SELECT * FROM match WHERE id = :id")
    suspend fun query(id: String): Match?

    @Query("SELECT *  FROM match")
    suspend fun all(): List<Match>
}