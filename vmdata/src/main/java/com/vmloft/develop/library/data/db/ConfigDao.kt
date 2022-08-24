package com.vmloft.develop.library.data.db

import androidx.room.*

import com.vmloft.develop.library.data.bean.Config

/**
 * Create by lzan13 on 2021/7/3 14:41
 * 描述：配置数据数据 Dao
 */
@Dao
interface ConfigDao {
    /**
     * 插入数据，如果已存在则会覆盖
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg config: Config)

    /**
     * 删除指定数据
     */
    @Delete
    suspend fun delete(config: Config)

    /**
     * 清空数据
     */
    @Query("DELETE FROM config")
    suspend fun clear()

    /**
     * 更新数据
     */
//    @Update
//    suspend fun update(match: Config)

    /**
     * 查询指定数据
     */
    @Query("SELECT * FROM config WHERE alias = :alias")
    suspend fun query(alias: String): Config?

    /**
     * 查询全部数据
     */
    @Query("SELECT *  FROM config")
    suspend fun all(): List<Config>
}