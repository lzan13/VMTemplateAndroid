package com.vmloft.develop.library.data.db

import androidx.room.*

import com.vmloft.develop.library.data.bean.Profession

/**
 * Create by lzan13 on 2020/8/9 14:41
 * 描述：职业数据 Dao
 */
@Dao
interface ProfessionDao {
    /**
     * 插入数据，如果已存在则会覆盖
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg profession: Profession)

    @Delete
    suspend fun delete(profession: Profession)

    @Query("DELETE FROM profession")
    suspend fun delete()

//    @Update
//    suspend fun update(profession: Profession)

    @Query("SELECT * FROM profession WHERE id = :id")
    suspend fun query(id: String): Profession?

    @Query("SELECT *  FROM profession")
    suspend fun all(): List<Profession>
}