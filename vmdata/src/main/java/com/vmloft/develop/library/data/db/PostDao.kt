package com.vmloft.develop.library.data.db

import androidx.room.*

import com.vmloft.develop.library.data.bean.Post

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

    /**
     * 删除指定数据
     */
    @Delete
    suspend fun delete(post: Post)

    /**
     * 清空数据
     */
    @Query("DELETE FROM post")
    suspend fun clear()

    /**
     * 删除非屏蔽数据
     */
    @Query("DELETE FROM post WHERE isShielded = :isShielded")
    suspend fun clear(isShielded: Boolean = false)

    /**
     * 更新数据
     */
    @Update
    suspend fun update(post: Post)

    /**
     * 查询指定数据
     */
    @Query("SELECT * FROM post WHERE id = :id")
    suspend fun query(id: String): Post?

    /**
     * 查询屏蔽的数据
     */
    @Query("SELECT * FROM post WHERE isShielded = :isShielded limit :limit")
    suspend fun queryShielded(isShielded: Boolean = true, limit: Int = 20): List<Post>

    /**
     * 获取本地缓存的全部非屏蔽数据
     */
    @Query("SELECT *  FROM post WHERE isShielded = :isShielded limit :limit")
    suspend fun all(limit: Int = 20, isShielded: Boolean = false): List<Post>
}