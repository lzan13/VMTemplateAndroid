package com.vmloft.develop.app.template.request.db

import androidx.room.*
import com.vmloft.develop.app.template.request.bean.Category

/**
 * Create by lzan13 on 2020/8/9 14:41
 * 描述：分类数据 Dao
 */
@Dao
interface CategoryDao {

    @Insert
    suspend fun insert(vararg category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("DELETE FROM category")
    fun delete()

    @Update
    suspend fun update(category: Category)

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun query(id: String): Category?

    @Query("SELECT *  FROM category")
    suspend fun all(): List<Category>
}