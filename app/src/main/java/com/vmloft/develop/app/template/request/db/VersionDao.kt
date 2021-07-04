package com.vmloft.develop.app.template.request.db

import androidx.room.*
import com.vmloft.develop.app.template.request.bean.Category
import com.vmloft.develop.app.template.request.bean.Version

/**
 * Create by lzan13 on 2021/7/3 14:41
 * 描述：版本检查数据 Dao
 */
@Dao
interface VersionDao {

    @Insert
    suspend fun insert(vararg version: Version)

    @Delete
    suspend fun delete(version: Version)

    @Query("DELETE FROM version")
    fun delete()

    @Update
    suspend fun update(version: Version)

    @Query("SELECT * FROM version WHERE id = :id")
    suspend fun query(id: String): Version?

    @Query("SELECT *  FROM version")
    suspend fun all(): List<Version>
}