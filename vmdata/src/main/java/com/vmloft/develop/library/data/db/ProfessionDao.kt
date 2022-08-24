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

    /**
     * 删除指定数据
     */
    @Delete
    suspend fun delete(profession: Profession)

    /**
     * 清空数据库
     */
    @Query("DELETE FROM profession")
    suspend fun clear()

    /**
     * 更新数据
     */
//    @Update
//    suspend fun update(profession: Profession)

    /**
     * 查询指定数据
     */
    @Query("SELECT * FROM profession WHERE id = :id")
    suspend fun query(id: String): Profession?

    /**
     * 查询全部数据
     */
    @Query("SELECT *  FROM profession")
    suspend fun all(): List<Profession>
}