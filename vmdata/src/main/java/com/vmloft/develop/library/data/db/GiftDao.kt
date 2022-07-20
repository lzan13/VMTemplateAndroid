package com.vmloft.develop.library.data.db

import androidx.room.*

import com.vmloft.develop.library.data.bean.Gift

/**
 * Create by lzan13 on 2020/8/9 14:41
 * 描述：职业数据 Dao
 */
@Dao
interface GiftDao {
    /**
     * 插入数据，如果已存在则会覆盖
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg gift: Gift)

    @Delete
    suspend fun delete(gift: Gift)

    @Query("DELETE FROM gift")
    suspend fun delete()

//    @Update
//    suspend fun update(gift: Gift)

    @Query("SELECT * FROM gift WHERE id = :id")
    suspend fun query(id: String): Gift?

    @Query("SELECT *  FROM gift")
    suspend fun all(): List<Gift>
}