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

    @Delete
    suspend fun delete(config: Config)

    @Query("DELETE FROM config")
    suspend fun delete()

//    @Update
//    suspend fun update(match: Config)

    @Query("SELECT * FROM config WHERE alias = :alias")
    suspend fun query(alias: String): Config?

    @Query("SELECT *  FROM config")
    suspend fun all(): List<Config>
}