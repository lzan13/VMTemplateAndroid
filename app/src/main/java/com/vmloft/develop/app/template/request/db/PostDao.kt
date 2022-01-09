package com.vmloft.develop.app.template.request.db

import androidx.room.*
import com.vmloft.develop.app.template.request.bean.Post

/**
 * Create by lzan13 on 2020/11/22
 * 描述：帖子数据 Dao
 */
@Dao
interface PostDao {
    /**
     * 插入数据，如果已存在则忽略，这里为了防止屏蔽的数据被覆盖
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg post: Post)

    @Delete
    suspend fun delete(post: Post)

    @Query("DELETE FROM post")
    suspend fun delete()

    @Update
    suspend fun update(post: Post)

    @Query("SELECT * FROM post WHERE id = :id")
    suspend fun query(id: String): Post?

    @Query("SELECT * FROM post WHERE isShielded = :isShielded")
    suspend fun query(isShielded: Boolean): List<Post>

    @Query("SELECT *  FROM post")
    suspend fun all(): List<Post>
}