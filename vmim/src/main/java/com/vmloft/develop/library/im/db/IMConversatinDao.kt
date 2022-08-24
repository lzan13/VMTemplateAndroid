package com.vmloft.develop.library.im.db

import androidx.room.*

import com.vmloft.develop.library.im.bean.IMConversation

/**
 * Create by lzan13 on 2022/8/16 14:41
 * 描述：会话数据 Dao
 */
@Dao
interface IMConversatinDao {
    /**
     * 插入数据，如果已存在则会覆盖
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg conversation: IMConversation)

    /**
     * 删除指定数据
     */
    @Delete
    suspend fun delete(conversation: IMConversation)

    /**
     * 清空数据
     */
    @Query("DELETE FROM conversation")
    suspend fun delete()

    /**
     * 更新数据
     */
    @Update
    suspend fun update(vararg conversation: IMConversation)

    /**
     * 查询指定数据
     */
    @Query("SELECT * FROM conversation WHERE chatId = :chatId")
    suspend fun query(chatId: String): IMConversation?

    /**
     * 查询全部数据
     */
    @Query("SELECT *  FROM conversation")
    suspend fun all(): List<IMConversation>
}