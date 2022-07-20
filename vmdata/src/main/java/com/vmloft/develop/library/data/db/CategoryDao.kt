package com.vmloft.develop.library.data.db

import androidx.room.*

import com.vmloft.develop.library.data.bean.Category

/**
 * Create by lzan13 on 2020/8/9 14:41
 * 描述：分类数据 Dao
 */
@Dao
interface CategoryDao {
    /**
     * 插入数据，如果已存在则会覆盖
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("DELETE FROM category")
    suspend fun delete()

//    @Update
//    suspend fun update(category: Category)

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun query(id: String): Category?

    @Query("SELECT *  FROM category")
    suspend fun all(): List<Category>
}