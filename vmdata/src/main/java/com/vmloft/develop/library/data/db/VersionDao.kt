package com.vmloft.develop.library.data.db

import androidx.room.*

import com.vmloft.develop.library.data.bean.Version

/**
 * Create by lzan13 on 2021/7/3 14:41
 * 描述：版本检查数据 Dao
 */
@Dao
interface VersionDao {
    /**
     * 插入数据，如果已存在则会覆盖
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg version: Version)

    /**
     * 删除指定数据
     */
    @Delete
    suspend fun delete(version: Version)

    /**
     * 清空数据
     */
    @Query("DELETE FROM version")
    suspend fun clear()

    /**
     * 更新数据
     */
//    @Update
//    suspend fun update(version: Version)

    /**
     * 查询指定数据
     */
    @Query("SELECT * FROM version WHERE id = :id")
    suspend fun query(id: String): Version?

    /**
     * 查询全部数据
     */
    @Query("SELECT *  FROM version")
    suspend fun all(): List<Version>
}