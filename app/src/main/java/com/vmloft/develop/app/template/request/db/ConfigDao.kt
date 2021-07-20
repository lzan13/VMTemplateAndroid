package com.vmloft.develop.app.template.request.db

import androidx.room.*
import com.vmloft.develop.app.template.request.bean.Config

/**
 * Create by lzan13 on 2021/7/3 14:41
 * 描述：配置数据数据 Dao
 */
@Dao
interface ConfigDao {

    @Insert
    suspend fun insert(vararg config: Config)

    @Delete
    suspend fun delete(config: Config)

    @Query("DELETE FROM config")
    fun delete()

    @Update
    suspend fun update(match: Config)

    @Query("SELECT * FROM config WHERE alias = :alias")
    suspend fun query(alias: String): Config?

    @Query("SELECT *  FROM config")
    suspend fun all(): List<Config>
}