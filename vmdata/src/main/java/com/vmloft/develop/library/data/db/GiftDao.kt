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

    /**
     * 删除指定数据
     */
    @Delete
    suspend fun delete(gift: Gift)

    /**
     * 清空数据
     */
    @Query("DELETE FROM gift")
    suspend fun clear()

//    @Update
//    suspend fun update(gift: Gift)

    /**
     * 查询指定数据
     */
    @Query("SELECT * FROM gift WHERE id = :id")
    suspend fun query(id: String): Gift?

    /**
     * 查询全部数据
     */
    @Query("SELECT *  FROM gift")
    suspend fun all(): List<Gift>
}